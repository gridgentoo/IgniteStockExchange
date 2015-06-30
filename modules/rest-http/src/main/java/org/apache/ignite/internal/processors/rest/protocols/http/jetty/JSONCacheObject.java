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

import net.sf.json.*;

import java.util.*;

/**
 * Json cache object.
 */
public class JSONCacheObject {
    /** Fields map. */
    private Map<Object, Object> fields = new HashMap<>();

    public JSONCacheObject() {

    }

    public JSONCacheObject(JSONObject o) {
        for (Object key : o.keySet())
            addField(toSimpleObject(key), toSimpleObject(o.get(key)));
    }

    private Object toSimpleObject(Object o) {
        if (o instanceof JSONObject) {
            JSONObject o1 = (JSONObject)o;
            JSONCacheObject res = new JSONCacheObject();

            for (Object key : o1.keySet())
                res.addField(toSimpleObject(key), toSimpleObject(o1.get(key)));

            return res;
        }
        else if (o instanceof JSONArray) {
            JSONArray o1 = (JSONArray) o;
            List<Object> val = new ArrayList<>();
            for (Object v : o1)
                val.add(toSimpleObject(v));

            return val;
        }

        return o;
    }

    /**
     * @param key Field name.
     * @param val Field value.
     */
    public void addField(Object key, Object val) {
        fields.put(key, val);
    }

    /**
     * @param key Field name.
     * @return Field value.
     */
    public Object getField(Object key) {
        return fields.get(key);
    }

    /**
     * @return Fields key set.
     */
    public Set<Object> keys() {
        return fields.keySet();
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        //TODO:
        return fields.hashCode();
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JSONCacheObject))
            return false;

        JSONCacheObject obj0 = (JSONCacheObject) obj;

        if (fields.size() != obj0.fields.size())
            return false;

        for (Object key : obj0.keys()) {
            if (!fields.containsKey(key))
                return false;

            if (!obj0.getField(key).equals(getField(key)))
                return false;
        }

        return true;
    }
}
