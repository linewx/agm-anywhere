// (C) Copyright 2003-2011 Hewlett-Packard Development Company, L.P.

package com.hp.saas.agm.core.model;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.Serializable;
import java.util.*;

final public class Entity implements Serializable {

    // TODO: should be synchronized (?)

    private Map<String, Object> props = new HashMap<String, Object>();
    private String entityType;

    // safety mechanism to prevent infinite fetching loop when expected property is not found
    private boolean complete;

    public Entity(String entityType) {
        this.entityType = entityType;
    }

    public Entity(String entityType, int entityId) {
        this.entityType = entityType;
        setProperty("id", String.valueOf(entityId));
    }

    public void setProperty(String property, Object value) {
        props.put(property, value);
    }

    public Object getProperty(String property) {
        return props.get(property);
    }

    public boolean isInitialized(String property) {
        return props.containsKey(property);
    }

    public String getPropertyValue(String property) {
        Object val = props.get(property);
        if(val == null) {
            return "";
        } else {
            return val.toString();
        }
    }

    public String getType() {
        return entityType;
    }

    public int getId() {
        return Integer.valueOf((String) getProperty("id"));
    }

    public boolean matches(Entity entity) {
        HashMap<String, Object> myProps = new HashMap<String, Object>(props);
        myProps.keySet().retainAll(entity.props.keySet());
        return entityType.equals(entity.entityType) && myProps.equals(entity.props);
    }

    public Element toElement(Set<String> fieldsToExport) throws ParserConfigurationException{

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        if(fieldsToExport == null) {
            fieldsToExport = props.keySet();
        }
        Element entity = doc.createElement("Entity");
        entity.setAttribute("Type", entityType);
        Element fields = doc.createElement("Fields");
        entity.appendChild(fields);
        for(String name: fieldsToExport) {
            if(!props.containsKey(name)) {
                continue;
            }
            Element field = doc.createElement("Field");
            fields.appendChild(field);
            field.setAttribute("Name", name);
            Element value = doc.createElement("Value");
            field.appendChild(value);
            Object val = getProperty(name);
            Node valNode = doc.createTextNode((String)val);
            value.appendChild(valNode);
        }
        return entity;
    }

    public Entity getPrimaryEntity() {
        HashMap<String, Object> primary = new HashMap<String, Object>();
        for(String prop: props.keySet()) {
            if(!prop.contains(".")) {
                primary.put(prop, props.get(prop));
            }
        }
        if(primary.size() < props.size()) {
            Entity copy = new Entity(entityType);
            copy.props.putAll(primary);
            return copy;
        } else {
            return this;
        }
    }

    public List<Entity> getRelatedEntities() {
        HashMap<String, Entity> map = new HashMap<String, Entity>();
        for(String prop: props.keySet()) {
            int p = prop.indexOf(".");
            if(p >= 0) {
                String type = prop.substring(0, p);
                Entity related = map.get(type);
                if(related == null) {
                    related = new Entity(type);
                    map.put(type, related);
                }
                related.setProperty(prop.substring(p + 1), props.get(prop));
            }
        }
        return new LinkedList<Entity>(map.values());
    }

    public void mergeEntity(Entity other) {
        for(String prop: other.props.keySet()) {
            props.put(prop, other.props.get(prop));
        }
    }

    public void mergeRelatedEntity(Entity other) {
        for(String prop: other.props.keySet()) {
            props.put(other.getType() + "." + prop, other.props.get(prop));
        }
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(!(o instanceof Entity)) {
            return false;
        } else {
            Entity other = (Entity) o;
            return other.entityType.equals(entityType) && other.getId() == getId();
        }
    }

    public int hashCode() {
        return entityType.hashCode() * 31 + getId();
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Entity clone() {
        Entity copy = new Entity(entityType);
        copy.complete = complete;
        copy.props.putAll(props);
        return copy;
    }
}
