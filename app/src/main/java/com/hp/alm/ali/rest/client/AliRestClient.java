/*
 * Copyright 2013 Hewlett-Packard Development Company, L.P
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.alm.ali.rest.client;

import com.hp.alm.ali.rest.client.exception.AuthenticationFailureException;
import com.hp.alm.ali.rest.client.exception.HttpStatusBasedException;
import com.hp.alm.ali.rest.client.filter.Filter;
import com.hp.alm.ali.rest.client.filter.IdentityFilter;
import com.hp.alm.ali.rest.client.filter.IssueTicketFilter;
import com.hp.alm.ali.rest.client.filter.ResponseFilter;
import com.hp.alm.ali.utils.XmlUtils;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.StatusLine;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import  org.apache.http.impl.client.DefaultHttpClient;
import  org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.methods.*;
import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.params.HttpParams;
//import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.*;
import  org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URI;
/*import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;*/

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.*;

import static com.hp.alm.ali.utils.PathUtils.pathJoin;

/**
 * Thin wrapper around commons-http client that provides basic support for communication with the HP ALM REST.
 * <p>
 *
 * No higher level abstractions are currently provided, this library only simplifies following tasks:
 *
 * <ul>
 *     <li>authentication; {@link #login()}, {@link #logout()}, {@link SessionStrategy}</li>
 *     <li>domain/project listing: {@link #listDomains()}, {@link #listCurrentProjects()}</li>
 * </ul>
 *
 * <h3>URL composition</h3>
 *
 * Position based expansion is used in methods that accept template with parameters:

 * <pre>
 *     client.getForString("defects/{0}/attachments/{1}", 1001, "readme.txt")
 * </pre>
 *
 * In the DOMAIN/PROJECT of http://localhost:8080/qcbin expands to:
 *
 * <pre>
 *     http://localost:8080/qcbin/domains/DOMAIN/projects/PROJECT/defects/1001/attachments/readme.txt
 * </pre>
 */
public class AliRestClient implements RestClient {

    private static final LocationBasedBuilder<HttpPost> POST_BUILDER = new LocationBasedBuilder<HttpPost>() {
        @Override
        public HttpPost build(String location) {
            return new HttpPost(location);
        }
    };
    private static final LocationBasedBuilder<HttpPut> PUT_BUILDER = new LocationBasedBuilder<HttpPut>() {
        @Override
        public HttpPut build(String location) {
            return new HttpPut(location);
        }
    };
    private static final LocationBasedBuilder<HttpGet> GET_BUILDER = new LocationBasedBuilder<HttpGet>() {
        @Override
        public HttpGet build(String location) {
            return new HttpGet(location);
        }
    };
    private static final LocationBasedBuilder<HttpDelete> DELETE_BUILDER = new LocationBasedBuilder<HttpDelete>() {
        @Override
        public HttpDelete build(String location) {
            return new HttpDelete(location);
        }
    };
    public static final Set<Integer> AUTH_FAIL_STATUSES = Collections.unmodifiableSet(new HashSet<Integer>(Arrays.asList(401, 403)));

    private LinkedList<ResponseFilter> responseFilters;

    /**
     * Creates ALM ALI rest client
     *
     * @param location location of an alm server e.g. http://localost:8080/qcbin
     * @param domain   ALM domain
     * @param project  ALM project
     * @param userName ALM user name
     * @param password ALM user password
     */
    public static AliRestClient create(String location, String domain, String project, String userName, String password, SessionStrategy sessionStrategy) {
        return new AliRestClient(location, domain, project, userName, password, sessionStrategy);
    }

    static final String COOKIE_SSO_NAME = "LWSSO_COOKIE_KEY";
    static final String COOKIE_SESSION_NAME = "QCSession";

    static final String CLIENT_TYPE = "ALI_IDEA_plugin";

    public static final int DEFAULT_CLIENT_TIMEOUT = 30000;

    private final String location;
    private final String userName;
    private final String password;
    private volatile String domain;
    private volatile String project;

    private final SessionStrategy sessionStrategy;
    private final DefaultHttpClient httpClient;
    private volatile SessionContext sessionContext = null;
    private volatile String encoding = "UTF-8";

