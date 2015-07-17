/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.rest.protocols.http.jetty;

import org.apache.ignite.internal.processors.scripting.*;
import org.apache.ignite.json.*;

import javax.json.*;
import java.util.*;

/**
 * Converter for glassfish objects.
 */
public class RestGlassFishScriptingConverter extends IgniteScriptingConverter {
    /** {@inheritDoc} */
    @Override public Object toJavaObject(Object o) {
        if (o == null)
            return null;

        if (o instanceof Map) {
            Map o1 = (Map)o;

            JSONCacheObject res = new JSONCacheObject();

            for (Object key : o1.keySet())
                res.put(toJavaObject(key), toJavaObject(o1.get(key)));

            return res;
        }
        else if (o instanceof List) {
            List o1 = (List) o;

            List<Object> val = new ArrayList<>();

            for (Object v : o1)
                val.add(toJavaObject(v));

            return val;
        }
        else if (o instanceof JsonString)
            return ((JsonString) o).getString();
        else if (o instanceof JsonNumber)
            return ((JsonNumber) o).intValue();
        else if (o.equals(JsonValue.FALSE))
            return false;
        else if (o.equals(JsonValue.TRUE))
            return true;
        else if (o.equals(JsonValue.NULL))
            return null;

        return o;
    }
}
