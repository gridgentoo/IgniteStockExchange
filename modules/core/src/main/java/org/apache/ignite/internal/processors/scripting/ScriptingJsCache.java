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

import org.apache.ignite.*;
import org.apache.ignite.internal.util.typedef.internal.*;

import java.util.*;

/**
 * Scripting cache.
 */
public class ScriptingJsCache {
    /** Ignite cache. */
    private IgniteCache<Object, Object> cache;

    /** Scripting processor. */
    private IgniteScriptingProcessor proc;

    /**
     * @param cache Ignite cache.
     * @param proc Ignite scripting processor.
     */
    public ScriptingJsCache(IgniteCache cache, IgniteScriptingProcessor proc) {
        this.cache = cache;
        this.proc = proc;
    }

    /**
     * @param key Key.
     * @param val Value.
     */
    public void put(Object key, Object val) {
        Object cacheKey = proc.toJavaObject(key);
        Object cacheVal = proc.toJavaObject(val);

        cache.put(cacheKey, cacheVal);
    }

    /**
     * @param key Key.
     */
    public Object get(Object key) {
        Object cacheKey = proc.toJavaObject(key);

        return cache.get(cacheKey);
    }

    /**
     * @param key Key
     * @return True if cache contains key.
     */
    public boolean containsKey(Object key) {
        Object cacheKey = proc.toJavaObject(key);

        return cache.containsKey(cacheKey);
    }

    /**
     * @param keys Keys
     * @return True if cache contains key.
     */
    public boolean containsKeys(List keys) {
        List<Object> cacheKeys = (List<Object>)proc.toJavaObject(keys);

        return cache.containsKeys(new HashSet<>(cacheKeys));
    }

    /**
     * @param keys Keys.
     * @return Cache entries.
     */
    public List<Object> getAll(List keys) {
        List cacheKeys = (List)proc.toJavaObject(keys);

        Map<Object, Object> entries = cache.getAll(new HashSet<>(cacheKeys));

        List<Object> res = new ArrayList<>();

        for (Map.Entry<Object, Object> e : entries.entrySet())
            res.add(proc.createScriptingEntry(e.getKey(), e.getValue()));

        return res;
    }

    /**
     * @param keys Keys.
     */
    public void removeAll(List keys) {
        List cacheKeys = (List)proc.toJavaObject(keys);

        cache.removeAll(new HashSet<>(cacheKeys));
    }

    /**
     * @param entries Entries.
     */
    public void putAll(List entries) {
        List cacheKeys = (List)proc.toJavaObject(entries);

        Map<Object, Object> cacheEntries = U.newHashMap(entries.size());

        for (Object e : cacheKeys)
            cacheEntries.put(proc.getField("key", e), proc.getField("value", e));

        cache.putAll(cacheEntries);
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return Previous value.
     */
    public Object getAndPut(Object key, Object val) {
        Object cacheKey = proc.toJavaObject(key);
        Object cacheVal = proc.toJavaObject(val);

        return cache.getAndPut(cacheKey, cacheVal);
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return Previous value.
     */
    public Object getAndReplace(Object key, Object val) {
        Object cacheKey = proc.toJavaObject(key);
        Object cacheVal = proc.toJavaObject(val);

        Object o = cache.getAndReplace(cacheKey, cacheVal);

        return o;
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return Previous value.
     */
    public Object getAndPutIfAbsent(Object key, Object val) {
        Object cacheKey = proc.toJavaObject(key);
        Object cacheVal = proc.toJavaObject(val);

        return cache.getAndPutIfAbsent(cacheKey, cacheVal);
    }

    /**
     * @param key Key.
     * @return Previous value.
     */
    public Object getAndRemove(Object key) {
        Object cacheKey = proc.toJavaObject(key);

        return cache.getAndRemove(cacheKey);
    }

    /**
     * @param key Key.
     * @return If operation success.
     */
    public boolean remove(Object key) {
        Object cacheKey = proc.toJavaObject(key);

        return cache.remove(cacheKey);
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return If operation success.
     */
    public boolean removeValue(Object key, Object val) {
        Object cacheKey = proc.toJavaObject(key);
        Object cacheVal = proc.toJavaObject(val);

        return cache.remove(cacheKey, cacheVal);
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return If operation success.
     */
    public boolean replace(Object key, Object val) {
        Object cacheKey = proc.toJavaObject(key);
        Object cacheVal = proc.toJavaObject(val);

        return cache.replace(cacheKey, cacheVal);
    }

    /**
     * @param key Key.
     * @param val Value.
     * @param oldVal Old value.
     * @return If operation success.
     */
    public boolean replaceValue(Object key, Object val, Object oldVal) {
        Object cacheKey = proc.toJavaObject(key);
        Object cacheVal = proc.toJavaObject(val);
        Object oldCacheVal = proc.toJavaObject(oldVal);

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
        Object cacheKey = proc.toJavaObject(key);
        Object cacheVal = proc.toJavaObject(val);

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
