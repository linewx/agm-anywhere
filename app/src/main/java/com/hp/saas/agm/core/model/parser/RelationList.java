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

package com.hp.saas.agm.core.model.parser;

import com.hp.saas.agm.core.model.Relation;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/*import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;*/
import java.io.InputStream;

public class RelationList extends AbstractList<Relation> {

    public static RelationList create(InputStream is) {
        try {
            return new RelationList(is);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private String targetEntity;
    private String relationName;

    private RelationList(InputStream is) throws XmlPullParserException {
        init(is);
    }

    @Override
    protected void onStartElement(XmlPullParser element) throws XmlPullParserException {
        String localPart = element.getName();
        if("TargetEntity".equals(localPart)) {
            targetEntity = readNextValue();
            add(new Relation(relationName, targetEntity));
        } else if("Alias".equals(localPart)) {
            if(Boolean.parseBoolean(element.getAttributeValue("", "Unique"))) {
                String name = element.getAttributeValue("", "Name");
                getLast().addAlias(name);
            }
        } else if("Relation".equals(localPart)) {
            relationName = element.getAttributeValue("", "Name");
        }
    }
}
