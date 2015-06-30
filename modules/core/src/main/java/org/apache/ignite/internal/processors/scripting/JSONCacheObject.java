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

package org.apache.ignite.internal.processors.scripting;

import java.util.*;

/**
 * Json cache object.
 */
public class JSONCacheObject {
    /** Fields map. */
    private Map<String, Object> fields = new HashMap<>();

    /**
     * @param key Field name.
     * @param val Field value.
     */
    public void addField(String key, Object val) {
        fields.put(key, val);
    }

    /**
     * @param key Field name.
     * @return Field value.
     */
    public Object getField(String key) {
        return fields.get(key);
    }

    /**
     * @return Fields key set.
     */
    public Set<String> keys() {
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

        for (String key : obj0.keys()) {
            if (!fields.containsKey(key))
                return false;

            if (!obj0.getField(key).equals(getField(key)))
                return false;
        }

        return true;
    }
}
