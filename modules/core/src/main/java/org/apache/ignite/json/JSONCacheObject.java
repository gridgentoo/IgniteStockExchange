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

package org.apache.ignite.json;

import java.util.*;

/**
 * JSON cache object.
 */
public class JSONCacheObject extends HashMap<Object, Object> {
    /** */
    private static final long serialVersionUID = 0L;

    /**
     * Empty constructor.
     */
    private JSONCacheObject() {
        // No-op.
    }

    /**
     * @param key Field name.
     * @return Field value.
     */
    public Object getField(Object key) {
        return get(key);
    }

    /**
     * Convert JSON object to RestJSONCacheObject
     *
     * @param o Object to convert.
     * @return Converted object.
     */
    public static Object toSimpleObject(Object o) {
        if (o == null)
            return null;

        if (o instanceof Map) {
            Map o1 = (Map)o;

            JSONCacheObject res = new JSONCacheObject();

            for (Object key : o1.keySet())
                res.put(toSimpleObject(key), toSimpleObject(o1.get(key)));

            return res;
        }
        else if (o instanceof List) {
            List o1 = (List) o;

            List<Object> val = new ArrayList<>();

            for (Object v : o1)
                val.add(toSimpleObject(v));

            return val;
        }
        else if (o.getClass().isArray()) {
            Object[] o1 = (Object[]) o;

            List<Object> val = new ArrayList<>();

            for (Object v : o1)
                val.add(toSimpleObject(v));

            return val;
        }

        return o;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JSONCacheObject))
            return false;

        return super.equals(obj);
    }
}
