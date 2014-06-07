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

import javax.xml.namespace.QName;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.apache.http.protocol.HTTP;

public class ProjectExtensionsList extends AbstractList<String[]> {

    protected void onStartElement(XmlPullParser element) throws XmlPullParserException {
        String tagName = element.getName();
        if("Extension".equals(tagName)) {
            String name = element.getAttributeValue("", "Name");
            add(new String[2]);
            getLast()[0] = name;
        } else if("Version".equals(tagName)) {
            getLast()[1] = readNextValue();
        }
    }

    public static ProjectExtensionsList create(InputStream is) {
        ProjectExtensionsList extensionsList = new ProjectExtensionsList();
        extensionsList.initNoEx(is);
        return extensionsList;
    }
}
