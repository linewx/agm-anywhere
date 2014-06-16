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

package com.hp.alm.ali.service;


import com.hp.alm.ali.manager.StrategyManager;
import com.hp.alm.ali.rest.client.AliRestClientFactory;
import com.hp.alm.ali.rest.client.RestClient;
import com.hp.alm.ali.rest.client.RestClientFactory;
import com.hp.alm.ali.rest.client.exception.AuthenticationFailureException;


import com.hp.alm.ali.rest.client.ResultInfo;

import com.hp.alm.ali.model.ServerStrategy;
import com.hp.alm.ali.manager.ApplicationManager;

/*
import javax.swing.event.HyperlinkEvent;*/
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;

public class RestService{

    private static RestClientFactory factory = AliRestClientFactory.getInstance();
    private RestClient restClient;
    private ServerType serverType = ServerType.NONE;

    private static RestService restService = new RestService();

    private RestService() {

    }

    public static RestService getInstance() {
        return restService;
    }


    /*private ServerType serverType = ServerType.NONE;
    volatile private RestClient restClient;
    private WeakListeners<RestListener> listeners;
    private WeakListeners<ServerTypeListener> serverTypeListeners;
    private Project project;
    private AliProjectConfiguration projConf;
    private RestServiceLogger restServiceLogger;
    final Notification errorNotification;

    private static RestClientFactory factory = AliRestClientFactory.getInstance();

    public RestService(final Project project, TroubleShootService troubleShootService, AliProjectConfiguration conf) {
        this.project = project;
        this.restServiceLogger = troubleShootService;
        this.projConf = conf;
        listeners = new WeakListeners<RestListener>();
        serverTypeListeners = new WeakListeners<ServerTypeListener>();
        conf.addListener(this);
        ApplicationManager.getApplication().getComponent(AliConfiguration.class).addListener(this);

        errorNotification = new Notification("HP ALM Integration", "Cannot connect to HP ALM",
                "<p><a href=\"\">Configure HP ALM integration ...</a></p>", NotificationType.ERROR,
                new NotificationListener() {
                    public void hyperlinkUpdate(Notification notification, HyperlinkEvent event) {
                        notification.expire();
                        ShowSettingsUtil.getInstance().showSettingsDialog(project, AliConfigurable.NAME);
                    }
                });
    }

    public void launchProjectUrl(String href) {
        String query =  href.contains("?")? "&": "?";
        BrowserUtil.launchBrowser(projConf.getLocation() + "/rest/domains/" + projConf.getDomain() + "/projects/" + projConf.getProject() + "/" + href + query + "login-form-required=Y");
    }

    private RestClient createRestClient(AliProjectConfiguration conf) {
        return createRestClient(conf.getLocation(), conf.getDomain(), conf.getProject(), conf.getUsername(), conf.getPassword(), RestClient.SessionStrategy.AUTO_LOGIN);
    }*/

    public RestClient createRestClient(String location, String domain, String project, String username, String password, RestClient.SessionStrategy strategy) {
        restClient = factory.create(location, domain, project, username, password, strategy);
        restClient.setEncoding(null);
        restClient.setTimeout(10000);
        /*
        //set proxy
        HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
        IdeaWideProxySelector proxySelector = new IdeaWideProxySelector(httpConfigurable);
        List<Proxy> proxies = proxySelector.select(VfsUtil.toUri(location));
        if (proxies != null) {
            for (Proxy proxy: proxies) {
                if (HttpConfigurable.isRealProxy(proxy) && Proxy.Type.HTTP.equals(proxy.type())) {
                    InetSocketAddress address = (InetSocketAddress)proxy.address();
                    restClient.setHttpProxy(address.getHostName(), address.getPort());

                    // not sure how IdeaWideAuthenticator & co. is supposed to work, let's try something simple:
                    if(httpConfigurable.PROXY_AUTHENTICATION) {
                        PasswordAuthentication authentication = httpConfigurable.getPromptedAuthentication("HP ALI", address.getHostName());
                        if(authentication != null) {
                            restClient.setHttpProxyCredentials(authentication.getUserName(), new String(authentication.getPassword()));
                        }
                    }
                    break;
                }
            }
        }*/
        return restClient;
    }


    public synchronized RestClient getRestClient() {
       /* if(restClient == null) {
            restClient = createRestClient(projConf);
        }*/
        return restClient;
    }

    public int get(MyResultInfo result, String template, Object... params) {
        return execute(getRestClient(), new MyGetMethod(), null, result, template, params);
    }

    public int put(String xml, MyResultInfo result, String template, Object... params) {
        return put(new MyInputData(xml), result, template, params);
    }

    public int put(MyInputData inputData, MyResultInfo result, String template, Object... params) {
        return execute(getRestClient(), new MyPutMethod(), inputData, result, template, params);
    }

    public int post(MyInputData inputData, MyResultInfo result, String template, Object... params) {
        return execute(getRestClient(),  new MyPostMethod(), inputData, result, template, params);
    }

    public int post(String xml, MyResultInfo result, String template, Object... params) {
        return post(new MyInputData(xml), result, template, params);
    }

    public int delete(MyResultInfo result, String template, Object... params) {
        return execute(getRestClient(), new MyDeleteMethod(), null, result, template, params);
    }

    public void delete(String template, Object... params) {
        execute(getRestClient(), new MyDeleteMethod(), null, new MyResultInfo(), template, params);
    }

