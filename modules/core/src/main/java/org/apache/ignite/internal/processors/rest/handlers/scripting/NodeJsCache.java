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
     * @param keys Keys.
     */
    public void removeAll(List keys) {
        List cacheKeys = (List)JSONCacheObject.toSimpleObject(keys);

        cache.removeAll(new HashSet<>(cacheKeys));
    }

    /**
     * @param entries Entries.
     */
    public void putAll(List entries) {
        List cacheKeys = (List)JSONCacheObject.toSimpleObject(entries);

        Map<Object, Object> cacheEntries = U.newHashMap(entries.size());

        for (Object e : cacheKeys) {
            JSONCacheObject e0 = (JSONCacheObject)e;
            cacheEntries.put(e0.getField("key"), e0.getField("value"));
        }

        cache.putAll(cacheEntries);
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return Previous value.
     */
    public Object getAndPut(Object key, Object val) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);
        Object cacheVal = JSONCacheObject.toSimpleObject(val);

        return RestJSONCacheObject.convertToRestObject(cache.getAndPut(cacheKey, cacheVal));
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return Previous value.
     */
    public Object getAndReplace(Object key, Object val) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);
        Object cacheVal = JSONCacheObject.toSimpleObject(val);

        Object o = RestJSONCacheObject.convertToRestObject(cache.getAndReplace(cacheKey, cacheVal));

        return o;
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return Previous value.
     */
    public Object getAndPutIfAbsent(Object key, Object val) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);
        Object cacheVal = JSONCacheObject.toSimpleObject(val);

        return RestJSONCacheObject.convertToRestObject(cache.getAndPutIfAbsent(cacheKey, cacheVal));
    }

    /**
     * @param key Key.
     * @return Previous value.
     */
    public Object getAndRemove(Object key) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);

        return RestJSONCacheObject.convertToRestObject(cache.getAndRemove(cacheKey));
    }

    /**
     * @param key Key.
     * @return If operation success.
     */
    public boolean remove(Object key) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);

        return cache.remove(cacheKey);
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return If operation success.
     */
    public boolean removeValue(Object key, Object val) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);
        Object cacheVal = JSONCacheObject.toSimpleObject(val);

        return cache.remove(cacheKey, cacheVal);
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return If operation success.
     */
    public boolean replace(Object key, Object val) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);
        Object cacheVal = JSONCacheObject.toSimpleObject(val);

        return cache.replace(cacheKey, cacheVal);
    }

    /**
     * @param key Key.
     * @param val Value.
     * @param oldVal Old value.
     * @return If operation success.
     */
    public boolean replaceValue(Object key, Object val, Object oldVal) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);
        Object cacheVal = JSONCacheObject.toSimpleObject(val);
        Object oldCacheVal = JSONCacheObject.toSimpleObject(oldVal);

        return cache.replace(cacheKey, oldCacheVal, cacheVal);
    }

    /**
     * Removes all from cache.
     */
    public void removeAllFromCache() {
        cache.removeAll();
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return Previous value.
     */
    public Object putIfAbsent(Object key, Object val) {
        Object cacheKey = JSONCacheObject.toSimpleObject(key);
        Object cacheVal = JSONCacheObject.toSimpleObject(val);

        return cache.putIfAbsent(cacheKey, cacheVal);
    }

    /**
     * @return Cache name.
     */
    public String getName() {
        return cache.getName();
    }

    /**
     * @return Local size.
     */
    public int localSize() {
        return cache.localSize();
    }

    /**
     * @return Size.
     */
    public int size() {
        return cache.size();
    }
}
