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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.jetbrains.annotations.NotNull;

import static org.apache.ignite.internal.processors.json.IgniteJsonUtils.toJsonValue;

/**
 * Implementation of JsonArray
 */
public class IgniteJsonArray implements JsonArray, Serializable {
    /** Empty array. */
    private static final Object[] EMPTY_ARR = new Object[0];

    /** Values for getValueAs. */
    private List<Object> list;

    /**
     * @param val List json values.
     */
    public IgniteJsonArray(List<Object> val) {
        this.list = Collections.unmodifiableList(val);
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
        return toJsonValueList(list);
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

        if (val == JsonValue.TRUE)
            return true;

        if (val == JsonValue.FALSE)
            return false;

        throw new ClassCastException();
    }

    /** {@inheritDoc} */
    @Override public boolean getBoolean(int idx, boolean dfltVal) {
        if (idx >= list.size())
            return dfltVal;

        JsonValue val = get(idx);

        if (val == JsonValue.TRUE)
            return true;

        if (val == JsonValue.FALSE)
            return false;

        return dfltVal;
    }

    /** {@inheritDoc} */
    @Override public boolean isNull(int idx) {
        return get(idx) == JsonValue.NULL;
    }

    /** {@inheritDoc} */
    @Override public ValueType getValueType() {
        return ValueType.ARRAY;
    }

    /** {@inheritDoc} */
    @Override public int size() {
        return list.size();
    }

    /** {@inheritDoc} */
    @Override public boolean isEmpty() {
        return list.isEmpty();
    }

    /** {@inheritDoc} */
    @Override public boolean contains(Object obj) {
        return indexOf(obj) > -1;
    }

    /**
     * @param val {@code JsonNumber} instance.
     */
    private int indexOf(JsonNumber val, boolean last) {
        for (int i = last ? list.size() - 1 : 0;
            (last && i > 0) || i < list.size(); i += last ? -1 : 1) {
            Object obj = list.get(i);

            if (obj instanceof Number &&
                (obj instanceof Integer && obj.equals(val.intValue()) ||
                    obj instanceof Long && obj.equals(val.longValue()) ||
                    obj instanceof Double && obj.equals(val.doubleValue()) ||
                    obj instanceof BigInteger && obj.equals(val.bigIntegerValue()) ||
                    obj instanceof BigDecimal && obj.equals(val.bigDecimalValue())))
                return i;
        }

        return -1;
    }

    /** {@inheritDoc} */
    @NotNull @Override public Iterator<JsonValue> iterator() {
        return new ImmutableIterator();
    }

    /** {@inheritDoc} */
    @NotNull @Override public Object[] toArray() {
        return toArray(EMPTY_ARR);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @NotNull @Override public <T> T[] toArray(T[] arr) {
        arr = (T[])new Object[list.size()];

        for (int i = 0; i < list.size(); i++)
            arr[i] = (T)toJsonValue(list.get(i));

        return arr;
    }

    /** {@inheritDoc} */
    @Override public boolean add(JsonValue val) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public boolean containsAll(Collection<?> col) {
        for (Object e : col) {
            if (!contains(e))
                return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override public boolean addAll(Collection<? extends JsonValue> c) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public boolean addAll(int idx, Collection<? extends JsonValue> c) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public void clear() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public JsonValue get(int idx) {
        Object val = list.get(idx);

        return val == null ? JsonValue.NULL : toJsonValue(val);
    }

    /** {@inheritDoc} */
    @Override public JsonValue set(int idx, JsonValue element) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public void add(int idx, JsonValue element) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public JsonValue remove(int idx) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override public int indexOf(Object obj) {
        A.notNull(obj, "obj");

        JsonValue val = (JsonValue)obj;

        JsonValue.ValueType valType = val.getValueType();

        switch (valType) {
            case ARRAY:
                return list.indexOf(((IgniteJsonArray)val).list());

            case OBJECT:
                return list.indexOf(((IgniteJsonObject)val).binaryObject());

            case STRING:
                return list.indexOf(((JsonString)val).getString());

            case NUMBER:
                return indexOf((JsonNumber)val, false);

            case TRUE:
                return list.indexOf(Boolean.TRUE);

            case FALSE:
                return list.indexOf(Boolean.FALSE);

            case NULL:
                return list.indexOf(null);

            default:
                throw new IllegalArgumentException("Unknown value type " + valType);
        }
    }

    /** {@inheritDoc} */
    @Override public int lastIndexOf(Object obj) {
        A.notNull(obj, "obj");

        JsonValue val = (JsonValue)obj;

        JsonValue.ValueType valType = val.getValueType();

        switch (valType) {
            case ARRAY:
                return list.lastIndexOf(((IgniteJsonArray)val).list());

            case OBJECT:
                return list.lastIndexOf(((IgniteJsonObject)val).binaryObject());

            case STRING:
                return list.lastIndexOf(((JsonString)val).getString());

            case NUMBER:
                return indexOf((JsonNumber)val, true);

            case TRUE:
                return list.lastIndexOf(Boolean.TRUE);

            case FALSE:
                return list.lastIndexOf(Boolean.FALSE);

            case NULL:
                return list.lastIndexOf(null);

            default:
                throw new IllegalArgumentException("Unknown value type " + valType);
        }
    }

    /** {@inheritDoc} */
    @NotNull @Override public ListIterator<JsonValue> listIterator() {
        return new ImmutableIterator();
    }

    /** {@inheritDoc} */
    @NotNull @Override public ListIterator<JsonValue> listIterator(int idx) {
        return new ImmutableIterator(idx);
    }

    /** {@inheritDoc} */
    @NotNull @Override public List<JsonValue> subList(int fromIdx, int toIdx) {
        return toJsonValueList(list.subList(fromIdx, toIdx));
    }

    /**
     * Returns backing list.
     *
     * @return Backing list.
     */
    List<Object> list() {
        return list;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        IgniteJsonArray arr = (IgniteJsonArray)o;

        return list.equals(arr.list);
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        return list.hashCode();
    }

    /**
     * @param l Object list.
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> toJsonValueList(List<Object> l) {
        if (l.isEmpty())
            return Collections.emptyList();

        List<T> res = new ArrayList<>();

        for (Object val : l)
            res.add((T)toJsonValue(val));

        return Collections.unmodifiableList(res);
    }

    /**
     * Immutable list iterator.
     */
    private class ImmutableIterator implements ListIterator<JsonValue> {
        /** Backing list iterator. */
        final ListIterator<Object> it;

        /**
         * Default constructor.
         */
        public ImmutableIterator() {
            this(0);
        }

        /**
         * @param idx Index.
         */
        public ImmutableIterator(int idx) {
            it = list.listIterator(idx);
        }

        /** {@inheritDoc} */
        @Override public boolean hasNext() {
            return it.hasNext();
        }

        /** {@inheritDoc} */
        @Override public JsonValue next() {
            return toJsonValue(it.next());
        }

        /** {@inheritDoc} */
        @Override public boolean hasPrevious() {
            return it.hasPrevious();
        }

        /** {@inheritDoc} */
        @Override public JsonValue previous() {
            return toJsonValue(it.previous());
        }

        /** {@inheritDoc} */
        @Override public int nextIndex() {
            return it.nextIndex();
        }

        /** {@inheritDoc} */
        @Override public int previousIndex() {
            return it.previousIndex();
        }

        /** {@inheritDoc} */
        @Override public void remove() {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        @Override public void set(JsonValue val) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        @Override public void add(JsonValue val) {
            throw new UnsupportedOperationException();
        }
    }
}
