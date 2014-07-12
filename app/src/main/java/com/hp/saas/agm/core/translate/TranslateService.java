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

package com.hp.saas.agm.core.translate;

import com.hp.saas.agm.core.model.Field;
import com.hp.saas.agm.core.translate.filter.FilterResolver;

public class TranslateService {

    public static final String LOADING_MESSAGE = "Loading...";
    public static final String NA_TOKEN = "N/A";
/*
    private Project project;
    private SimpleCache cache;*/

    public TranslateService() {

    }

    public String convertQueryModelToREST(Field field, String value) {
        FilterResolver resolver = getFilterResolver(field);
        if(resolver != null) {
            return resolver.toRESTQuery(value);
        } else {
            return value;
        }
    }

    public FilterResolver getFilterResolver(Field field) {
/*        if(Type.class.isAssignableFrom(field.getClazz())) {
            Type type = (Type)project.getComponent(field.getClazz());
            FilterResolver filterResolver = type.getFilterResolver();
            if(filterResolver != null) {
                return filterResolver;
            }
        }
        if(field.getReferencedType() != null) {
            return new ExpressionResolver(getReferenceTranslator(field.getReferencedType()));
        }
        if(field.getListId() != null) {
            return new MultipleItemsResolver();
        }*/
        return null;
    }

    /*public TranslateService(Project project, RestService restService) {
        this.project = project;

        cache = new SimpleCache();

        restService.addServerTypeListener(this);
    }



    public boolean isTranslated(Field field) {
        return getTranslator(field) != null;
    }

    public String convertQueryModelToREST(Field field, String value) {
        FilterResolver resolver = getFilterResolver(field);
        if(resolver != null) {
            return resolver.toRESTQuery(value);
        } else {
            return value;
        }
    }

    public String convertQueryModelToView(Field field, String value, ValueCallback callback) {
        FilterResolver resolver = getFilterResolver(field);
        if(resolver != null) {
            return resolver.resolveDisplayValue(value, callback);
        } else {
            callback.value(value);
            return value;
        }
    }

    public FilterResolver getFilterResolver(Field field) {
        if(Type.class.isAssignableFrom(field.getClazz())) {
            Type type = (Type)project.getComponent(field.getClazz());
            FilterResolver filterResolver = type.getFilterResolver();
            if(filterResolver != null) {
                return filterResolver;
            }
        }
        if(field.getReferencedType() != null) {
            return new ExpressionResolver(getReferenceTranslator(field.getReferencedType()));
        }
        if(field.getListId() != null) {
            return new MultipleItemsResolver();
        }
        return null;
    }

    public Translator getTranslator(Field field) {
        if(Type.class.isAssignableFrom(field.getClazz())) {
            Type type = (Type)project.getComponent(field.getClazz());
            Translator translator = type.getTranslator();
            if(translator != null) {
                return translator;
            }
        }
        if(field.getReferencedType() != null) {
            return getReferenceTranslator(field.getReferencedType());
        }
        return null;
    }

    public Translator getReferenceTranslator(String targetType) {
        return new EntityReferenceTranslator(project, targetType, cache);
    }

    public String translateAsync(Field field, final String value, boolean syncCall, final ValueCallback onValue) {
        return translateAsync(getTranslator(field), value, syncCall, onValue);
    }

    public String translateAsync(Translator translator, final String value, boolean syncCall, final ValueCallback onValue) {
        if((value == null || value.isEmpty()) && !(translator instanceof ContextAware)) {
            if(syncCall) {
                onValue.value(value);
            }
            return value;
        }
        String result = translator.translate(value, new ValueCallback() {
            @Override
            public void value(final String value) {
                UIUtil.invokeLaterIfNeeded(new Runnable() {
                    @Override
                    public void run() {
                        onValue.value(value);
                    }
                });
            }
        });
        if(result != null) {
            if(syncCall) {
                onValue.value(result);
            }
            return result;
        }
        return LOADING_MESSAGE;
    }

    @Override
    public void connectedTo(ServerType serverType) {
        if(serverType.isConnected()) {
            cache.clear();
        }
    }*/
}
