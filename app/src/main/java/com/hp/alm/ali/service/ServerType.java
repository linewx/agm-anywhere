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

package com.hp.alm.ali.service;

import com.hp.alm.ali.model.*;
public enum ServerType {

    CONNECTING("Connecting..."),
    NONE("Not Connected"),
    NEEDS_PASSWORD("Needs Password"),
    ALM11("ALM 11", MayaStrategy.class),
    ALI("ALI 1.x", AliStrategy.class),
    ALI2("ALI 2.0", Ali2Strategy.class),
    ALM11_5("ALM 11.5x*", ApolloStrategy.class),
    ALI11_5("ALM 11.5x", ApolloAliStrategy.class),
    ALM12("ALM 12*", Alm12Strategy.class),
    ALI12("ALM 12", Alm12AliStrategy.class),
    AGM("AGM", HorizonStrategy.class);

    private String name;
    private Class<? extends ServerStrategy> clazz;

    private ServerType(String name) {
        this.name = name;
    }

    private ServerType(String name, Class<? extends ServerStrategy> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String toString() {
        return name;
    }

    public boolean isConnected() {
        return clazz != null;
    }

    public Class<? extends ServerStrategy> getClazz() {
        if(clazz == null) {
            throw new RuntimeException();
        } else {
            return clazz;
        }
    }
}
