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

package com.hp.alm.ali.model.type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.hp.alm.ali.translate.Translator;
import com.hp.alm.ali.translate.ValueCallback;
import com.hp.alm.ali.filter.FilterFactory;
import com.hp.alm.ali.translate.filter.FilterResolver;
import com.hp.alm.ali.manager.ApplicationManager;
import com.hp.alm.ali.translate.TranslateService;
import com.hp.alm.ali.filter.MultipleItemsResolver;
import com.hp.alm.ali.model.Entity;
import com.hp.alm.ali.translate.expr.*;

public abstract class AbstractTargetType implements Type, FilterResolver, Translator {

    private TranslateService translateService;
    private String targetType;

    public AbstractTargetType(String targetType) {
        this.targetType = targetType;
        translateService = ApplicationManager.getTranslateService();
    }

    @Override
    public FilterFactory getFilterFactory(boolean multiple) {
       /* if(multiple) {
            // requirements endpoint needs ^Releases\Release^ format when filtering
            return new FilterFactoryImpl(project, targetType, multiple, false);
        } else {
            // but it needs plain ID when editing
            return new FilterFactoryImpl(project, targetType, multiple, true);
        }*/
        return null;
    }

    @Override
    public FilterResolver getFilterResolver() {
        return this;
    }

    @Override
    public Translator getTranslator() {
        return this;
    }

    @Override
    public String resolveDisplayValue(String value, ValueCallback onValue) {
        onValue.value(value);
        return value;
    }

    @Override
    public String toRESTQuery(String value) {
        return value;
    }

    @Override
    public String translate(String value, ValueCallback callback) {
       /* try {
            // need separate handling for the filter (multiple values) and editor (single value) context
            Integer.parseInt(value);
            return translateService.getReferenceTranslator(targetType).translate(value, callback);
        } catch (NumberFormatException e) {
            return value;
        }*/
        return value;
    }
}

