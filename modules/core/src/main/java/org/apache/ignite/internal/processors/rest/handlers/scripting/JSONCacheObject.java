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

package org.apache.ignite.internal.processors.rest.handlers.scripting;

import jdk.nashorn.api.scripting.JSObject;

import java.util.*;

/**
 * Json cache object.
 */
public class JSONCacheObject implements JSObject {
    Map <Object, Object> fields = new HashMap<Object, Object>();
    /**
     * Empty constructor.
     */
    private JSONCacheObject() {
    }

    /**
     * @param o JSON object.
     */
    public JSONCacheObject(Map o) {
        for (Object key : o.keySet())
            addField(toSimpleObject(key), toSimpleObject(o.get(key)));
    }

    @Override public int hashCode() {
        return fields.hashCode();
    }

    @Override public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JSONCacheObject))
            return false;
        return fields.equals(((JSONCacheObject)obj).fields);
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
     * Convert JSON object to JSONCacheObject
     *
     * @param o Object to convert.
     * @return Converted object.
     */
    public static Object toSimpleObject(Object o) {
        if (o instanceof Map) {
            Map o1 = (Map)o;

            JSONCacheObject res = new JSONCacheObject();

            for (Object key : o1.keySet())
                res.addField(toSimpleObject(key), toSimpleObject(o1.get(key)));

            return res;
        }
        else if (o instanceof List) {
            List o1 = (List) o;

            List<Object> val = new ArrayList<>();

            for (Object v : o1)
                val.add(toSimpleObject(v));

            return val;
        }

        return o;
    }

    @Override public Object call(Object o, Object... objects) {
        return null;
    }

    @Override public Object newObject(Object... objects) {
        return null;
    }

    @Override public Object eval(String s) {
        return null;
    }

    @Override public Object getMember(String s) {
        return fields.get(s);
    }

    @Override public Object getSlot(int i) {
        return null;
    }

    @Override public boolean hasMember(String s) {
        return fields.containsKey(s);
    }

    @Override
    public boolean hasSlot(int i) {
        return false;
    }

    @Override
    public void removeMember(String s) {

    }

    @Override
    public void setMember(String s, Object o) {

    }

    @Override
    public void setSlot(int i, Object o) {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Object> values() {
        return fields.values();
    }

    @Override
    public boolean isInstance(Object o) {
        return false;
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return false;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isStrictFunction() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public double toNumber() {
        return 0;
    }
}
