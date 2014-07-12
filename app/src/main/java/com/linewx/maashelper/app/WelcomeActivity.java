package com.linewx.maashelper.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.widget.ImageView;
import com.hp.alm.ali.manager.ApplicationManager;
import com.hp.alm.ali.rest.client.RestClient;
import com.hp.alm.ali.service.RestService;

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
		setContentView(R.layout.activity_welcome);
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
                        ApplicationManager.getSprintService().init();

                        //loading story
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                        finish();

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
