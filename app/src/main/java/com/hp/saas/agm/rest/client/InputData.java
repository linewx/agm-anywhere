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

package com.hp.saas.agm.rest.client;

/*import org.apache.commons.httpclient.methods.InputStreamEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;*/
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

public class InputData {
    private final InputStream dataStream;
    private long size;
    private final String data;
    private final Map<String, String> headers;

    private InputData(InputStream dataStream, long size, String data, Map<String, String> headers) {
        if (dataStream != null && data != null) {
            throw new IllegalArgumentException("dataStream != null && data != null");

        }
        this.dataStream = dataStream;
        this.data = data;
        this.size = size;
        this.headers = headers;
    }

    public static InputData create(String data, Map<String, String> headers) {
        return new InputData(null, -2, data, headers);
    }

    public static InputData create(InputStream bodyStream, Map<String, String> headers) {
        return new InputData(bodyStream, -2, null, headers);
    }

    public static InputData create(InputStream bodyStream, long size, Map<String, String> headers) {
        return new InputData(bodyStream, size, null, headers);
    }

    public static InputData create(InputStream bodyStream) {
        return new InputData(bodyStream, -2, null, Collections.<String, String>emptyMap());
    }

    public static InputData create(String data) {
        return new InputData(null, -2, data, Collections.<String, String>emptyMap());
    }

    Map<String, String> getHeaders() {
        return headers;
    }

    public HttpEntity getRequestEntity(String encoding) {
        if (data != null) {
            HttpEntity requestEntity;
            try {
                requestEntity = new StringEntity(data,  encoding);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            return requestEntity;
        } else if (dataStream != null) {
            return new InputStreamEntity(dataStream, size);
        } else {
            return null;
        }


    }
}
