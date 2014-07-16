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

import android.text.TextUtils;
import com.hp.saas.agm.core.entity.*;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.Metadata;
import com.hp.saas.agm.core.model.Field;

import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.core.model.parser.DefectLinkList;
import com.hp.saas.agm.core.model.ServerStrategy;

import com.hp.saas.agm.manager.ApplicationManager;

import com.hp.saas.agm.utils.IOUtils;
import com.hp.saas.agm.utils.XmlUtils;
import org.apache.http.HttpStatus;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class EntityService {

    private RestService restService;
    private MetadataService metadataService;
    private NewEntityListener.EntityQueryListener entityQueryListener;

/*    private WeakListeners<EntityListener> listeners;*/
    //final private Map<EntityRef, List<EntityListener>> asyncRequests;

    public EntityService(RestService restService, MetadataService metadataService) {
        this.restService = restService;
        this.metadataService = metadataService;
/*
        listeners = new WeakListeners<EntityListener>();
        asyncRequests = new HashMap<EntityRef, List<EntityListener>>();*/
    }

/*    public void addEntityListener(EntityListener listener) {
        listeners.add(listener);
    }

    public void removeEntityListener(EntityListener listener) {
        listeners.remove(listener);
    }*/

/*    public void refreshEntity(EntityRef ref) {
        getEntityAsync(ref, null, EntityListener.Event.REFRESH);
    }

    public void getEntityAsync(EntityRef ref, EntityListener callback) {
        getEntityAsync(ref, callback, EntityListener.Event.GET);
    }*/

   /* private void getEntityAsync(final EntityRef ref, final EntityListener callback, final EntityListener.Event event) {
        synchronized (asyncRequests) {
            List<EntityListener> list = asyncRequests.get(ref);
            if(list != null) {
                if(callback != null) {
                    list.add(callback);
                }
                return;
            }
            list = new LinkedList<EntityListener>();
            if(callback != null) {
                list.add(callback);
            }
            asyncRequests.put(ref, list);
        }
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                List<EntityListener> list;
                Entity entity;
                try {
                    entity = getEntity(ref);
                } catch(Exception e) {
                    synchronized (asyncRequests) {
                        list = asyncRequests.remove(ref);
                    }
                    for(EntityListener listener: list) {
                        listener.entityNotFound(ref, false);
                    }
                    fireEntityNotFound(ref, false);
                    return;
                }
                synchronized (asyncRequests) {
                    list = asyncRequests.remove(ref);
                }
                for(EntityListener listener: list) {
                    listener.entityLoaded(entity, event);
                }
                fireEntityLoaded(entity, event);
            }
        });
    }*/


    public Entity getEntity(EntityRef ref) {
        EntityQuery query = new EntityQuery(ref.type);
        Metadata metadata = metadataService.getEntityMetadata(ref.type);
        for(Field field: metadata.getAllFields().values()) {
            // list all fields to have compound fields fetched too
            query.addColumn(field.getName(), 1);
        }
        query.setValue("id", String.valueOf(ref.id));
        query.setPropertyResolved("id", true);
        EntityList list = doQuery(query, true);
        if(list.isEmpty()) {
            throw new RuntimeException("Entity not found: "+ref);
        } else {
            return list.get(0);
        }
    }


    private EntityList parse(InputStream is, boolean complete) {
        return EntityList.create(is, complete);
    }

    public EntityList query(EntityQuery query) {
        return doQuery(query, false);
    }

    public InputStream queryForStream(EntityQuery query) {
        EntityRef parent = query.getParent();
        if(parent == null) {
            return restService.getForStream("{0}s?{1}", query.getEntityType(), queryToString(query));
        } else {
            return restService.getForStream("{0}s/{1}/{2}s?{3}", parent.type, parent.id, query.getEntityType(), queryToString(query));
        }
    }

    private void fireQueryEvent(EntityQuery query, byte[] results) {
        entityQueryListener.query(query, results);
    }
    private EntityList doQuery(EntityQuery query, boolean complete) {
        InputStream is = queryForStream(query);
        if (entityQueryListener != null) {
            try {
                byte[] byteArray = IOUtils.toByteArray(is);
                is = null;
                is = new ByteArrayInputStream(byteArray);
                fireQueryEvent(query, byteArray);
            }catch(IOException e) {
                e.printStackTrace();
            }
        }



        if("defect-link".equals(query.getEntityType()) && restService.getServerStrategy().hasSecondLevelDefectLink()) {
            return DefectLinkList.create(is, complete);
        } else {
            return parse(is, complete);
        }
    }

    public Entity getDefectLink(int defectId, int linkId) {
        if(!restService.getServerStrategy().hasSecondLevelDefectLink()) {
            EntityQuery linkQuery = new EntityQuery("defect-link");
            linkQuery.setValue("id", String.valueOf(linkId));
            linkQuery.setPropertyResolved("id", true);
            return doQuery(linkQuery, true).get(0);
        } else {
            // in ALM 11 the two level query doesn't work correctly on the second level and ID must be specified in the path
            return DefectLinkList.create(restService.getForStream("defects/{0}/defect-links/{1}", defectId, linkId), true).get(0);
        }
    }

    private String queryToString(EntityQuery query) {
        ServerStrategy cust = restService.getServerStrategy();
        EntityQuery clone = cust.preProcess(query.clone());
        StringBuffer buf = new StringBuffer();
        buf.append("fields=");
        LinkedHashMap<String,Integer> columns = clone.getColumns();
        buf.append(TextUtils.join("," ,columns.keySet().toArray(new String[columns.size()])));
        buf.append("&query=");
        buf.append(EntityQuery.encode("{" + filterToString(clone, ApplicationManager.getMetadataService()) + "}"));
        buf.append("&order-by=");
        buf.append(EntityQuery.encode("{" + orderToString(clone) + "}"));
        if(query.getPageSize() != null) {
            buf.append("&page-size=");
            buf.append(query.getPageSize());
        }
        if(query.getStartIndex() != null) {
            buf.append("&start-index=");
            buf.append(query.getStartIndex());
        }
        return buf.toString();
    }

    private String filterToString(EntityFilter<? extends EntityFilter> filter, String prefix, MetadataService metadataService) {
        StringBuffer buf = new StringBuffer();
        for(String prop: filter.getPropertyMap().keySet()) {
            String val = filter.getPropertyMap().get(prop);
            if("".equals(val)) {
                continue;
            }

            if(buf.length() > 0) {
                buf.append("; ");
            }
            if(prefix != null) {
                buf.append(prefix).append(".");
            }
            buf.append(prop);
            buf.append("[");

            if(!filter.isResolved(prop)) {
                Field field = metadataService.getEntityMetadata(filter.getEntityType()).getField(prop);
                val = ApplicationManager.getTranslateService().convertQueryModelToREST(field, val);
            }

            buf.append(val);
            buf.append("]");
        }
        return buf.toString();
    }

    private String filterToString(EntityQuery query, MetadataService metadataService) {
        StringBuffer buf = new StringBuffer();
        buf.append(filterToString(query, null, metadataService));
        Map<String, EntityCrossFilter> crossFilters = query.getCrossFilters();
        for(String alias: crossFilters.keySet()) {
            EntityCrossFilter cf = crossFilters.get(alias);
            if(buf.length() > 0) {
                buf.append("; ");
            }
            buf.append(filterToString(cf, alias, metadataService));

            if(!cf.isInclusive()) {
                buf.append("; ").append(cf.getEntityType()).append(".inclusive-filter[false]");
            }
        }
        return buf.toString();
    }

    private String orderToString(EntityQuery query) {
        StringBuffer buf = new StringBuffer();
        LinkedHashMap<String, SortOrder> order = query.getOrder();
        for(String name: order.keySet()) {
            if(buf.length() > 0) {
                buf.append("; ");
            }
            buf.append(name);
            buf.append("[");
            buf.append(order.get(name) == SortOrder.ASCENDING? "ASC": "DESC" );
            buf.append("]");
        }
        return buf.toString();
    }

    private Entity updateOldDefectLink(Entity entity, boolean silent, boolean fireUpdate) {
        /*String xml = new XMLOutputter().outputString(DefectLinkList.linkToXml(entity));
        MyResultInfo result = new MyResultInfo();
        if(restService.put(xml, result, "defects/{0}/defect-links/{1}", entity.getPropertyValue("first-endpoint-id"), entity.getId()) != HttpStatus.SC_OK) {
            if(!silent) {
                errorService.showException(new RestException(result));
            }
            return null;
        } else {
            Entity resultEntity = DefectLinkList.create(result.getBodyAsStream()).get(0);
            if(fireUpdate) {
                fireEntityLoaded(resultEntity, EntityListener.Event.GET);
            }
            return resultEntity;
        }*/
        return null;
    }

    public Entity updateEntity(Entity entity, Set<String> fieldsToUpdate, boolean silent) {
        return updateEntity(entity, fieldsToUpdate, silent, false, true);
    }

    public Entity updateEntity(Entity entity, Set<String> fieldsToUpdate, boolean silent, boolean reloadOnFailure) {
        return updateEntity(entity, fieldsToUpdate, silent, reloadOnFailure, true);
    }

    public Entity updateEntity(Entity entity, Set<String> fieldsToUpdate, boolean silent, boolean reloadOnFailure, boolean fireUpdate) {
        if("defect-link".equals(entity.getType()) && restService.getServerStrategy().hasSecondLevelDefectLink()) {
            return updateOldDefectLink(entity, silent, fireUpdate);
        }

        String xml = "";
        try {
            Element element = entity.toElement(fieldsToUpdate);
            xml = XmlUtils.getStringFromXml(element, true);
        }catch(Exception e) {
            e.printStackTrace();
        }

        MyResultInfo result = new MyResultInfo();
        if(restService.put(xml, result, "{0}s/{1}", entity.getType(), entity.getId()) != HttpStatus.SC_OK) {
            if(!silent) {
               /* errorService.showException(new RestException(result));*/
            }
            if(reloadOnFailure) {
                try {
                    return getEntity(new EntityRef(entity));
                } catch (Exception e)  {
                    // do not report another failure if reload fails
                }
            }
            return null;
        } else {
            if(fireUpdate) {
                return parseEntityAndFireEvent(result.getBodyAsStream(), EntityListener.Event.GET);
            } else {
                return parse(result.getBodyAsStream(), true).get(0);
            }
        }
    }

    private Entity createOldDefectLink(Entity entity, boolean silent) {
        /*String xml = new XMLOutputter().outputString(DefectLinkList.linkToXml(entity));
        MyResultInfo result = new MyResultInfo();
        if(restService.post(xml, result, "defects/{0}/defect-links", entity.getPropertyValue("first-endpoint-id")) != HttpStatus.SC_CREATED) {
            if(!silent) {
                errorService.showException(new RestException(result));
            }
            return null;
        } else {
            Entity resultEntity = DefectLinkList.create(result.getBodyAsStream()).get(0);
            fireEntityLoaded(resultEntity, EntityListener.Event.CREATE);
            return resultEntity;
        }*/
        return null;
    }

    public Entity createEntity(Entity entity, boolean silent) {
        if("defect-link".equals(entity.getType()) && restService.getServerStrategy().hasSecondLevelDefectLink()) {
            return createOldDefectLink(entity, silent);
        }
        //String xml = new XMLOutputter().outputString(entity.toElement(null));
        String xml = "";
        try {
            xml = entity.toElement(null).toString();
        }catch(Exception e) {
            //
        }

        MyResultInfo result = new MyResultInfo();
        if(restService.post(xml, result, "{0}s", entity.getType()) != HttpStatus.SC_CREATED) {
            if(!silent) {
                /*errorService.showException(new RestException(result));*/
            }
            return null;
        } else {
            return parseEntityAndFireEvent(result.getBodyAsStream(), EntityListener.Event.CREATE);
        }
    }

    private Entity parseEntityAndFireEvent(InputStream is, EntityListener.Event event) {
        Entity resultEntity = parse(is, true).get(0);
        fireEntityLoaded(resultEntity, event);
        return resultEntity;
    }

    public Entity lockEntity(Entity entity, boolean silent) {
        Entity locked = doLock(new EntityRef(entity), silent);
        if(locked != null) {
            if(!locked.matches(entity)) {
                if(!silent) {
                    //Messages.showDialog("Item has been recently modified on the server. Local values have been updated to match the up-to-date revision.", "Entity Update", new String[]{"Continue"}, 0, Messages.getInformationIcon());
                }
                fireEntityLoaded(locked, EntityListener.Event.GET);
            }
        }
        return locked;
    }

    private boolean deleteOldDefectLink(Entity entity) {
        MyResultInfo result = new MyResultInfo();
        if(restService.delete(result, "defects/{0}/defect-links/{1}", entity.getPropertyValue("first-endpoint-id"), entity.getId()) != HttpStatus.SC_OK) {
            //errorService.showException(new RestException(result));
            return false;
        } else {
            fireEntityNotFound(new EntityRef(entity), true);
            return true;
        }
    }

    public boolean deleteEntity(Entity entity) {
        if("defect-link".equals(entity.getType()) && restService.getServerStrategy().hasSecondLevelDefectLink()) {
            return deleteOldDefectLink(entity);
        }
        MyResultInfo result = new MyResultInfo();
        if(restService.delete(result, "{0}s/{1}", entity.getType(), entity.getId()) != HttpStatus.SC_OK) {
            /*errorService.showException(new RestException(result));*/
            return false;
        } else {
            fireEntityNotFound(new EntityRef(entity), true);
            return true;
        }
    }

    private Entity doLock(EntityRef ref, boolean silent) {
        MyResultInfo result = new MyResultInfo();
        int httpStatus = restService.post("", result, "{0}s/{1}/lock", ref.type, ref.id);
        if(httpStatus != HttpStatus.SC_OK && httpStatus != HttpStatus.SC_CREATED) {
            if(!silent) {
              /*  errorService.showException(new RestException(result));*/
            }
            return null;
        }
        return parse(result.getBodyAsStream(), true).get(0);
    }

    public void unlockEntity(Entity entity) {
        restService.delete("{0}s/{1}/lock", entity.getType(), String.valueOf(entity.getId()));
    }

    public void fireEntityLoaded(final Entity entity, final EntityListener.Event event) {
       /* listeners.fire(new WeakListeners.Action<EntityListener>() {
            public void fire(EntityListener listener) {
                listener.entityLoaded(entity, event);
            }
        });*/
    }

    public void fireEntityNotFound(final EntityRef ref, final boolean removed) {
      /*  listeners.fire(new WeakListeners.Action<EntityListener>() {
            public void fire(EntityListener listener) {
                listener.entityNotFound(ref, removed);
            }
        });*/
    }

    public void requestCachedEntity(final EntityRef ref, final List<String> properties, final EntityListener callback) {
        /*ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                final LinkedList<Entity> done = new LinkedList<Entity>();
                listeners.fire(new WeakListeners.Action<EntityListener>() {
                    public void fire(EntityListener listener) {
                        if(done.isEmpty() && listener instanceof CachingEntityListener) {
                            Entity cached = ((CachingEntityListener) listener).lookup(ref);
                            if(cached != null) {
                                for(String property: properties) {
                                    if(!cached.isInitialized(property)) {
                                        return;
                                    }
                                }
                                done.add(cached);
                            }
                        }
                    }
                });
                if(done.isEmpty()) {
                    // all properties are fetched. possible optimization is to request only properties from the
                    // current request + properties initialized in cached value (if any)
                    getEntityAsync(ref, callback);
                } else {
                    callback.entityLoaded(done.getFirst(), EntityListener.Event.CACHE);
                }
            }
        });*/
    }

    public void setEntityQueryListener(NewEntityListener.EntityQueryListener entityQueryListener) {
        this.entityQueryListener = entityQueryListener;
    }

    public void clearEntityQueryListener() {
        this.entityQueryListener = null;
    }

}
