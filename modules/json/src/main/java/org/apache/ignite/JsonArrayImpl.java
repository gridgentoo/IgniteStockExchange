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

package org.apache.ignite;

import javax.json.*;
import java.util.*;

/**
 * Implementation of JsonArray
 */
public class JsonArrayImpl extends ArrayList<JsonValue> implements JsonArray {
    /** Values for getValueAs. */
    private List<JsonValue> val;

    /**
     * @param val List json values.
     */
    public JsonArrayImpl(List<JsonValue> val) {
        super(val);
    }

    /** {@inheritDoc} */
    @Override public JsonObject getJsonObject(int idx) {
        return (JsonObject)get(idx);
    }

    /** {@inheritDoc} */
    @Override public JsonArray getJsonArray(int idx) {
        return (JsonArray)get(idx);
    }

    /** {@inheritDoc} */
    @Override public JsonNumber getJsonNumber(int idx) {
        return (JsonNumber)get(idx);
    }

    /** {@inheritDoc} */
    @Override public JsonString getJsonString(int idx) {
        return (JsonString)get(idx);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
        if (val == null) {
            val = new ArrayList(this.size());

            for (int i = 0; i < size(); ++i)
                val.add(get(i));

            val = Collections.unmodifiableList(val);
        }
        return (List<T>) val;
    }

    /** {@inheritDoc} */
    @Override public String getString(int idx) {
        return getJsonString(idx).getString();
    }

    /** {@inheritDoc} */
    @Override public String getString(int idx, String dfltVal) {
        try {
            return getString(idx);
        }
        catch (Exception e) {
            return dfltVal;
        }
    }

    /** {@inheritDoc} */
    @Override public int getInt(int idx) {
        return getJsonNumber(idx).intValue();
    }

    /** {@inheritDoc} */
    @Override public int getInt(int idx, int dfltVal) {
        try {
            return getInt(idx);
        } catch (Exception e) {
            return dfltVal;
        }
    }

    /** {@inheritDoc} */
    @Override public boolean getBoolean(int idx) {
        JsonValue val = get(idx);

        if (val.equals(JsonValue.TRUE))
            return true;

        if (val.equals(JsonValue.FALSE))
            return false;

        throw new ClassCastException();
    }

    /** {@inheritDoc} */
    @Override public boolean getBoolean(int idx, boolean dfltVal) {
        try {
            return getBoolean(idx);
        } catch (Exception e) {
            return dfltVal;
        }
    }

    /** {@inheritDoc} */
    @Override public boolean isNull(int idx) {
        return get(idx).equals(JsonValue.NULL);
    }

    /** {@inheritDoc} */
    @Override public ValueType getValueType() {
        return ValueType.ARRAY;
    }
}
