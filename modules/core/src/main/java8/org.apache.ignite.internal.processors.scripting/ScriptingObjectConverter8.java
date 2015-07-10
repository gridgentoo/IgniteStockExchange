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

import jdk.nashorn.api.scripting.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.json.*;

import java.util.*;

/**
 * Json cache object.
 */
public class ScriptingObjectConverter8 extends ScriptingObjectConverter implements JSObject {
    /** Fields. */
    private final JSONCacheObject fields;

    /**
     * @param o JSON object.
     */
    private ScriptingObjectConverter8(JSONCacheObject o) {
        fields = o;
    }

    /**
     * @param o Object.
     * @return Rest JSON cache object.
     */
    public static Object convertToRestObject(Object o) {
        if (o instanceof JSONCacheObject)
            return new ScriptingObjectConverter8((JSONCacheObject)o);

        return o;
    }

    /**
     * @return Fields.
     */
    public Map<Object, Object> getFields() {
        return fields;
    }

    /** {@inheritDoc} */
    @Override public Object toScriptingObject(Object o) {
        return convertToRestObject(o);
    }

    /**
     * @param key Field name.
     * @return Field value.
     */
    public Object getField(Object key) {
        return fields.get(key);
    }

    /**
     * @param o Object from script.
     * @return Object to store in cache.
     */
    public Object getField(String key, Object o) {
        if (o instanceof JSONCacheObject)
            return ((JSONCacheObject)o).getField(key);
        if (o instanceof ScriptingObjectConverter8)
            return ((ScriptingObjectConverter8)o).getField(key);

        return null;
    }

    /**
     * @param o Object from script.
     * @return Object to store in cache.
     */
    public Object getFields(Object o) {
        if (o instanceof ScriptingObjectConverter8)
            return ((ScriptingObjectConverter8)o).getFields();

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

        for (Object o : fields.keySet()) {
            if (!(o instanceof ScriptingObjectConverter8))
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
        return U.getSimpleName(ScriptingObjectConverter8.class);
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
