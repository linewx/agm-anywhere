//(C) Copyright 2003-2012 Hewlett-Packard Development Company, L.P.

package com.hp.alm.ali.model.parser;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.apache.http.protocol.HTTP;

/*import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;*/
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public abstract class AbstractList<E> extends ArrayList<E> {

    protected XmlPullParser reader;

    protected AbstractList() {
    }

    protected void initNoEx(InputStream is) {
        try {
            init(is);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    protected void init(InputStream is) throws XmlPullParserException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            reader = factory.newPullParser();
            reader.setInput(is, HTTP.UTF_8);

            int eventType = reader.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = reader.getEventType();
                if (eventType == XmlPullParser.START_TAG) {
                    onStartElement(reader);
                }
                if (eventType == XmlPullParser.END_TAG) {
                    onEndElement(reader);
                }

                reader.next();

            }
        }catch(Exception e) {
            throw new XmlPullParserException("");
        }
    }

    protected AbstractList(InputStream is) throws XmlPullParserException {
        init(is);
    }

    protected String readNextValue() throws XmlPullParserException {
        try {
            return readNextValue(reader);
        }catch(IOException e) {
            throw new XmlPullParserException("");
        }

    }

    protected E getLast() {
        return get(size() - 1);
    }

    public static String readNextValue(XmlPullParser reader) throws XmlPullParserException,IOException {
/*        StringBuffer buf = new StringBuffer();
        while(reader.peek() instanceof Characters) {
            buf.append(((Characters)reader.nextEvent()).getData());
        }
        return buf.toString();*/

        return reader.nextText();
    }

    protected abstract void onStartElement(XmlPullParser element) throws XmlPullParserException;

    protected void onEndElement(XmlPullParser element) throws XmlPullParserException {
    }

}

