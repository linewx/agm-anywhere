package com.linewx.maashelper.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;
import com.hp.alm.ali.manager.ApplicationManager;
import com.hp.alm.ali.model.parser.EntityList;
import com.hp.alm.ali.rest.client.RestClient;
import com.hp.alm.ali.service.RestService;
import com.hp.alm.ali.utils.SpUtil;
import com.linewx.maashelper.app.persistent.Settings;

import com.linewx.maashelper.app.test.SDManager;

public class WelcomeActivity extends Activity {
	protected static final String TAG = "WelcomeActivity";
	private Context mContext;
	private ImageView mImageView;
	private SharedPreferences sp;

    private final String LOCATION = "https://agilemanager-int.saas.hp.com/agm";
    private final String DOMAIN="t604331885_hp_com";
    private final String PROJECT="MaaS";

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

	@SuppressWarnings("static-access")
	private void init() {

        //load state
        Settings settings = Settings.getInstance();
        settings.init(getApplicationContext());


		mImageView.postDelayed(new Runnable() {
			@Override
			public void run() {
                String user = Settings.getInstance().getUser();
                String password = Settings.getInstance().getPassword();

                if (user != null || password != null) {
                    //login in manually
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    //auto login
                    RestService restService = ApplicationManager.getRestService();
                    RestClient restClient = restService.createRestClient(LOCATION, DOMAIN, PROJECT, "guest@test.com",
                            "password", RestClient.SessionStrategy.NONE);

                    restService.setServerType(LoginActivity.getServerType(restClient, true));

                    //set default release
                   /* if (Settings.getInstance().getReleaseId() == null) {
                        ApplicationManager.getSprintService().init();
                    }*/
                    ApplicationManager.getSprintService().init();

                    //loading story
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish();

                }



               /* sp = SpUtil.getInstance().getSharePerference(mContext);

                //workarond
                SpUtil.getInstance().setBooleanSharedPerference(sp,
                        "isFirst", false);
				boolean isFirst = SpUtil.getInstance().isFirst(sp);
				if (!isFirst) {
					SDManager manager = new SDManager(mContext);

					SpUtil.getInstance().setBooleanSharedPerference(sp,
							"isFirst", true);
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
					finish();
				}*/
			}
		},2000);
		
	}
}
