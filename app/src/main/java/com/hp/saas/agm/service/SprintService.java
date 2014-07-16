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

import android.content.SharedPreferences;
import com.hp.saas.agm.core.entity.EntityQuery;
import com.hp.saas.agm.core.entity.EntityRef;
import com.hp.saas.agm.core.entity.NewEntityListener;
import com.hp.saas.agm.core.entity.SortOrder;
import com.hp.saas.agm.manager.ApplicationManager;
import com.hp.saas.agm.manager.ThreadPoolManager;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;


import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class SprintService {

    private static SimpleDateFormat ALM_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private EntityService entityService;
    private RestService restService;
    private SharedPreferencesService sharedPreferencesService;

    final private Selector releaseSelector = new Selector();
    final private Selector sprintSelector = new Selector();
    final private Selector teamSelector = new Selector();
    final private Selector storySelector = new Selector();
    final private Selector teamMemberSelector = new Selector();

    public SprintService(EntityService entityService, RestService restService, SharedPreferencesService sharedPreferencesService) {
        this.entityService = entityService;
        this.restService = restService;
        this.sharedPreferencesService = sharedPreferencesService;

    }

    public void init() {
/*        loadReleases();
        loadSprints();
        loadTeams();
        loadStories();*/
        //load from cache
        //we do cache for: release, sprint, teams, teams members
        initReleases();
        initSprint();
        initTeams();
        initTeamMembers();
    }

    public void initReleases() {
        try {
            releaseSelector.values = EntityList.create(sharedPreferencesService.getFromFile("release"));
        }catch(IOException e) {
            releaseSelector.values = null;
        }

        String releaseId = sharedPreferencesService.getPreference("selectedReleaseId");
        if (releaseId != null) {
            releaseSelector.selected = lookup(new EntityRef("release", Integer.parseInt(releaseId)));
        }
        releaseSelector.requestRunning = false;
    }

    public void initSprint() {
        try {
            sprintSelector.values = EntityList.create(sharedPreferencesService.getFromFile("sprint"));
        }catch(IOException e) {
            sprintSelector.values = null;
        }
        if (sprintSelector.values != null) {
            selectSprint(findClosest(sprintSelector.values));
        }

    }

    public void initTeams() {
        try {
            teamSelector.values = EntityList.create(sharedPreferencesService.getFromFile("team"));
        }catch(IOException e) {
            releaseSelector.values = null;
        }

        String teamId = sharedPreferencesService.getPreference("teamId");
        if (teamId != null) {
            teamSelector.selected = lookup(new EntityRef("team", Integer.parseInt(teamId)));
        }
        teamSelector.requestRunning = false;
    }
    public void initTeamMembers() {
        //only load my team member
        try {
            teamMemberSelector.values = EntityList.create(sharedPreferencesService.getFromFile("team_memeber"));
        }catch(IOException e) {
            releaseSelector.values = null;
        }
        teamMemberSelector.selected = null;
        releaseSelector.requestRunning = false;
    }

/*    private Entity loadEntity(Element element, String type) {
        String id = element.getAttributeValue(type + "-id");
        if(id != null) {
            Entity entity = new Entity(type, Integer.valueOf(id));
            entity.setProperty("name", element.getAttributeValue(type+"-name"));
            return entity;

        } else {
            return null;
        }
    }*/

    synchronized void resetValues() {
        teamSelector.values = null;
        sprintSelector.values = null;
        releaseSelector.values = null;
        storySelector.values = null;
    }

    public EntityList getReleasesFromFile() {
        Selector release = (Selector)sharedPreferencesService.getObjectPreference("release");
        if (release == null) {
            return null;
        }else {
            return release.values;
        }

    }

    public void connectedTo(ServerType serverType) {
        if(ServerType.AGM.equals(restService.getServerTypeIfAvailable())) {
            synchronized (this) {
                resetValues();
                final Entity release = this.releaseSelector.selected;
                if(release != null) {
                    teamSelector.requestRunning = true;
                    sprintSelector.requestRunning = true;
                    releaseSelector.requestRunning = true;
                    ThreadPoolManager.getInstance().executeOnPooledThread(new Runnable() {
                        @Override
                        public void run() {
                            loadTeams(release);
                            loadSprints(release);
                            loadReleases();
                        }
                    });
                } else {
                    this.releaseSelector.requestRunning = true;
                   ThreadPoolManager.getInstance().executeOnPooledThread(new Runnable() {
                        @Override
                        public void run() {
                            loadReleases();
                        }
                    });
                }
            }
        }
    }

    public synchronized Entity getRelease() {
        return releaseSelector.selected;
    }

    public synchronized Entity getSprint() {
        return sprintSelector.selected;
    }

    public synchronized Entity getTeam() {
        return teamSelector.selected;
    }

    private void loadReleases() {
        EntityQuery query = new EntityQuery("release");
        query.addColumn("id", 1);
        query.addColumn("name", 1);
        query.addColumn("start-date", 1);
        query.addColumn("end-date", 1);
        EntityList list = EntityList.empty();
        try {
            entityService.setEntityQueryListener(new NewEntityListener.EntityQueryListener() {
                @Override
                public void query(EntityQuery query, byte[] bytes) {
                    if (query.getEntityType().equals("release")) {
                        sharedPreferencesService.saveToFile("release", bytes);
                    }
                }
            });
            list = entityService.query(query);


        } finally {
            synchronized (this) {
                releaseSelector.values = list;
                releaseSelector.requestRunning = false;

                if(!releaseSelector.values.contains(releaseSelector.selected)) {
                    selectRelease(findClosest(list));
                }
                //save release persistently

                notifyAll();
            }
        }
    }

    private void loadSprints() {
        loadSprints(releaseSelector.selected);
    }

    private void loadTeams() {
        loadTeams(releaseSelector.selected);
    }

    private void loadStories() {
        loadStories(sprintSelector.selected, teamSelector.selected);
    }

    private void loadSprints(final Entity release) {
        EntityQuery query = new EntityQuery("release-cycle");
        query.addColumn("id", 1);
        query.addColumn("name", 1);
        query.addColumn("tense", 1);
        query.addColumn("start-date", 1);
        query.addColumn("end-date", 1);
        query.setValue("parent-id", String.valueOf(release.getId()));
        query.addOrder("start-date", SortOrder.ASCENDING);
        EntityList list = EntityList.empty();
        try {
            list = entityService.query(query);
        } finally {
            synchronized (this) {
                sprintSelector.values = list;
                sprintSelector.requestRunning = false;

                if(!sprintSelector.values.contains(sprintSelector.selected)) {
                    selectSprint(findClosest(list));
                }
                notifyAll();
            }
        }
    }

    private void loadStories(final Entity sprint, final Entity team) {
        EntityQuery query = new EntityQuery("release-backlog-item");
        query.addColumn("release-id", 1);
        query.addColumn("entity-name", 1);
        query.addColumn("status", 1);
        query.addColumn("owner", 1);
        query.addColumn("entity-id", 1);
        query.addColumn("story-points", 1);
        query.setValue("sprint-id", String.valueOf(sprint.getPropertyValue("id")));
        query.setValue("team-id", String.valueOf(team.getPropertyValue("id")));
        //query.addOrder("last-modified", SortOrder.ASCENDING);
        EntityList list = EntityList.empty();
        try {
            list = entityService.query(query);
        } finally {
            synchronized (this) {
                storySelector.values = list;
                storySelector.requestRunning = false;
                notifyAll();
            }
        }
    }

    //todo: workround for maas
    private Entity findMaaS(List<Entity> list) {
        for (Entity entity : list) {
            if (entity.getPropertyValue("name").startsWith("SPMaaS")) {
                return entity;
            }
        }

        if(!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private Entity findMyTeam(List<Entity> teams, Entity release, String userName) {
        String teamId = ApplicationManager.getTeamMemberService().getTeamId(userName, release.getPropertyValue("id"));
        for (Entity entity: teams) {
            if (entity.getProperty("id").equals(teamId)) {
                return entity;
            }
        }
        return null;
    }

    private Entity findClosest(List<Entity> list) {
        if(!list.isEmpty()) {
            final long now = System.currentTimeMillis();
            // sort according to time distance

            Collections.sort(list, new Comparator<Entity>() {
                @Override
                public int compare(Entity entity1, Entity entity2) {
                    long result = (long)(distance(now, entity1) - distance(now, entity2));
                    if (result > 0) {
                        return 1;
                    }
                    else if (result == 0) {
                        return 0;
                    }
                    else if (result < 0) {
                        return -1;
                    }

                    return 0;
                }
            });
            return findMaaS(list);
        } else {
            return null;
        }
    }

    public EntityList getClosestEntities(String entityName) {
        EntityList result = EntityList.empty();
        final long now = System.currentTimeMillis();
        if (entityName.equals("release")) {
            EntityList releases = getReleases();
            for (Entity release : releases) {
                if (distance(now, release) == 0) {
                    result.add(release);
                }
            }
        } else if (entityName.equals("sprint")) {
            EntityList sprints = getSprints();
            for (Entity sprint : sprints) {
                if (distance(now, sprint) == 0) {
                    result.add(sprint);
                }
            }
        }else {
            result = null;
        }

        return result;
    }



    private void loadTeams(final Entity release) {
        EntityQuery query = new EntityQuery("team");
        query.addColumn("id", 1);
        query.addColumn("name", 1);
        query.setValue("release-id", release.getPropertyValue("id"));
        query.addOrder("name", SortOrder.ASCENDING);
        EntityList list = EntityList.empty();
        try {
            list = entityService.query(query);
        } finally {
            synchronized (this) {
                teamSelector.values = list;
                teamSelector.requestRunning = false;

                if(!teamSelector.values.contains(teamSelector.selected)) {
                    // TODO: choose my team
                    selectTeam(findMyTeam(list, release, ApplicationManager.getUserService().getUser()));
                }
                notifyAll();
            }
        }
    }

    /*private void fireSprintSelected() {
        listeners.fire(new WeakListeners.Action<Listener>() {
            public void fire(Listener listener) {
                listener.onSprintSelected(sprintSelector.selected);
            }
        });
    }

    private void fireTeamSelected() {
        listeners.fire(new WeakListeners.Action<Listener>() {
            public void fire(Listener listener) {
                listener.onTeamSelected(teamSelector.selected);
            }
        });
    }

    private void fireReleaseSelected() {
        listeners.fire(new WeakListeners.Action<Listener>() {
            public void fire(Listener listener) {
                listener.onReleaseSelected(releaseSelector.selected);
            }
        });
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }*/

    public synchronized void selectRelease(final Entity release) {
        if((release != null && release.equals(this.releaseSelector.selected)) ||
                (release == null && this.releaseSelector.selected == null)) {
            return;
        }

        this.releaseSelector.selected = release;

        //cache selected Release
        sharedPreferencesService.setPreference("releaseId", String.valueOf(release.getId()));

        /*if(release != null) {
            if(ServerType.AGM.equals(restService.getServerTypeIfAvailable())) {
                sprintSelector.requestRunning = true;
                teamSelector.requestRunning = true;
                storySelector.requestRunning = true;
                ThreadPoolManager.getInstance().executeOnPooledThread(new Runnable() {
                   @Override
                   public void run() {
                       loadSprints(release);
                       loadTeams(release);
                       loadStories(sprintSelector.selected, teamSelector.selected);

                   }
               });
            }
        } else {
            selectSprint(null);
            selectTeam(null);
        }*/
        //fireReleaseSelected();
    }

    public synchronized void selectSprint(Entity sprint) {
        if((sprint != null && sprint.equals(this.sprintSelector.selected)) ||
                (sprint == null && this.sprintSelector.selected == null)) {
            return;
        }

        this.sprintSelector.selected = sprint;
        //fireSprintSelected();
    }

    public synchronized void selectTeam(Entity team) {
        if((team != null && team.equals(this.teamSelector.selected)) ||
                (team == null && this.teamSelector.selected == null)) {
            return;
        }

        this.teamSelector.selected = team;
        //fireTeamSelected();
    }

    public synchronized Entity getCurrentSprint() {
        if(sprintSelector.values == null) {
            return null;
        }

        for(Entity sprint: sprintSelector.values) {
            if(isCurrentSprint(sprint)) {
                return sprint;
            }
        }

        return  null;
    }

    private synchronized EntityList getValues(final Selector selector, final Runnable loader) {
        while(true) {
            if(selector.requestRunning) {
                // request running
            } else if(selector.values != null) {
                return selector.values;
            } else {
                selector.requestRunning = true;
                ThreadPoolManager.getInstance().executeOnPooledThread(loader);
            }
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    public synchronized EntityList getTeams() {
        final Entity release = this.releaseSelector.selected;
        if(release != null) {
            return getValues(teamSelector, new Runnable() {
                @Override
                public void run() {
                    loadTeams(release);
                }
            });
        } else {
            return null;
        }
    }

    public EntityList getSprints() {
        final Entity release = this.releaseSelector.selected;
        if(release != null) {
            return getValues(sprintSelector, new Runnable() {
                @Override
                public void run() {
                    loadSprints(release);
                }
            });
        } else {
            return null;
        }
    }

    public synchronized EntityList getStories() {
        final Entity sprint = this.sprintSelector.selected;
        final Entity team = this.teamSelector.selected;
        if(sprint != null && team != null) {
            return getValues(storySelector, new Runnable() {
                @Override
                public void run() {
                    loadStories();
                }
            });
        } else {
            return null;
        }
    }

    public synchronized EntityList getStories(Boolean reload) {
        if (reload) {
            storySelector.values = null;
        }
        return getStories();
    }



    public EntityList getReleases() {
        return getValues(releaseSelector, new Runnable() {
            @Override
            public void run() {
                loadReleases();
            }
        });
    }

    public Entity getCurrentRelease() {
        return releaseSelector.selected;
    }

    public static boolean isCurrentSprint(Entity sprint) {
        return "CURRENT".equalsIgnoreCase(sprint.getPropertyValue("tense"));
    }

    public static long distance(long now, Entity entity) {
        try {
            Date startDate = ALM_DATE_FORMAT.parse(entity.getPropertyValue("start-date"));
            if(now < startDate.getTime()) {
                // future release
                return startDate.getTime() - now;
            }

            Date endDate = ALM_DATE_FORMAT.parse(entity.getPropertyValue("end-date"));
            // make end-date inclusive
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DATE, 1);
            endDate = calendar.getTime();

            if(now > endDate.getTime()) {
                return now - endDate.getTime();
            }

            return 0;
        } catch(Exception e) {
            return Long.MAX_VALUE;
        }
    }

    public static int distanceToNow(Entity entity) {
        final long now = System.currentTimeMillis();
        try {
            Date startDate = ALM_DATE_FORMAT.parse(entity.getPropertyValue("start-date"));
            if(now < startDate.getTime()) {
                // future release
                return 1;
            }

            Date endDate = ALM_DATE_FORMAT.parse(entity.getPropertyValue("end-date"));
            // make end-date inclusive
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DATE, 1);
            endDate = calendar.getTime();

            if(now > endDate.getTime()) {
                return -1;
            }

            return 0;
        } catch(Exception e) {
            return -1;
        }
    }

    public Entity lookup(final EntityRef ref) {
        if("release".equals(ref.type)) {
            return find(releaseSelector, ref);
        } else if("release-cycle".equals(ref.type)) {
            return find(sprintSelector, ref);
        } else if("team".equals(ref.type)) {
            return find(teamSelector, ref);
        } else {
            return null;
        }
    }

    private synchronized Entity find(Selector selector, EntityRef ref) {
        if(selector.values != null) {
            int i = selector.values.indexOf(new Entity(ref.type, ref.id));
            if(i >= 0) {
                return selector.values.get(i);
            }
        }
        return null;
    }



/*    @Override
    public void entityLoaded(Entity entity, Event event) {
        // no need to implement as long as nobody modifies these entity types
    }

    @Override
    public void entityNotFound(EntityRef ref, boolean removed) {
    }*/

    public static interface Listener {

        void onReleaseSelected(Entity release);

        void onSprintSelected(Entity sprint);

        void onTeamSelected(Entity team);

    }

    private static class Selector implements Serializable{
        private boolean requestRunning;
        private Entity selected;
        private EntityList values;
    }
}
