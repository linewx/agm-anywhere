package com.linewx.maashelper.app;

import android.app.Application;
import com.hp.alm.ali.service.RestService;
import com.hp.alm.ali.translate.TranslateService;
import com.hp.alm.ali.model.ServerStrategy;
import java.lang.reflect.Constructor;


public class MassApp extends Application {

    private static RestService restService;
    private static TranslateService translateService;
    private static ServerStrategy serverStrategy;
    @Override
    public void onCreate() {
        super.onCreate();

        restService = new RestService();
        translateService = new TranslateService();

    }

    public static RestService getRestService() {
        return restService;
    }

    public static TranslateService getTranslateService() {
        return translateService;
    }

    public static ServerStrategy getServerStrategry(Class<? extends ServerStrategy> clazz) {
        try {
            if(serverStrategy == null) {
                Constructor<?> ctor = clazz.getConstructor();
                serverStrategy = (ServerStrategy)ctor.newInstance();
            }
        }catch(Exception e) {

        }

        return serverStrategy;

    }
}
