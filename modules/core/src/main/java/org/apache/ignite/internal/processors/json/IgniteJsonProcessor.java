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

import org.apache.ignite.internal.processors.*;
import org.apache.ignite.internal.processors.cache.*;
import org.jetbrains.annotations.*;

/**
 *
 */
public interface IgniteJsonProcessor extends GridProcessor {
    /**
     * @param ctx Cache context.
     * @param obj Key value.
     * @param userObj If {@code true} then given object is object provided by user and should be copied
     *        before stored in cache.
     * @return Cache key object.
     */
    public KeyCacheObject toCacheKeyObject(CacheObjectContext ctx, Object obj, boolean userObj);

    /**
     * @param ctx Cache context.
     * @param obj Object.
     * @param userObj If {@code true} then given object is object provided by user and should be copied
     *        before stored in cache.
     * @return Cache object.
     */
    @Nullable public CacheObject toCacheObject(CacheObjectContext ctx, @Nullable Object obj, boolean userObj);

    /**
     * @param cls Class.
     * @return {@code True} if given type is json object type.
     */
    public boolean jsonType(Class<?> cls);

    /**
     * @param obj Object.
     * @return {@code True} if given object is JSON object.
     */
    public boolean jsonObject(Object obj);

    /**
     * @param obj Object.
     * @param fieldName Field name.
     * @return {@code True} if field is set.
     */
    public boolean hasField(Object obj, String fieldName);

    /**
     * @param obj Object.
     * @param fieldName Field name.
     * @return Field value.
     */
    public Object field(Object obj, String fieldName);
}
