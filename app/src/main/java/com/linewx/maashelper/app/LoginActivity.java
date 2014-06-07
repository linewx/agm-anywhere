package com.linewx.maashelper.app;

import android.renderscript.RSRuntimeException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;
import com.hp.alm.ali.rest.client.AliRestClientFactory;
import com.hp.alm.ali.rest.client.RestClient;
import com.hp.alm.ali.rest.client.RestClientFactory;
import com.hp.alm.ali.rest.client.exception.HttpClientErrorException;
import android.os.StrictMode;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.hp.alm.ali.service.ServerType;

import com.hp.alm.ali.service.RestService;
import com.hp.alm.ali.model.parser.ProjectExtensionsList;
import org.apache.http.HttpStatus;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String encode(String val) {
        try {
            return URLEncoder.encode(val, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return val;
        }
    }

    public void login(View source) {
        //EditText location = (EditText) findViewById(R.id.location);
        EditText username = (EditText) findViewById(R.id.account);
        EditText password = (EditText) findViewById(R.id.password);
        //EditText domain = (EditText) findViewById(R.id.domain);
        //EditText project = (EditText) findViewById(R.id.project);

        RestClientFactory factory = AliRestClientFactory.getInstance();

        //RestClient restClient = factory.create(location.toString(), domain.toString(), project.toString(), username.toString(), password.toString(), RestClient.SessionStrategy.NONE);
        RestClient restClient = factory.create("test",
                                                "test",
                                                "test",
                                                "test",
                                                "test",
                                                RestClient.SessionStrategy.NONE);
        restClient.setEncoding(null);
        restClient.setTimeout(10000);

        ServerType serverType = getServerType(restClient, true);

        /*//restClient.login();

        try {

                restClient.login();

            // check for at least ALM 11
            RestService.getForString(restClient, "defects?query={0}", encode("{id[0]}"));

            try {
                InputStream is = restClient.getForStream("customization/extensions");

                *//*return checkServerType(ProjectExtensionsList.create(is));*//*
            } catch (HttpClientErrorException e){
               *//* if(e.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
                    return checkServerTypeOldStyle(restClient);
                }*//*
                throw e;
            }
        } catch(HttpClientErrorException e) {
         *//*   if(e.getHttpStatus() == HttpStatus.SC_UNAUTHORIZED) {
                throw new AuthenticationFailed();
            } else {
                throw new RuntimeException("Failed to connect to HP ALM: " + handleGenericException(restClient, restClient.getDomain(), restClient.getProject()));
            }*//*
            throw e;
        } catch(Exception e) {

            *//*throw new RuntimeException("Failed to connect to HP ALM: " + handleGenericException(restClient, restClient.getDomain(), restClient.getProject()));*//*
        } finally {
         *//*   if(loginLogout) {
                RestService.logout(restClient);
            }*//*
        }*/


        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("login information").
                setMessage( serverType.name()).
                setIcon(R.drawable.ic_launcher).
                create();
        alertDialog.show();


    }

    public static ServerType getServerType(RestClient restClient, boolean loginLogout) throws RuntimeException{
        try {
            if(loginLogout) {
                restClient.login();
            }
            // check for at least ALM 11
            RestService.getForString(restClient, "defects?query={0}", encode("{id[0]}"));

            try {
                InputStream is = restClient.getForStream("customization/extensions");
                return checkServerType(ProjectExtensionsList.create(is));
            } catch (HttpClientErrorException e){
                if(e.getHttpStatus() == HttpStatus.SC_NOT_FOUND) {
                    return checkServerTypeOldStyle(restClient);
                }
                throw e;
            }
        } catch(HttpClientErrorException e) {
            if(e.getHttpStatus() == HttpStatus.SC_UNAUTHORIZED) {
                throw new RuntimeException();
            } else {
                throw new RuntimeException("Failed to connect to HP ALM");
            }
        } catch(Exception e) {
            throw new RuntimeException("Failed to connect to HP ALM");
        } finally {
            if(loginLogout) {
                //RestService.logout(restClient);
            }
        }
    }

    private static ServerType checkServerType(ProjectExtensionsList projectExtensionsList) {
        String qcVersion = null;
        String aliVersion = null;

        for (String[] ext : projectExtensionsList) {
            if ("QUALITY_CENTER".equals(ext[0])) {
                qcVersion = ext[1];
            }

            if ("ALI_EXTENSION".equals(ext[0])) {
                aliVersion = ext[1];
            }

            if ("APM_EXTENSION".equals(ext[0])) {
                return ServerType.AGM;
            }
        }

        if(qcVersion != null && qcVersion.startsWith("11.5")) {
            if(aliVersion != null) {
                return ServerType.ALI11_5;
            } else {
                return ServerType.ALM11_5;
            }
        } else {
            // assume latest version compatibility
            if(aliVersion != null) {
                return ServerType.ALI12;
            } else {
                return ServerType.ALM12;
            }
        }
    }

    private static ServerType checkServerTypeOldStyle(RestClient restClient) {
        return ServerType.NONE;
        // check for ALM version
        /*String xml;
        try {
            xml = RestService.getForString(restClient, "../../../../?alt=application%2Fatomsvc%2Bxml");
        } catch (RestException e) {
            return ServerType.AGM;
        }

        if(xml.contains("{project}/build-instances")) {
            if(aliEnabledProject(restClient)) {
                return ServerType.ALI2;
            } else {
                return ServerType.ALM11;
            }
        } else if(xml.contains("{project}/changesets")) {
            if(aliEnabledProject(restClient)) {
                return ServerType.ALI;
            } else {
                return ServerType.ALM11;
            }
        } else {
            return ServerType.ALM11;
        }*/
    }

}
