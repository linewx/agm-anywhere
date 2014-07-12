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

package com.hp.saas.agm.service;

import com.hp.saas.agm.rest.client.InputData;
import com.hp.saas.agm.rest.client.RestClient;
import com.hp.saas.agm.rest.client.ResultInfo;

public class MyDeleteMethod implements MyMethod {

    @Override
    public String getName() {
        return "DELETE";
    }

    @Override
    public int execute(RestClient client, InputData input, ResultInfo info, String template, Object... params) {
        return client.delete(info, template, params);
    }
}
