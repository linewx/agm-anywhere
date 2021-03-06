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

package com.hp.saas.agm.core.filter;

import com.hp.saas.agm.core.translate.ValueCallback;
import com.hp.saas.agm.core.translate.filter.FilterResolver;
import android.text.TextUtils;


import java.util.Arrays;
import java.util.LinkedList;

public class MultipleItemsResolver implements FilterResolver {

    public static final String NO_VALUE = "\"\"";
    public static final String NO_VALUE_DESC = "(no value)";

    @Override
    public String resolveDisplayValue(String value, final ValueCallback onValue) {
        value = value.replace(NO_VALUE, NO_VALUE_DESC);
        onValue.value(value);
        return value;
    }

    @Override
    public String toRESTQuery(String value) {
        LinkedList<String> list = new LinkedList<String>();
        for(String item: Arrays.asList(value.split(";"))) {
            if(NO_VALUE.equals(item)) {
                list.add(NO_VALUE);
            } else {
                list.add("\"" + item + "\"");
            }
        }
        return TextUtils.join(" OR ", list);
    }
}
