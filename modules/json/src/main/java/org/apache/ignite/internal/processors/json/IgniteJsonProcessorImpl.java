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

import org.apache.ignite.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.*;
import org.apache.ignite.internal.processors.cache.*;
import org.jetbrains.annotations.*;

import javax.json.*;

/**
 *
 */
public class IgniteJsonProcessorImpl extends GridProcessorAdapter implements IgniteJsonProcessor {
    /**
     * @param ctx Context.
     */
    public IgniteJsonProcessorImpl(GridKernalContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    @Override public KeyCacheObject toCacheKeyObject(CacheObjectContext ctx, Object obj, boolean userObj) {
        if (obj instanceof JsonObject)
            return new JsonCacheObject((JsonObject)obj);

        return null;
    }

    /** {@inheritDoc} */
    @Nullable @Override public CacheObject toCacheObject(CacheObjectContext ctx,
        @Nullable Object obj,
        boolean userObj) {
        if (obj instanceof JsonObject)
            return new JsonCacheObject((JsonObject)obj);

        return null;
    }

    /** {@inheritDoc} */
    @Override public boolean jsonType(Class<?> cls) {
        return cls.equals(JsonObject.class);
    }

    /** {@inheritDoc} */
    @Override public boolean jsonObject(Object obj) {
        return obj instanceof JsonCacheObject || obj instanceof JsonObject;
    }

    /** {@inheritDoc} */
    @Override public boolean hasField(Object obj, String fieldName) {
        if (obj instanceof JsonObject)
            return ((JsonObject)obj).containsKey(fieldName);

        return ((JsonCacheObject)obj).hasField(fieldName);
    }

    /** {@inheritDoc} */
    @Override public Object field(Object obj, String fieldName) {
        if (obj instanceof JsonObject)
            return value((JsonObject) obj, fieldName);

        return ((JsonCacheObject)obj).field(fieldName);
    }

    /**
     * @param obj Object.
     * @param fieldName Field name.
     * @return Field value.
     */
    static Object value(JsonObject obj, String fieldName) {
        JsonValue jsonVal = obj.get(fieldName);

        if (jsonVal == null)
            return null;

        switch (jsonVal.getValueType()) {
            case FALSE:
                return Boolean.FALSE;

            case TRUE:
                return Boolean.TRUE;

            case STRING:
                return ((JsonString)jsonVal).getString();

            case NUMBER:
                return ((JsonNumber)jsonVal).intValue();

            case OBJECT:
                return jsonVal;

            default:
                throw new IgniteException("Unsupported type [field=" + fieldName +
                    ", type=" + jsonVal.getValueType() + ']');
        }
    }
}
