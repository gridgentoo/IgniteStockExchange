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

package org.apache.ignite.internal.processors.json;

import javax.json.*;
import java.io.*;
import java.util.*;

/**
 * IgniteJsonObject implementation.
 */
public class IgniteJsonObject extends HashMap<String, JsonValue> implements javax.json.JsonObject, Serializable {
    /**
     * @param val Map to store.
     */
    public IgniteJsonObject(Map<String, JsonValue> val) {
        super(val);
    }

    /** {@inheritDoc} */
    @Override public JsonArray getJsonArray(String name) {
        return (JsonArray)get(name);
    }

    /** {@inheritDoc} */
    @Override public javax.json.JsonObject getJsonObject(String name) {
        return (javax.json.JsonObject)get(name);
    }

    /** {@inheritDoc} */
    @Override public JsonNumber getJsonNumber(String name) {
        return (JsonNumber)get(name);
    }

    /** {@inheritDoc} */
    @Override public JsonString getJsonString(String name) {
        return (JsonString)get(name);
    }

    /** {@inheritDoc} */
    @Override public String getString(String name) {
        return getJsonString(name).getString();
    }

    /** {@inheritDoc} */
    @Override public String getString(String name, String dfltVal) {
        try {
            return getString(name);
        }
        catch (Exception e) {
            return dfltVal;
        }
    }

    /** {@inheritDoc} */
    @Override public int getInt(String name) {
        return getJsonNumber(name).intValue();
    }

    /** {@inheritDoc} */
    @Override public int getInt(String name, int dfltVal) {
        try {
            return getInt(name);
        }
        catch (Exception e) {
            return dfltVal;
        }
    }

    /** {@inheritDoc} */
    @Override public boolean getBoolean(String name) {
        JsonValue val = get(name);

        if (val.equals(JsonValue.TRUE))
            return true;

        if (val.equals(JsonValue.FALSE))
            return false;

        throw new ClassCastException();
    }

    /** {@inheritDoc} */
    @Override public boolean getBoolean(String name, boolean dfltVal) {
        try {
            return getBoolean(name);
        }
        catch (Exception e) {
            return dfltVal;
        }
    }

    /** {@inheritDoc} */
    @Override public boolean isNull(String name) {
        return get(name).equals(JsonValue.NULL);
    }

    /** {@inheritDoc} */
    @Override public ValueType getValueType() {
        return ValueType.OBJECT;
    }

    /** {@inheritDoc}*/
    @Override public boolean equals(Object o) {
        if (o == null || !(o instanceof IgniteJsonObject))
            return false;

        return super.equals(o);
    }

    /** {@inheritDoc}*/
    @Override public int hashCode() {
        return super.hashCode();
    }
}
