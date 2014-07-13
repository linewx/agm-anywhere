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

package com.hp.saas.agm.service;

public class UserService{

    private EntityService entityService;
    private SharedPreferencesService sharedPreferencesService;
    private String location;
    private String domain;
    private String project;
    private String user;
    private String password;


    public UserService(EntityService entityService, SharedPreferencesService sharedPreferencesService) {
        this.entityService = entityService;
        this.sharedPreferencesService = sharedPreferencesService;
    }

   public void init() {
       //todo: make sure sharedPreferencesService has been initilaized
       this.user = sharedPreferencesService.getPreference("user");
       this.password = sharedPreferencesService.getPreference("password");
       this.project = sharedPreferencesService.getPreference("project");
       this.domain = sharedPreferencesService.getPreference("domain");
       this.location = sharedPreferencesService.getPreference("location");
   }

   public String getUser() {
       return user;
   }

    public void setUser(String user) {
        this.user = user;
        sharedPreferencesService.setPreference("user", user);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        sharedPreferencesService.setPreference("password", password);
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
        sharedPreferencesService.setPreference("domain", domain);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        sharedPreferencesService.setPreference("location", location);
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
        sharedPreferencesService.setPreference("project", project);
    }





}
