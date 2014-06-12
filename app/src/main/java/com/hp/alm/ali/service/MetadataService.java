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

package com.hp.alm.ali.service;

import com.hp.alm.ali.model.Metadata;

import java.util.LinkedList;

public class MetadataService extends AbstractCachingService<String, Metadata, AbstractCachingService.Callback<Metadata>> {

    private MetadataSimpleService metadataService;
    private RestService restService;

    public MetadataService(MetadataSimpleService metadataService, RestService restService) {
        this.metadataService = metadataService;
        this.restService = restService;
    }

    @Override
    protected Metadata doGetValue(String entityName) {
        LinkedList<String> list = new LinkedList<String>();
        list.add(entityName);
        list.addAll(restService.getServerStrategy().getCompoundEntityTypes(entityName));

        Metadata result = new Metadata(entityName, false);

        for(String entityType: list) { // make the requests run in parallel if necessary
            metadataService.getEntityMetadataAsync(entityType, null);
        }
        for(String entityType: list) { // collect the results
            Metadata metadata = metadataService.getEntityMetadata(entityType);
            result.add(metadata);
        }

        restService.getServerStrategy().fixMetadata(result);

        return result;
    }

    public Metadata getEntityMetadata(String entityName) {
        return getValue(entityName);
    }

    public static interface MetadataCallback {

        void metadataLoaded(Metadata metadata);

        void metadataFailed();

    }

    public static class DispatchProxy extends Proxy implements DispatchCallback<Metadata> {

        private DispatchProxy(MetadataCallback callback) {
            super(callback);
        }
    }

    public static class Proxy implements Callback<Metadata>, FailureCallback {

        private MetadataCallback callback;

        public static Proxy create(MetadataCallback callback) {
            if(callback instanceof DispatchMetadataCallback) {
                return new DispatchProxy(callback);
            } else {
                return new Proxy(callback);
            }
        }
        public static interface DispatchMetadataCallback extends MetadataCallback {

            // callback to be executed in the context of the dispatching thread

        }

        private Proxy(MetadataCallback callback) {
            this.callback = callback;
        }

        @Override
        public void loaded(Metadata data) {
            if(callback != null) {
                callback.metadataLoaded(data);
            }
        }

        @Override
        public void failed() {
            if(callback != null) {
                callback.metadataFailed();
            }
        }
    }

    /*
    public void loadEntityMetadataAsync(String entityName, MetadataCallback callback) {
        getValueAsync(entityName, Proxy.create(callback));
    }

    @Override
    protected Metadata doGetValue(String entityName) {
        LinkedList<String> list = new LinkedList<String>();
        list.add(entityName);
        list.addAll(restService.getServerStrategy().getCompoundEntityTypes(entityName));

        Metadata result = new Metadata(project, entityName, false);

        for(String entityType: list) { // make the requests run in parallel if necessary
            metadataService.getEntityMetadataAsync(entityType, null);
        }
        for(String entityType: list) { // collect the results
            Metadata metadata = metadataService.getEntityMetadata(entityType);
            result.add(metadata);
        }

        restService.getServerStrategy().fixMetadata(result);

        return result;
    }*/

/*    public Metadata getCachedEntityMetadata(String entityType) {
        return getCachedValue(entityType);
    }

    public Exception getCachedFailure(String entityType) {
        return super.getCachedFailure(entityType);
    }*/

    /*public static class Proxy implements Callback<Metadata>, FailureCallback {

        private MetadataCallback callback;

        public static Proxy create(MetadataCallback callback) {
            if(callback instanceof DispatchMetadataCallback) {
                return new DispatchProxy(callback);
            } else {
                return new Proxy(callback);
            }
        }

        private Proxy(MetadataCallback callback) {
            this.callback = callback;
        }

        @Override
        public void loaded(Metadata data) {
            if(callback != null) {
                callback.metadataLoaded(data);
            }
        }

        @Override
        public void failed() {
            if(callback != null) {
                callback.metadataFailed();
            }
        }
    }*/

    /*public static class DispatchProxy extends Proxy implements DispatchCallback<Metadata> {

        private DispatchProxy(MetadataCallback callback) {
            super(callback);
        }
    }*/

   /* public static interface MetadataCallback {

        void metadataLoaded(Metadata metadata);

        void metadataFailed();

    }

    public static interface DispatchMetadataCallback extends MetadataCallback {

        // callback to be executed in the context of the dispatching thread

    }*/
}
