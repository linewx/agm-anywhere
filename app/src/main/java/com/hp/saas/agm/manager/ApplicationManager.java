package com.hp.saas.agm.manager;


import android.content.Context;
import com.hp.saas.agm.service.*;
import com.hp.saas.agm.core.translate.TranslateService;

/**
 * Created by lugan on 6/10/2014.
 */

public class ApplicationManager {

    private static final RestService restService;
    private static final TranslateService translateService;
    private static final EntityService entityService;
    private static final MetadataService metadataService;
    private static final MetadataSimpleService metadataSimpleService;
    private static final SprintService sprintService;
    private static final TeamMemberService teamMemberService;
    private static final UserService userService;
    private static final SharedPreferencesService sharedPreferencesService;
    private static final MessageService messageService;
    private static Context context;

    static {
        sharedPreferencesService = new SharedPreferencesService();
        restService = RestService.getInstance();
        metadataSimpleService = new MetadataSimpleService();
        metadataService = new MetadataService(metadataSimpleService, restService);
        translateService = new TranslateService();
        entityService = new EntityService(restService, metadataService);
        sprintService = new SprintService(entityService, restService, sharedPreferencesService);
        teamMemberService = new TeamMemberService(entityService);
        userService = new UserService(entityService, sharedPreferencesService);
        messageService = new MessageService();

    }

    public static RestService getRestService() {
        return restService;
    }

    public static MetadataService getMetadataService() {
        return metadataService;
    }

    public static TranslateService getTranslateService() {
        return translateService;
    }

    public static EntityService getEntityService() {
        return entityService;
    }

    public static SprintService getSprintService() {
        return sprintService;
    }

    public static TeamMemberService getTeamMemberService() {
        return teamMemberService;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static SharedPreferencesService getSharedPreferencesService() {
        return sharedPreferencesService;
    }

    public static MessageService getMessageService() {
        return messageService;
    }

    public static Context getContext() {
        return context;
    }

    public static void init(Context applicationContext) {
        context = applicationContext;
        sharedPreferencesService.init(applicationContext);
        messageService.init(applicationContext);
        userService.init();
    }
}
