package com.hp.alm.ali.manager;


import com.hp.alm.ali.model.ServerStrategy;
import com.hp.alm.ali.service.*;
import com.hp.alm.ali.translate.TranslateService;

import java.lang.reflect.Constructor;

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

    static {
        restService = RestService.getInstance();
        metadataSimpleService = new MetadataSimpleService();
        metadataService = new MetadataService(metadataSimpleService, restService);
        translateService = new TranslateService();
        entityService = new EntityService(restService, metadataService);
        sprintService = new SprintService(entityService, restService);
        teamMemberService = new TeamMemberService(entityService);
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





}
