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

package com.hp.alm.ali.entity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

public abstract class AbstractEntityFilter<E extends EntityFilter> implements EntityFilter<E>, JDOMSerialization {

    protected Map<String, String> props = new HashMap<String, String>();
    protected Set<String> resolvedFilters = new HashSet<String>();
    protected String entityType;

    protected AbstractEntityFilter(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public String getEntityType() {
        return entityType;
    }

    @Override
    public synchronized boolean setValue(String prop, String value) {
        if(value == null || value.isEmpty()) {
            if(props.containsKey(prop)) {
                props.remove(prop);
                return true;
            }
        } else if(!value.equals(props.get(prop))) {
            props.put(prop, value);
            return true;
        }
        return false;
    }

    @Override
    public synchronized String getValue(String prop) {
        return props.get(prop);
    }

    @Override
    public synchronized Map<String, String> getPropertyMap() {
        return new HashMap<String, String>(props);
    }

    @Override
    public synchronized void setPropertyResolved(String property, boolean resolved) {
        if(resolved) {
            resolvedFilters.add(property);
        } else {
            resolvedFilters.remove(property);
        }
    }

    @Override
    public synchronized boolean isResolved(String property) {
        return resolvedFilters.contains(property);
    }

    public synchronized void clear() {
        props.clear();
    }

    @Override
    public synchronized boolean isEmpty() {
        return props.isEmpty();
    }

    @Override
    public synchronized Element toElement(String name) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element filter = doc.createElement(name);
        for(String key: props.keySet()) {
            Element child = doc.createElement(key);
            child.setTextContent(props.get(key));
            filter.appendChild(child);
        }


        return filter;
    }

    @Override
    public synchronized void fromElement(Element element) {
        props.clear();
        for(Element child: (List<Element>)element.getChildNodes()) {
            props.put(child.getTagName(), child.getTextContent());
        }
    }

    @Override
    public synchronized void copyFrom(E other) {
        synchronized (other) {
            props.clear();
            props.putAll(other.getPropertyMap());
        }
    }

    @Override
    public abstract E clone();

}
