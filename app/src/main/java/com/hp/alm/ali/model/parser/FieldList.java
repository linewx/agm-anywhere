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

package com.hp.alm.ali.model.parser;

import com.hp.alm.ali.model.Field;
import com.hp.alm.ali.model.type.UserType;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class FieldList extends AbstractList<Field> {

    public static FieldList create(InputStream is) {
        try {
            return new FieldList(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FieldList(InputStream is) throws XmlPullParserException {
        init(is);
    }

    protected void onStartElement(XmlPullParser element) throws XmlPullParserException {
        try {
            String localPart = element.getName();
            if ("Field".equals(localPart)) {
                String name = element.getAttributeValue("", "Name");
                String label = element.getAttributeValue("", "Label");
                label = label != null ? label : name; // requirement.req-ver-stamp has no label
                add(new Field(name, label));
            } else if ("Type".equals(localPart)) {
                Field field = getLast();
                String val = readNextValue(reader);
                if ("Number".equals(val)) {
                    field.setClazz(Integer.class);
                } else if ("UsersList".equals(val)) {
                    field.setClazz(UserType.class);
                } else if ("Memo".equals(val)) {
                    field.setBlob(true);
                }
            } else if ("List-Id".equals(localPart)) {
                int listId = Integer.valueOf(AbstractList.readNextValue(reader));
                if (listId == 0) {
                    // ALM 12.00.6489.0 deployed in Center returns zero for defect summary field
                    return;
                }
                getLast().setListId(listId);
            } else if ("Editable".equals(localPart)) {
                getLast().setEditable(Boolean.valueOf(AbstractList.readNextValue(reader)));
            } else if ("Filterable".equals(localPart)) {
                getLast().setCanFilter(Boolean.valueOf(AbstractList.readNextValue(reader)));
            } else if ("Required".equals(localPart)) {
                getLast().setRequired(Boolean.valueOf(AbstractList.readNextValue(reader)));
            } else if ("References".equals(localPart)) {
                String referenceTypeField = element.getAttributeValue("", "ReferenceTypeField");
                if (referenceTypeField != null) {
                    getLast().setReferencedTypeField(referenceTypeField);
                }
            } else if ("RelationReference".equals(localPart) && getLast().getReferencedTypeField() == null) {
                getLast().setReferencedType(element.getAttributeValue("", "ReferencedEntityType"));
            }
        } catch (IOException e) {

        }
    }
}
