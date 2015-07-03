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

import jdk.nashorn.api.scripting.*;
import org.apache.ignite.internal.util.typedef.internal.U;

import java.util.*;

/**
 * Json cache object.
 */
public class JSONCacheObject implements JSObject {
    Map<Object, Object> fields = new HashMap();
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

    public Map<Object, Object> getFields() {
        return fields;
    }

    @Override public int hashCode() {
        return fields.hashCode();
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JSONCacheObject))
            return false;

        JSONCacheObject obj0 = (JSONCacheObject) obj;

        if (fields.size() != obj0.fields.size())
            return false;

        for (Object key : obj0.fields.keySet()) {
            if (!fields.containsKey(key))
                return false;

            if (!obj0.getField(key).equals(getField(key)))
                return false;
        }

        return true;
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
        o = tryConvert(o, Object[].class);

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
        else if (o.getClass().isArray()) {
            Object[] o1 = (Object[]) o;

            List<Object> val = new ArrayList<>();

            for (Object v : o1)
                val.add(toSimpleObject(v));

            return val;
        }

        return o;
    }

    /**
     * @param o Object.
     * @param cl Class.
     */
    private static Object tryConvert(Object o, Class cl) {
        try {
            return ScriptUtils.convert(o, cl);

        } catch (Exception e) {
            //skip.
        }

        return o;
    }

    @Override public Object call(Object o, Object... objects) {
        System.out.println("!!!!CALL");
        return null;
    }

    @Override public Object newObject(Object... objects) {
        System.out.println("!!!!newObject");
        return null;
    }

    @Override public Object eval(String s) {
        System.out.println("!!!!eval");
        return null;
    }

    @Override public Object getMember(String s) {
        System.out.println("!!!!getMember + " + s);
        return fields.get(s);
    }

    @Override public Object getSlot(int i) {
        System.out.println("!!!!getSlot");
        return null;
    }

    @Override public boolean hasMember(String s) {
        System.out.println("!!!!hasMember");
        return fields.containsKey(s);
    }

    @Override public boolean hasSlot(int i) {
        System.out.println("!!!!hasSlot");
        return false;
    }

    @Override public void removeMember(String s) {
        System.out.println("!!!!removeMember");
        fields.remove(s);
    }

    @Override public void setMember(String s, Object o) {
        System.out.println("!!!!setMember");
        fields.put(s, o);
    }

    @Override public void setSlot(int i, Object o) {
        System.out.println("!!!!setSlot");

    }

    @Override public Set<String> keySet() {
        System.out.println("!!!!keySet");
        Set<String> keys = new HashSet<>();

        for (Object o : keys) {
            if (!(o instanceof JSONCacheObject))
                keys.add(o.toString());
        }

        return keys;
    }

    @Override public Collection<Object> values() {
        System.out.println("!!!!values");
        return fields.values();
    }

    @Override public boolean isInstance(Object o) {
        System.out.println("!!!!isInstance");
        return false;
    }

    @Override public boolean isInstanceOf(Object o) {
        System.out.println("!!!!isInstanceOf");
        return false;
    }

    @Override public String getClassName() {
        System.out.println("!!!!getClassName");
        return U.getSimpleName(JSONCacheObject.class);
    }

    @Override public boolean isFunction() {
        System.out.println("!!!!isFunction");
        return false;
    }

    @Override public boolean isStrictFunction() {
        System.out.println("!!!!isStrictFunction");
        return false;
    }

    @Override public boolean isArray() {
        System.out.println("!!!!isArray");
        return false;
    }

    @Override public double toNumber() {
        return 0;
    }
}