    public static String getForString(RestClient restClient, String template, Object ... params) {
       // TroubleShootService troubleShootService = ApplicationManager.getApplication().getComponent(TroubleShootService.class);
        MyResultInfo result = new MyResultInfo();
        int status = execute(restClient, new MyGetMethod(), null, result, template, params);
        if(status < 200 || status > 299) {
            //throw new RestException(result);
            throw new RuntimeException();
        }
        return result.getBodyAsString();
    }

    public InputStream getForStream(String template, Object ... params) {
        MyResultInfo result = new MyResultInfo();
        int status = get(result, template, params);
        if(status < 200 || status > 299) {
            throw new RuntimeException();
        }
       /* UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                errorNotification.expire();
            }
        });*/
        return result.getBodyAsStream();
    }

    private static int execute(RestClient restClient, MyMethod method, MyInputData myInput, MyResultInfo myResult, String template, Object... params) {
        ResultInfo info = ResultInfo.create(myResult.getOutputStream());
        //long id = restServiceLogger.request(project, method.getName(), myInput, template, params);
        int code;
        try {
            code = method.execute(restClient, myInput == null? null: myInput.getInputData(), info, template, params);
        } catch(AuthenticationFailureException e) {
            //restServiceLogger.loginFailure(id, e);
            throw e;
        }
        myResult.copyFrom(info);
        //restServiceLogger.response(id, code, myResult);
        return code;
    }

    public synchronized ServerType getServerTypeIfAvailable() {
        return serverType;
    }

    public synchronized ServerType getServerType() throws InterruptedException {
        while(serverType == ServerType.CONNECTING) {
            wait();
        }
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        synchronized (this) {
            this.serverType = serverType;
            notifyAll();
        }
        //fireServerTypeEvent();
    }

    public synchronized ServerStrategy getServerStrategy() {
        StrategyManager strategyManager = StrategyManager.getInstance();
        return strategyManager.getServerStrategy(serverType.getClazz().getSimpleName());
    }


    /*public void addListener(RestListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RestListener listener) {
        listeners.remove(listener);
    }

    public void onChanged() {
        try {
            synchronized (this) {
                if(restClient != null) {
                    logout(restClient);
                }
                serverType = ServerType.NONE; // fire event later (when connecting)
                restClient = createRestClient(projConf);
            }
            fireRestConfigurationChanged();
        } finally {
            checkConnectivity();
        }
    }

    public static void logout(final RestClient client) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                client.logout();
            }
        });
    }

    private void fireRestConfigurationChanged() {
        listeners.fire(new WeakListeners.Action<RestListener>() {
            public void fire(RestListener listener) {
                listener.restConfigurationChanged();
            }
        });
    }

    public void checkConnectivity() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                try {
                    if(setConnectingType()) {
                        setServerType(AliConfigurable.getServerType(getRestClient(), false));
                    }
                } catch(Exception e) {
                    if(e instanceof AuthenticationFailed && projConf.getPassword().isEmpty()) {
                        setServerType(ServerType.NEEDS_PASSWORD);
                    } else {
                        setServerType(ServerType.NONE);
                    }
                    UIUtil.invokeLaterIfNeeded(new Runnable() {
                        public void run() {
                            ToolWindow toolWindow = project.getComponent(ToolWindowManager.class).getToolWindow("HP ALI");
                            if (toolWindow != null && toolWindow.getContentManager().getContentCount() > 1) {
                                expireConnectivityError();
                                Notifications.Bus.notify(errorNotification, project);
                            }
                        }
                    });
                }
            }
        });
    }

    public void expireConnectivityError() {
        errorNotification.expire();
    }

    public synchronized ServerType getServerTypeIfAvailable() {
        return serverType;
    }

    public synchronized ServerType getServerType() throws InterruptedException {
        while(serverType == ServerType.CONNECTING) {
            wait();
        }
        return serverType;
    }

    public synchronized ServerStrategy getServerStrategy() {
        return project.getComponent(serverType.getClazz());
    }

    private boolean setConnectingType() {
        synchronized (this) {
            if(serverType != ServerType.NONE) {
                return false;
            }
            serverType = ServerType.CONNECTING;
            notifyAll();
        }
        fireServerTypeEvent();
        return true;
    }

    public void setServerType(ServerType serverType) {
        synchronized (this) {
            this.serverType = serverType;
            notifyAll();
        }
        fireServerTypeEvent();
    }

    private void fireServerTypeEvent() {
        serverTypeListeners.fire(new WeakListeners.Action<ServerTypeListener>() {
            public void fire(ServerTypeListener listener) {
                listener.connectedTo(serverType);
            }
        });
    }

    public void addServerTypeListener(ServerTypeListener listener) {
        addServerTypeListener(listener, true);
    }

    public void addServerTypeListener(ServerTypeListener listener, boolean weak) {
        serverTypeListeners.add(listener, weak);
    }

    public void removeServerTypeListener(ServerTypeListener listener) {
        serverTypeListeners.remove(listener);
    }

    public boolean _isRegistered(ServerTypeListener listener) {
        return serverTypeListeners.isRegistered(listener);
    }

    @TestOnly
    static void _setFactory(RestClientFactory factory) {
        RestService.factory = factory;
    }

    @TestOnly
    void _setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @TestOnly
    void _setRestServiceLogger(RestServiceLogger logger) {
        this.restServiceLogger = logger;
    }

    @TestOnly
    synchronized void _setServerType(ServerType serverType) {
        this.serverType = serverType;
        notifyAll();
    }*/
}
