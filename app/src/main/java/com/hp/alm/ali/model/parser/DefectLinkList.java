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

import com.hp.alm.ali.model.Entity;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefectLinkList extends EntityList {

    private static Set<String> PROPS_DESERIALIZE = new HashSet<String>(Arrays.asList("id", "comment", "first-endpoint-id", "second-endpoint-id", "second-endpoint-name", "second-endpoint-status", "second-endpoint-type", "owner", "creation-time"));
    private static List<String> PROPS_SERIALIZE = Arrays.asList("comment", "first-endpoint-id", "second-endpoint-id", "second-endpoint-type", "owner", "creation-time");

    public static DefectLinkList create(InputStream is) {
        return create(is, true);
    }

    public static DefectLinkList create(InputStream is, boolean complete) {
        try {
            return new DefectLinkList(is, complete);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private DefectLinkList(InputStream is, boolean complete) throws XmlPullParserException {
        super(is, complete);
    }

    protected void onStartElement(XmlPullParser element) throws XmlPullParserException {
        String localPart = element.getName();
        if("defect-link".equals(localPart)) {
            Entity entity = new Entity(localPart);
            add(entity);
        } else if(PROPS_DESERIALIZE.contains(localPart)) {
            getLast().setProperty(localPart, readNextValue());
        }
    }

    public static Element linkToXml(Entity entity) throws ParserConfigurationException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element element = doc.createElement("defect-link");
        for(String property: PROPS_SERIALIZE) {
            if(entity.isInitialized(property)) {
                Element child = doc.createElement(property);
                child.setTextContent(entity.getPropertyValue(property));
                element.appendChild(child);
            }
        }
        return element;
    }
}
