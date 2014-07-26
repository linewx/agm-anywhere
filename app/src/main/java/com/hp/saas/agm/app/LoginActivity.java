package com.hp.saas.agm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.hp.saas.agm.manager.ApplicationManager;
import com.hp.saas.agm.core.model.parser.ProjectExtensionsList;
import com.hp.saas.agm.rest.client.RestClient;
import com.hp.saas.agm.rest.client.exception.AuthenticationFailureException;
import com.hp.saas.agm.service.RestService;
import com.hp.saas.agm.service.ServerType;

import java.io.InputStream;


public class LoginActivity extends Activity {
    private Button mLogin;

    private EditText etUserName;
    private EditText etPassword;
    private EditText etLocation;
    private EditText etDomain;
    private EditText etProject;
    private ProgressBar pbLoading;


    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(loginOnClickListener);

        initView();

        context = this;
    }


    public void initView() {
        etUserName = (EditText) findViewById(R.id.account);
        etPassword = (EditText) findViewById(R.id.password);
        etLocation = (EditText) findViewById(R.id.location);
        etDomain = (EditText) findViewById(R.id.domain);
        etProject = (EditText) findViewById(R.id.project);

        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        //init user & password
        String user = ApplicationManager.getUserService().getUser();
        String password = ApplicationManager.getUserService().getPassword();
        String location = ApplicationManager.getUserService().getLocation();
        String domain = ApplicationManager.getUserService().getDomain();
        String project = ApplicationManager.getUserService().getProject();

        if (!TextUtils.isEmpty(user)) {
            etUserName.setText(user);
        }
        if (!TextUtils.isEmpty(password)) {
            etPassword.setText(password);
        }
        if (!TextUtils.isEmpty(location)) {
            etLocation.setText(location);
        }
        if (!TextUtils.isEmpty(domain)) {
            etDomain.setText(domain);
        }
        if (!TextUtils.isEmpty(project)) {
            etProject.setText(project);
        }

    }


    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            login(v);
        }
    };

    public void login(View source) {

        if (etUserName.getText().toString().trim().equals("")) {
/*            tvLoginInfo.setText("please input account");
            tvLoginInfo.setVisibility(View.VISIBLE);*/
        } else if (etPassword.getText().toString().trim().equals("")) {
/*            tvLoginInfo.setText("Please input password");
            tvLoginInfo.setVisibility(View.VISIBLE);*/
        } else {
            //store user & password
            pbLoading.setVisibility(View.VISIBLE);
            mLogin.setClickable(false);
            mLogin.setAlpha(0.5f);

            ApplicationManager.getUserService().setUser(etUserName.getText().toString());
            ApplicationManager.getUserService().setPassword(etPassword.getText().toString());
            ApplicationManager.getUserService().setLocation(etLocation.getText().toString());
            ApplicationManager.getUserService().setDomain(etDomain.getText().toString());
            ApplicationManager.getUserService().setProject(etProject.getText().toString());

            RestService restService = ApplicationManager.getRestService();

            try {
                RestClient restClient = restService.createRestClient(
                        ApplicationManager.getUserService().getLocation(),
                        ApplicationManager.getUserService().getDomain(),
                        ApplicationManager.getUserService().getProject(),
                        ApplicationManager.getUserService().getUser(),
                        ApplicationManager.getUserService().getPassword(),
                        RestClient.SessionStrategy.NONE);

                restClient.login();
                restService.setServerType(getServerType(restClient));
                ApplicationManager.getSprintService().init();

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();

            } catch (AuthenticationFailureException e) {
                ApplicationManager.getMessageService().show("Authentication failed");
            } catch (Exception e) {
                ApplicationManager.getMessageService().show("Connection failed");
            } finally {
                pbLoading.setVisibility(View.INVISIBLE);
                mLogin.setClickable(true);
                mLogin.setAlpha(1f);
            }



        }


        //restClient.login();




       /* Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("login information").
                setMessage("Hello").
                setIcon(R.drawable.ic_launcher).
                create();
        alertDialog.show();*/


    }

    public static ServerType getServerType(RestClient restClient) {
        InputStream is = restClient.getForStream("customization/extensions");
        return checkServerType(ProjectExtensionsList.create(is));
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

        if (qcVersion != null && qcVersion.startsWith("11.5")) {
            if (aliVersion != null) {
                return ServerType.ALI11_5;
            } else {
                return ServerType.ALM11_5;
            }
        } else {
            // assume latest version compatibility
            if (aliVersion != null) {
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
