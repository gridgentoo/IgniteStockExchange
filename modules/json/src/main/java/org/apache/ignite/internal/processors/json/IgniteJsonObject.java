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

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.jetbrains.annotations.NotNull;

import static org.apache.ignite.internal.processors.json.IgniteJsonUtils.toJsonValue;

/**
 * IgniteJsonObject implementation.
 */
public class IgniteJsonObject implements javax.json.JsonObject, Serializable {
    /** Bin object. */
    private final BinaryObject binObj;

    /** Size. */
    private int size;

    /**
     * @param binObj Binary object.
     */
    public IgniteJsonObject(BinaryObject binObj) {
        this(binObj, -1);
    }

    /**
     * @param binObj Binary object.
     * @param size Size.
     */
    public IgniteJsonObject(BinaryObject binObj, int size) {
        this.binObj = binObj;
        this.size = size;
    }

    /** {@inheritDoc} */
    @Override public JsonArray getJsonArray(String name) {
        return (JsonArray)get(name);
    }

    /** {@inheritDoc} */
    @Override public javax.json.JsonObject getJsonObject(String name) {
        return (JsonObject)get(name);
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

        if (val == null)
            throw new NullPointerException();

        if (val == JsonValue.TRUE)
            return true;

        if (val == JsonValue.FALSE)
            return false;

        throw new ClassCastException();
    }

    /** {@inheritDoc} */
    @Override public boolean getBoolean(String name, boolean dfltVal) {
        JsonValue val = get(name);

        if (val == JsonValue.TRUE)
            return true;

        if (val == JsonValue.FALSE)
            return false;

        return dfltVal;
    }

    /** {@inheritDoc} */
    @Override public boolean isNull(String name) {
        JsonValue val = get(name);

        if (val == null)
            throw new NullPointerException();

        return val == JsonValue.NULL;
    }

    /** {@inheritDoc} */
    @Override public ValueType getValueType() {
        return ValueType.OBJECT;
    }

    /** {@inheritDoc} */
    @Override public int size() {
        if (size == -1) {
            for (String field : binObj.type().fieldNames()) {
                if (binObj.hasField(field))
                    size++;
            }

            size++;
        }

        return size;
    }

    /** {@inheritDoc} */
    @Override public boolean isEmpty() {
        return size() == 0;
    }

    /** {@inheritDoc} */
    @Override public boolean containsKey(Object key) {
        A.notNull(key, "key");

        return binObj.hasField((String)key);
    }

    /** {@inheritDoc} */
    @Override public boolean containsValue(Object val) {
        A.notNull(val, "val");

        JsonValue val0 = (JsonValue)val;

        for (String key : binObj.type().fieldNames()) {
            Object field = binObj.field(key);

            if (field == null && val0 == JsonValue.NULL && binObj.hasField(key) ||
                field != null && toJsonValue(field).equals(val))
                return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override public JsonValue get(Object key) {
        A.notNull(key, "key");

        Object val = binObj.field((String)key);

        if (val == null)
            return binObj.hasField((String)key) ? JsonValue.NULL : null;

        return toJsonValue(val);
    }

    /** {@inheritDoc} */
    @Override public JsonValue put(String key, JsonValue val) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public JsonValue remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public void putAll(Map<? extends String, ? extends JsonValue> m) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public void clear() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    //TODO: Preserve iteration order. See JsonObject#keySet javadoc.
    @NotNull @Override public Set<String> keySet() {
        if (isEmpty())
            return Collections.emptySet();

        Set<String> keys = new HashSet<>();

        for (String name : binObj.type().fieldNames()) {
            if (binObj.hasField(name))
                keys.add(name);
        }

        return Collections.unmodifiableSet(keys);
    }

    /** {@inheritDoc} */
    //TODO: Preserve iteration order. See JsonObject#values javadoc.
    @NotNull @Override public Collection<JsonValue> values() {
        if (isEmpty())
            return Collections.emptyList();

        List<JsonValue> vals = new ArrayList<>();

        for (String name : binObj.type().fieldNames()) {
            if (binObj.hasField(name))
                vals.add(toJsonValue(binObj.field(name)));
        }

        return Collections.unmodifiableList(vals);
    }

    /** {@inheritDoc} */
    //TODO: Preserve iteration order. See JsonObject#entrySet javadoc.
    @NotNull @Override public Set<Entry<String, JsonValue>> entrySet() {
        if (isEmpty())
            return Collections.emptySet();

        Set<Entry<String, JsonValue>> entries = new HashSet<>();

        for (String name : binObj.type().fieldNames()) {
            if (binObj.hasField(name))
                entries.add(new AbstractMap.SimpleImmutableEntry<>(name, toJsonValue(binObj.field(name))));
        }

        return Collections.unmodifiableSet(entries);
    }

    /**
     * Returns backing {@link BinaryObject} instance.
     *
     * @return Backing {@link BinaryObject} instance.
     */
    BinaryObject binaryObject() {
        return binObj;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (o == null || !(o instanceof IgniteJsonObject))
            return false;

        return binObj.equals(((IgniteJsonObject)o).binObj);
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        return binObj.hashCode();
    }
}
