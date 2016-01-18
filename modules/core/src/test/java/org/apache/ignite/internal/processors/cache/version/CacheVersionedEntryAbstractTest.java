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
import org.apache.ignite.cache.CacheMemoryMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.eviction.lru.LruEvictionPolicy;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.NearCacheConfiguration;
import org.apache.ignite.internal.processors.cache.GridCacheAbstractSelfTest;

/**
 * Versioned entry abstract test.
 */
public abstract class CacheVersionedEntryAbstractTest extends GridCacheAbstractSelfTest {
    /** Entries number to store in a cache. */
    protected static final int ENTRIES_NUM = 500;

    /** {@inheritDoc} */
    @Override protected int gridCount() {
        return 2;
    }

    /** {@inheritDoc} */
    @Override protected NearCacheConfiguration nearConfiguration() {
        return null;
    }

    /**
     * Memory mode.
     *
     * @return Cache memory mode.
     */
    protected CacheMemoryMode memoryMode() {
        return CacheMemoryMode.ONHEAP_TIERED;
    }

    /** {@inheritDoc} */
    @Override protected CacheConfiguration cacheConfiguration(String gridName) throws Exception {
        CacheConfiguration<Integer, String> cfg = super.cacheConfiguration(gridName);

        cfg.setMemoryMode(memoryMode());

        if (swapEnabled()) {
            if (cfg.getMemoryMode() == CacheMemoryMode.ONHEAP_TIERED)
                cfg.setOffHeapMaxMemory(-1);

            cfg.setEvictionPolicy(new LruEvictionPolicy(ENTRIES_NUM / 2));
            cfg.setStatisticsEnabled(true);
        }

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        super.beforeTest();

        Cache<Integer, String> cache = grid(0).cache(null);

        for (int i = 0 ; i < ENTRIES_NUM; i++)
            cache.put(i, "value_" + i);
    }

    /**
     * @param entry Versioned entry.
     */
    protected void checkVersionedEntry(CacheEntry<Integer, String> entry) {
        assertNotNull(entry);

        assertNotNull(entry.version());
        assert entry.updateTime() > 0;

        assertNotNull(entry.getKey());
        assertNotNull(entry.getValue());
    }
}