    private AliRestClient(String location, String domain, String project,
                          String userName, String password,
                          SessionStrategy sessionStrategy) {
        if (location == null) {
            throw new IllegalArgumentException("location==null");
        }
        validateProjectAndDomain(domain, project);
        this.location = location;
        this.userName = userName;
        this.password = password;
        this.domain = domain;
        this.project = project;
        this.sessionStrategy = sessionStrategy;

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpProtocolParams.setUserAgent(params,"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");

        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        //httpCLient = new DefaultHttpClient();
        //httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schReg), new BasicHttpParams());
        //httpClient = new DefaultHttpClient(params);
        httpClient = new DefaultHttpClient();


        setTimeout(DEFAULT_CLIENT_TIMEOUT);



        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

        responseFilters = new LinkedList<ResponseFilter>();
        responseFilters.add(new IssueTicketFilter());
    }

    private void validateProjectAndDomain(String domain, String project) {
        if (domain == null && project != null) {
            throw new IllegalArgumentException("When project is specified domain must be specified too.");
        }
    }

    @Override
    public void setTimeout(int timeout) {
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), timeout);
/*        httpClient.getParams().setSoTimeout(timeout);
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);*/
    }

    @Override
    public void setHttpProxy(String proxyHost, int proxyPort) {
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }

    @Override
    public void setHttpProxyCredentials(String username, String password) {

        Credentials cred = new UsernamePasswordCredentials(username, password);
        AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
        httpClient.getCredentialsProvider().setCredentials(scope, cred);
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public SessionStrategy getSessionStrategy() {
        return sessionStrategy;
    }

    @Override
    public void setEncoding(String encoding) {
        if(encoding != null) {
            Charset.forName(encoding);
        }
        this.encoding = encoding;
    }

    @Override
    public void setDomain(String domain) {
        validateProjectAndDomain(domain, project);
        this.domain = domain;
    }

    @Override
    public void setProject(String project) {
        validateProjectAndDomain(domain, project);
        this.project = project;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getProject() {
        return project;
    }

    @Override
    public synchronized void login() {
        // exclude the NTLM authentication scheme (requires NTCredentials we don't supply)
        List<String> authPrefs = new ArrayList<String>(2);
        authPrefs.add(AuthPolicy.DIGEST);
        authPrefs.add(AuthPolicy.BASIC);
        httpClient.getParams().setParameter("http.auth.scheme-priority", authPrefs);

        // first try Apollo style login
        String authPoint = pathJoin("/", location, "/authentication-point/alm-authenticate");
        String authXml = createAuthXml();
        HttpPost post = initHttpPost(authPoint, authXml);
        ResultInfo resultInfo = ResultInfo.create(null);
        executeAndWriteResponse(post, resultInfo, Collections.<Integer>emptySet());

        if(resultInfo.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
            // try Maya style login
            Credentials cred = new UsernamePasswordCredentials(userName, password);
            AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
            httpClient.getParams().setParameter("http.protocol.credential-charset", "UTF-8");
            //todo?
            httpClient.getCredentialsProvider().setCredentials(scope, cred);

            authPoint = pathJoin("/", location, "/authentication-point/authenticate");
            HttpGet get = new HttpGet(authPoint);
            resultInfo = ResultInfo.create(null);
            executeAndWriteResponse(get, resultInfo, Collections.<Integer>emptySet());
        }
        HttpStatusBasedException.throwForError(resultInfo);
        if(resultInfo.getHttpStatus() != 200) {
            // during login we only accept 200 status (to avoid redirects and such as seemingly correct login)
            throw new AuthenticationFailureException(resultInfo);
        }

        List<Cookie> cookies = httpClient.getCookieStore().getCookies();
        Cookie ssoCookie = getSessionCookieByName(cookies, COOKIE_SSO_NAME);
        addTenantCookie(ssoCookie);

        //Since ALM 12.00 it is required explicitly ask for QCSession calling "/rest/site-session"
        //For all the rest of HP ALM / AGM versions it is optional
        String siteSessionPoint = pathJoin("/", location, "/rest/site-session");
        String sessionParamXml = createRestSessionXml();
        post = initHttpPost(siteSessionPoint, sessionParamXml);
        resultInfo = ResultInfo.create(null);
        executeAndWriteResponse(post, resultInfo, Collections.<Integer>emptySet());
        //AGM throws 403
        if (resultInfo.getHttpStatus() != HttpStatus.SC_FORBIDDEN) {
            HttpStatusBasedException.throwForError(resultInfo);
        }

        cookies = httpClient.getCookieStore().getCookies();
        Cookie qcCookie = getSessionCookieByName(cookies, COOKIE_SESSION_NAME);
        sessionContext = new SessionContext(location, ssoCookie, qcCookie);
    }

    private HttpPost initHttpPost(String restEndPoint, String xml) {
        HttpPost post = new HttpPost(restEndPoint);
        post.setEntity(createRequestEntity(InputData.create(xml)));
        post.addHeader("Content-type", "application/xml");

        return post;
    }

    private String createAuthXml() {
/*        Element authElem = new Element("alm-authentication");
        Element userElem = new Element("user");
        authElem.addContent(userElem);
        userElem.setText(userName);
        Element passElem = new Element("password");
        passElem.setText(password);
        authElem.addContent(passElem);
        return new XMLOutputter().outputString(authElem);*/

        /*XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startTag("", "alm-authentication");
            serializer.startTag("", "user");
            serializer.text(userName);
            serializer.endTag("", "user");
            serializer.startTag("", "password");
            serializer.text(password);
            serializer.endTag("", "password");
            serializer.endTag("", "alm-authentication");
            return writer.toString();

        } catch(Exception e) {
            throw new RuntimeException(e);
        }*/

        return "<alm-authentication><user>"+userName+"</user><password>"+password+"</password></alm-authentication>";


    }

    private String createRestSessionXml() {
/*        Element sessionParamElem = new Element("session-parameters");
        Element clientTypeElem = new Element("client-type");
        sessionParamElem.addContent(clientTypeElem);
        clientTypeElem.setText(CLIENT_TYPE);
        return new XMLOutputter().outputString(sessionParamElem);*/

        /*XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startTag("", "session-parameters");
            serializer.startTag("", "client-type");
            serializer.text(CLIENT_TYPE);
            serializer.endTag("", "client-type");
            serializer.endTag("", "session-parameters");
            return writer.toString();

        } catch(Exception e) {
            throw new RuntimeException(e);
        }*/

        return "<session-parameters><client-type>"+CLIENT_TYPE+"</client-type></session-parameters>";

    }

    private void addTenantCookie(Cookie ssoCookie) {
        if(ssoCookie != null) {

            BasicClientCookie tenant_id_cookie = new BasicClientCookie( "TENANT_ID_COOKIE", "0");
            tenant_id_cookie.setDomain(ssoCookie.getDomain());
            tenant_id_cookie.setPath(ssoCookie.getPath());
/*            tenant_id_cookie.setDomainAttributeSpecified(true);
            tenant_id_cookie.setPath(ssoCookie.getPath());
            tenant_id_cookie.setPathAttributeSpecified(true);*/
            httpClient.getCookieStore().addCookie(tenant_id_cookie);
        }
    }

    private Cookie getSessionCookieByName(List<Cookie> cookies, String name) {
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    @Override
    public synchronized void logout() {
        if(sessionContext != null) {
            HttpGet get = new HttpGet(pathJoin("/", location, "/authentication-point/logout"));
            ResultInfo resultInfo = ResultInfo.create(null);
            executeAndWriteResponse(get, resultInfo, Collections.<Integer>emptySet());
            HttpStatusBasedException.throwForError(resultInfo);
            sessionContext = null;
        }
    }

    @Override
    public String getForString(String template, Object... params) {
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        ResultInfo result = ResultInfo.create(responseBody);
        get(result, template, params);
        HttpStatusBasedException.throwForError(result);
        return responseBody.toString();
    }

    @Override
    public InputStream getForStream(String template, Object... params) {
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        ResultInfo result = ResultInfo.create(responseBody);
        get(result, template, params);
        HttpStatusBasedException.throwForError(result);
        return new ByteArrayInputStream(responseBody.toByteArray());
    }

    @Override
    public int get(ResultInfo result, String template, Object... params) {
        HttpGet method = createMethod(domain, project, GET_BUILDER, null, template, params);
        executeHttpMethod(method, result);
        return result.getHttpStatus();
    }

    @Override
    public int put(InputData inputData, ResultInfo result, String template, Object... params) {
        HttpPut HttpPut = createMethod(domain, project, PUT_BUILDER, createRequestEntity(inputData), template, params);
        setHeaders(HttpPut, inputData.getHeaders());
        executeHttpMethod(HttpPut, result);
        return result.getHttpStatus();
    }

    @Override
    public int delete(ResultInfo result, String template, Object... params) {
        HttpDelete HttpDelete = createMethod(domain, project, DELETE_BUILDER, null, template, params);
        executeHttpMethod(HttpDelete, result);
        return result.getHttpStatus();
    }

    @Override
    public int post(InputData data, ResultInfo result, String template, Object... params) {
        HttpPost HttpPost = createMethod(domain, project, POST_BUILDER, createRequestEntity(data), template, params);
        setHeaders(HttpPost, data.getHeaders());
        executeHttpMethod(HttpPost, result);
        return result.getHttpStatus();
    }
    
    private void setHeaders(HttpRequestBase method, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            method.setHeader(entry.getKey(), entry.getValue());
        }
    }

    private <T extends HttpRequestBase> T createMethod(String domain, String project, LocationBasedBuilder<T> builder, HttpEntity requestEntity, String template, Object... params) {
        String location = composeLocation(domain, project, template, params);
        T method = builder.build(location);
        if (requestEntity != null) {
            ((HttpEntityEnclosingRequestBase) method).setEntity(requestEntity);
        }
        return method;
    }

    private HttpEntity createRequestEntity(InputData inputData) {
        return inputData.getRequestEntity(encoding);
    }

    private void writeResponse(ResultInfo result, HttpResponse response, boolean writeBodyAndHeaders, HttpRequestBase method) {
        OutputStream bodyStream = result.getBodyStream();

        StatusLine statusLine = response.getStatusLine();
        if (statusLine != null) {
            result.setReasonPhrase(statusLine.getReasonPhrase());
        }
        try {
            result.setLocation(method.getURI().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (writeBodyAndHeaders) {
            Map<String, String> headersMap = result.getHeaders();
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                headersMap.put(header.getName(), header.getValue());
            }
        }
        result.setHttpStatus(response.getStatusLine().getStatusCode());
        Filter filter = new IdentityFilter(result);
        for(ResponseFilter responseFilter: responseFilters) {
            filter = responseFilter.applyFilter(filter, response, result);
        }
        if (writeBodyAndHeaders && bodyStream != null && response.getStatusLine().getStatusCode() != 204) {
            try {
                InputStream responseBody = response.getEntity().getContent();
                if (responseBody != null) {
                    copy(responseBody, filter.getOutputStream());
                    bodyStream.flush();
                    bodyStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException{
        byte[] buffer = new byte[1024 * 4];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private String composeLocation(String domain, String project, String template, Object... params) {
        String[] strParams = encodeParams(params);
        String substituted = MessageFormat.format(template, strParams);
        Object encDomain = encodeParams(new Object[]{domain})[0];
        Object encProject = encodeParams(new Object[]{project})[0];
        if (encDomain == null) {
            return pathJoin("/", location, "/rest", substituted);
        } else if (encProject == null) {
            return pathJoin("/", location, "/rest/domains", encDomain.toString(), substituted);
        }
        return pathJoin("/", location, "/rest/domains", encDomain.toString(), "projects", encProject.toString(), substituted);
    }

    private String[] encodeParams(Object params[]) {
        String enc = encoding;
        String result[] = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                result[i] = null;
                continue;
            }
            String strPar = params[i].toString();
            try {
                if (enc == null) {
                    result[i] = strPar;
                } else {
                    result[i] = URLEncoder.encode(strPar, enc).replace("+", "%20");
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e); //should never happen
            }
        }
        return result;
    }

    private boolean tryLogin(ResultInfo resultInfo, HttpRequestBase method) {
        try {
            login();
            return true;
        } catch (HttpStatusBasedException e) {
            resultInfo.setHttpStatus(e.getHttpStatus());
            resultInfo.setReasonPhrase(e.getReasonPhrase());
            try {
                resultInfo.setLocation(e.getLocation() + " [on-behalf-of: " + method.getURI().toString() + "]");
            } catch (Exception e2) {
                resultInfo.setLocation(e.getLocation() + " [on-behalf-of: " + method.getURI().toString() + "]");
            }
            return false;
        }
    }

    private void executeHttpMethod(HttpRequestBase method, ResultInfo resultInfo) {
        switch (sessionStrategy) {
            case NONE:
                executeAndWriteResponse(method, resultInfo, Collections.<Integer>emptySet());
                return;
            case AUTO_LOGIN:
                SessionContext myContext = null;
                synchronized (this) {
                    if (sessionContext == null) {
                        if(!tryLogin(resultInfo, method)) {
                            return;
                        }
                    } else {
                        myContext = sessionContext;
                    }
                }
                int statusCode = executeAndWriteResponse(method, resultInfo, AUTH_FAIL_STATUSES);
                if (AUTH_FAIL_STATUSES.contains(statusCode)) {
                    synchronized (this) {
                        if(myContext == sessionContext) {
                            // login (unless someone else just did it)
                            if(!tryLogin(resultInfo, method)) {
                                return;
                            }
                        }
                    }
                    // and try again
                    executeAndWriteResponse(method, resultInfo, Collections.<Integer>emptySet());
                }
        }
    }

    private int executeAndWriteResponse(HttpRequestBase method, ResultInfo resultInfo, Set<Integer> doNotWriteForStatuses) {
        try {
            HttpResponse status;
            int statusCode = -1;
            // prevent creation of multiple implicit sessions
            // hopefully the first request to come (and fill in the session) will be short
            boolean hasQcSession;
            synchronized (this) {
                hasQcSession = hasQcSessionCookie();
                if(!hasQcSession) {
                    status = httpClient.execute(method);
                    statusCode = status.getStatusLine().getStatusCode();
                    writeResponse(resultInfo, status, !doNotWriteForStatuses.contains(statusCode), method);
                }
            }
            if(hasQcSession) {
                //status = httpClient.execute(method);
                //status = new DefaultHttpClient().execute(method);
                //HttpClient testClient = new DefaultHttpClient();
                //testClient.execute(method);
                status = httpClient.execute(method);
                statusCode = status.getStatusLine().getStatusCode();
                writeResponse(resultInfo, status, !doNotWriteForStatuses.contains(statusCode), method);
            }


            //writeResponse(resultInfo, status, !doNotWriteForStatuses.contains(statusCode));
            return statusCode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }/* finally {
            method.releaseConnection();
        }*/
    }

    private boolean hasQcSessionCookie() {
        for(Cookie cookie: httpClient.getCookieStore().getCookies()) {
            if(COOKIE_SESSION_NAME.equals(cookie.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> listDomains() {
        return listValues(createMethod(null, null, GET_BUILDER, null, "domains"), "Domain");
    }

    @Override
    public List<String> listCurrentProjects() {
        if (domain == null) {
            throw new IllegalStateException("domain==null");
        }
        return listValues(createMethod(domain, null, GET_BUILDER, null, "projects"), "Project");
    }

    private List<String> listValues(HttpGet method, String entity) {
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        ResultInfo resultInfo = ResultInfo.create(responseBody);
        executeHttpMethod(method, resultInfo);
        return getAttributeValues(new ByteArrayInputStream(responseBody.toByteArray()), entity, "Name");
    }

    private List<String> getAttributeValues(InputStream xml, String elemName, String attrName) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new InputStreamReader(xml));
/*            XMLInputFactory factory = XmlUtils.createBasicInputFactory();
            XMLStreamReader parser;
            parser = factory.createXMLStreamReader(xml);*/
            List<String> result = new LinkedList<String>();

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && elemName.equals(xpp.getName())) {

                    for (int i = 0; i < xpp.getAttributeCount(); i++) {
                        String localName = xpp.getAttributeName(i);
                        if (attrName.equals(localName)) {
                            result.add(xpp.getAttributeValue(i));
                            break;
                        }
                    }
                }

                xpp.next();
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                xml.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private static interface LocationBasedBuilder<T extends HttpRequestBase> {

        T build(String location);

    }
}
