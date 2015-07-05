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

import org.apache.ignite.*;
import org.apache.ignite.internal.processors.scripting.*;
import org.apache.ignite.internal.util.typedef.internal.*;

import java.util.*;

/**
 * Node js cache.
 */
public class NodeJsCache {
    /** Ignite cache. */
    private IgniteCache<Object, Object> cache;

    /**
     * @param cache Ignite cache.
     */
    public NodeJsCache(IgniteCache cache) {
        this.cache = cache;
    }

    /**
     * @param key Key.
     * @param val Value.
     */
    public void put(Object key, Object val) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);
        Object cacheVal = JSONCacheObject.toSimpleObject(val);

        cache.put(cacheKey, cacheVal);
    }

    /**
     * @param key Key.
     */
    public Object get(Object key) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);

        return RestJSONCacheObject.convertToRestObject(cache.get(cacheKey));
    }

    /**
     * @param key Key
     * @return True if cache contains key.
     */
    public boolean containsKey(Object key) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);

        return cache.containsKey(cacheKey);
    }

    /**
     * @param keys Keys
     * @return True if cache contains key.
     */
    public boolean containsKeys(List keys) {
        List<Object> cacheKeys = (List<Object>)JSONCacheObject.toSimpleObject(keys);

        return cache.containsKeys(new HashSet<>(cacheKeys));
    }

    /**
     * @param keys Keys.
     * @return Cache entries.
     */
    public List<RestEntry> getAll(List keys) {
        List cacheKeys = (List)JSONCacheObject.toSimpleObject(keys);

        Map<Object, Object> entries = cache.getAll(new HashSet<>(cacheKeys));

        List<RestEntry> res = new ArrayList<>();

        for (Map.Entry<Object, Object> e : entries.entrySet())
            res.add(new RestEntry(
                RestJSONCacheObject.convertToRestObject(e.getKey()),
                RestJSONCacheObject.convertToRestObject(e.getValue())));

        return res;
    }

    /**
     * @param entries Entries.
     */
    public void putAll(List entries) {
        List cacheKeys = (List)JSONCacheObject.toSimpleObject(entries);

        Map<Object, Object> cacheEntries = U.newHashMap(entries.size());

        for (Object e : cacheKeys) {
            JSONCacheObject e0 = (JSONCacheObject)e;
            cacheEntries.put(e0.getField("_key"), e0.getField("_val"));
        }

        cache.putAll(cacheEntries);
    }

    /**
     * @return Local size.
     */
    public int localSize() {
        return cache.localSize();
    }
}
