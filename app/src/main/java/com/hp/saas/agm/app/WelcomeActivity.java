package com.hp.saas.agm.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.widget.ImageView;
import com.hp.saas.agm.manager.ApplicationManager;
import com.hp.saas.agm.rest.client.RestClient;
import com.hp.saas.agm.service.RestService;

public class WelcomeActivity extends Activity {
	protected static final String TAG = "WelcomeActivity";
	private Context mContext;
	private ImageView mImageView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_activity);
		mContext = this;
		findView();
		init();
	}

	private void findView() {
		mImageView = (ImageView) findViewById(R.id.iv_welcome);
	}

	private void init() {

            //init service
            ApplicationManager.init(getApplicationContext());

            mImageView.postDelayed(new Runnable() {
			@Override
			public void run() {
                String user = ApplicationManager.getUserService().getUser();
                String password = ApplicationManager.getUserService().getPassword();
                String location = ApplicationManager.getUserService().getLocation();
                String domain = ApplicationManager.getUserService().getDomain();
                String project = ApplicationManager.getUserService().getProject();

                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(password) || TextUtils.isEmpty(location)
                        || TextUtils.isEmpty(domain) || TextUtils.isEmpty(project)) {
                    //login in manually
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    RestService restService = ApplicationManager.getRestService();
                    RestClient restClient = restService.createRestClient(location, domain, project, user, password, RestClient.SessionStrategy.NONE);

                    try {
                        restClient.login();
                        restService.setServerType(LoginActivity.getServerType(restClient));

                        //load meta information from cache
                        ApplicationManager.getSprintService().init();

                        //todo: for testing
                        if (ApplicationManager.getSprintService().getRelease() == null || ApplicationManager.getSprintService().getTeam() == null) {
                            //set up release
                            Intent intent = new Intent(mContext, ReleaseConfigurationActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            //enter to main screen
                            Intent intent = new Intent(mContext, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    } catch(Exception e) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

			}
		},2000);
		
	}
}
