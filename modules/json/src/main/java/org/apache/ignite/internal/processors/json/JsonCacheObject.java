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
import org.apache.ignite.internal.processors.cache.*;
import org.jetbrains.annotations.*;

import javax.json.*;

/**
 *
 */
public class JsonCacheObject extends CacheObjectAdapter implements KeyCacheObject {
    /**
     *
     */
    public JsonCacheObject() {
        // No-op.
    }

    /**
     * @param obj Object.
     */
    public JsonCacheObject(JsonObject obj) {
        this.val = obj;
    }

    /** {@inheritDoc} */
    @Override public byte type() {
        return 10;
    }

    /** {@inheritDoc} */
    @Nullable @Override public <T> T value(CacheObjectContext ctx, boolean cpy) {
        return (T)val;
    }

    /** {@inheritDoc} */
    @Override public byte[] valueBytes(CacheObjectContext ctx) throws IgniteCheckedException {
        return valBytes;
    }

    /** {@inheritDoc} */
    @Override public CacheObject prepareForCache(CacheObjectContext ctx) {
        return this;
    }

    /** {@inheritDoc} */
    @Override public void finishUnmarshal(CacheObjectContext ctx, ClassLoader ldr) throws IgniteCheckedException {
        assert val != null || valBytes != null;

        if (val == null)
            val = ctx.processor().unmarshal(ctx, valBytes, ldr);
    }

    /** {@inheritDoc} */
    @Override public void prepareMarshal(CacheObjectContext ctx) throws IgniteCheckedException {
        if (valBytes == null)
            valBytes = ctx.processor().marshal(ctx, val);
    }

    /** {@inheritDoc} */
    @Override public boolean internal() {
        return false;
    }

    /** {@inheritDoc} */
    @Override public byte directType() {
        return -23;
    }

    /**
     * @param fieldName Field name.
     * @return {@code True} if has field.
     */
    boolean hasField(String fieldName) {
        return ((IgniteJsonObject)val).containsKey(fieldName);
    }

    /**
     * @param fieldName Field name.
     * @return Field value.
     */
    Object field(String fieldName) {
        return IgniteJsonProcessorImpl.value((JsonObject)val, fieldName);
    }

    /** {@inheritDoc}*/
    @Override public int hashCode() {
        //TODO: do correct
        return val.hashCode();
    }

    /** {@inheritDoc}*/
    @Override public boolean equals(Object obj) {
        //TODO: do correct
        if (obj == null || !(obj instanceof JsonCacheObject))
            return false;

        return val.equals(((JsonCacheObject) obj).val);
    }
}
