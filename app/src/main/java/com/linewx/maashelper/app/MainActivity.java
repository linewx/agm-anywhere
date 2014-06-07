package com.linewx.maashelper.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.*;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import com.hp.alm.ali.model.parser.ProjectExtensionsList;
import com.hp.alm.ali.rest.client.AliRestClientFactory;
import com.hp.alm.ali.rest.client.RestClient;
import com.hp.alm.ali.rest.client.RestClientFactory;
import com.hp.alm.ali.rest.client.exception.HttpClientErrorException;
import com.hp.alm.ali.service.RestService;
import com.hp.alm.ali.service.ServerType;
import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.os.Bundle;
import android.view.View;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class MainActivity extends FragmentActivity {

    protected static final String TAG = "MainActivity";
    private Context mContext;
    private ImageButton mNews,mConstact,mDeynaimic,mSetting;
    private View mPopView;
    private View currentButton;

    private TextView app_cancle;
    private TextView app_exit;
    private TextView app_change;

    private int mLevel=1;
    private PopupWindow mPopupWindow;
    private LinearLayout buttomBarGroup;
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;

        findView();
        init();


    }

    private void findView(){

        mPopView= LayoutInflater.from(mContext).inflate(R.layout.app_exit, null);
        buttomBarGroup=(LinearLayout) findViewById(R.id.buttom_bar_group);
        mNews=(ImageButton) findViewById(R.id.buttom_news);
        mConstact=(ImageButton) findViewById(R.id.buttom_constact);
        mDeynaimic=(ImageButton) findViewById(R.id.buttom_deynaimic);
        mSetting=(ImageButton) findViewById(R.id.buttom_setting);

/*        app_cancle=(TextView) mPopView.findViewById(R.id.app_cancle);
        app_change=(TextView) mPopView.findViewById(R.id.app_change_user);
        app_exit=(TextView) mPopView.findViewById(R.id.app_exit);*/
    }

    private void init(){
        mNews.setOnClickListener(newsOnClickListener);
        mConstact.setOnClickListener(constactOnClickListener);
        mDeynaimic.setOnClickListener(deynaimicOnClickListener);
        mSetting.setOnClickListener(settingOnClickListener);

        mNews.performClick();

        mPopupWindow=new PopupWindow(mPopView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        app_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        app_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent=new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.activity_up, R.anim.fade_out);
                finish();*/
            }
        });

        app_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private View.OnClickListener newsOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          /*  FragmentManager fm=getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            NewsFatherFragment newsFatherFragment=new NewsFatherFragment();
            ft.replace(R.id.fl_content, newsFatherFragment,MainActivity.TAG);
            ft.commit();
            setButton(v);*/

        }
    };

    private View.OnClickListener constactOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           /* FragmentManager fm=getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            ConstactFatherFragment constactFatherFragment=new ConstactFatherFragment();
            ft.replace(R.id.fl_content, constactFatherFragment,MainActivity.TAG);
            ft.commit();
            setButton(v);*/

        }
    };

    private View.OnClickListener deynaimicOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          /*  FragmentManager fm=getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            DynamicFragment dynamicFragment=new DynamicFragment();
            ft.replace(R.id.fl_content, dynamicFragment,MainActivity.TAG);
            ft.commit();
            setButton(v);*/

        }
    };

    private View.OnClickListener settingOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         /*   FragmentManager fm=getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            SettingFragment settingFragment=new SettingFragment();
            ft.replace(R.id.fl_content, settingFragment,MainActivity.TAG);
            ft.commit();
            setButton(v);*/

        }
    };

    private void setButton(View v){
        if(currentButton!=null&&currentButton.getId()!=v.getId()){
            currentButton.setEnabled(true);
        }
        v.setEnabled(false);
        currentButton=v;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
/*        if(keyCode==KeyEvent.KEYCODE_MENU){
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b0000000")));
            mPopupWindow.showAtLocation(buttomBarGroup, Gravity.BOTTOM, 0, 0);
            mPopupWindow.setAnimationStyle(R.style.app_pop);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.update();
        }*/
        //return super.onKeyDown(keyCode, event);
        return true;

    }


}
