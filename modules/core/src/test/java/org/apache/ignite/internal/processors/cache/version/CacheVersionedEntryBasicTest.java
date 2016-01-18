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

package org.apache.ignite.internal.processors.cache.version;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.cache.Cache;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheEntry;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.eviction.lru.LruEvictionPolicy;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.processors.cache.GridCacheAbstractSelfTest;

/**
 * Versioned entry abstract test.
 */
public abstract class CacheVersionedEntryBasicTest extends CacheVersionedEntryAbstractTest {
    /**
     * @throws Exception If failed.
     */
    public void testInvoke() throws Exception {
        Cache<Integer, String> cache = grid(0).cache(null);

        final AtomicInteger invoked = new AtomicInteger();

        cache.invoke(100, new EntryProcessor<Integer, String, Object>() {
            @Override public Object process(MutableEntry<Integer, String> entry, Object... arguments)
                throws EntryProcessorException {

                invoked.incrementAndGet();

                CacheEntry<Integer, String> verEntry = entry.unwrap(CacheEntry.class);

                checkVersionedEntry(verEntry);

                return entry;
            }
        });

        assert invoked.get() > 0;
    }

    /**
     * @throws Exception If failed.
     */
    public void testInvokeAll() throws Exception {
        Cache<Integer, String> cache = grid(0).cache(null);

        Set<Integer> keys = new HashSet<>();

        for (int i = 0; i < ENTRIES_NUM; i++)
            keys.add(i);

        final AtomicInteger invoked = new AtomicInteger();

        cache.invokeAll(keys, new EntryProcessor<Integer, String, Object>() {
            @Override public Object process(MutableEntry<Integer, String> entry, Object... arguments)
                throws EntryProcessorException {

                invoked.incrementAndGet();

                CacheEntry<Integer, String> verEntry = entry.unwrap(CacheEntry.class);

                checkVersionedEntry(verEntry);

                return null;
            }
        });

        assert invoked.get() > 0;
    }

    /**
     * @throws Exception If failed.
     */
    public void testRandomEntry() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        for (int i = 0; i < 5; i++)
            checkVersionedEntry(cache.randomEntry().unwrap(CacheEntry.class));
    }

    /**
     * @throws Exception If failed.
     */
    public void testLocalPeek() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        Iterable<Cache.Entry<Integer, String>> entries = offheapTiered(cache) ?
            cache.localEntries(CachePeekMode.SWAP, CachePeekMode.OFFHEAP) :
            cache.localEntries(CachePeekMode.ONHEAP);

        for (Cache.Entry<Integer, String> entry : entries)
            checkVersionedEntry(entry.unwrap(CacheEntry.class));
    }

    /**
     * @throws Exception If failed.
     */
    public void testVersionComparision() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        CacheEntry<Integer, String> ver1 = cache.invoke(100,
            new EntryProcessor<Integer, String, CacheEntry<Integer, String>>() {
                @Override public CacheEntry<Integer, String> process(MutableEntry<Integer, String> entry,
                    Object... arguments) throws EntryProcessorException {
                        return entry.unwrap(CacheEntry.class);
                    }
            });

        cache.put(100, "new value 100");

        CacheEntry<Integer, String> ver2 = cache.invoke(100,
            new EntryProcessor<Integer, String, CacheEntry<Integer, String>>() {
                @Override public CacheEntry<Integer, String> process(MutableEntry<Integer, String> entry,
                    Object... arguments) throws EntryProcessorException {
                    return entry.unwrap(CacheEntry.class);
                }
            });

        assert ver1.version().compareTo(ver2.version()) < 0;
        assert ver1.updateTime() <= ver2.updateTime();
    }

    /**
     * @throws Exception If failed.
     */
    public void testLocalScanQuery() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        ScanQuery<Integer, String> query = new ScanQuery<>();

        query.setLocal(true);

        List<Cache.Entry<Integer, String>> res = cache.query(query).getAll();

        for (Cache.Entry<Integer, String> entry : res)
            checkVersionedEntry(entry.unwrap(CacheEntry.class));
    }

    /**
     * @throws Exception If failed.
     */
    public void testLocalScanQueryNoVersion() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        ScanQuery<Integer, String> query = new ScanQuery<>();

        List<Cache.Entry<Integer, String>> res = cache.query(query).getAll();

        assert !res.isEmpty() : "Wrong entries size: " + res.size();

        for (Cache.Entry<Integer, String> entry : res) {
            CacheEntry<Integer, String> verEntry = entry.unwrap(CacheEntry.class);

            assertNull(verEntry.version());
        }
    }
}
