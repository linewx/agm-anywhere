// (C) Copyright 2003-2011 Hewlett-Packard Development Company, L.P.

package com.hp.saas.agm.core.model.parser;

import com.hp.saas.agm.core.model.Entity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityList extends AbstractList<Entity> {

    private String fieldName;
    private int total;
    private boolean complete;
    private boolean inRelation = false;
    private String fieldPrefix = "";

    public static EntityList create(InputStream is) {
        return create(is, true);
    }

    public static EntityList create(InputStream is, boolean complete) {
        try {
            return new EntityList(is, complete);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    public static EntityList empty() {
        return new EntityList();
    }

    private EntityList() {
    }

    protected EntityList(InputStream is, boolean complete) throws XmlPullParserException {
        this.complete = complete;
        init(is);
    }

    protected void onEndElement(XmlPullParser element) throws XmlPullParserException {
        String localPart = element.getName();
        if("Relation".equals(localPart)) {
            inRelation = false;
            fieldPrefix = "";
        }
    }

    protected void onStartElement(XmlPullParser element) throws XmlPullParserException {
        String localPart = element.getName();
        if("Relation".equals(localPart)) {
            inRelation = true;
        } else if("Entity".equals(localPart)) {
            String type = element.getAttributeValue("", "Type");
            if(inRelation) {
                fieldPrefix = type + ".";
            } else {
                Entity entity = new Entity(type);
                entity.setComplete(complete);
                add(entity);
            }
        } else if("Field".equals(localPart)) {
            fieldName = fieldPrefix + element.getAttributeValue("", "Name");
            // there is no <Value> element at all for requirement.target-rel, make sure
            // the property is initialized
            getLast().setProperty(fieldName, null);
        } else if("Value".equals(localPart)) {
            String value = readNextValue();
            getLast().setProperty(fieldName, value);
        } else if("Entities".equals(localPart)) {
            total = Integer.valueOf(element.getAttributeValue("", "TotalResults"));
        }
    }

    public int getTotal() {
        return total;
    }

    public List<Integer> getIds() {
        List<Integer> ids = new ArrayList<Integer>();
        for(Entity entity: this) {
            ids.add(entity.getId());
        }
        return Collections.unmodifiableList(ids);
    }

    public List<String> getIdStrings() {
        List<String> ids = new ArrayList<String>();
        for(Entity entity: this) {
            ids.add(String.valueOf(entity.getId()));
        }
        return Collections.unmodifiableList(ids);
    }
}
