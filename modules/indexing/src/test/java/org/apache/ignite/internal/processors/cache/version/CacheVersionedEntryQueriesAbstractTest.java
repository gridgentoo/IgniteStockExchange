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

import java.util.List;
import javax.cache.Cache;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheEntry;
import org.apache.ignite.cache.CacheMemoryMode;
import org.apache.ignite.cache.eviction.lru.LruEvictionPolicy;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.configuration.CacheConfiguration;

/**
 * Versioned entry abstract test.
 */
public abstract class CacheVersionedEntryQueriesAbstractTest extends CacheVersionedEntryAbstractTest {
    /** {@inheritDoc} */
    @Override protected CacheConfiguration cacheConfiguration(String gridName) throws Exception {
        CacheConfiguration<Integer, String> cfg = super.cacheConfiguration(gridName);

        cfg.setIndexedTypes(Integer.class, String.class);

        return cfg;
    }

    /**
     * @throws Exception If failed.
     */
    public void testLocalSqlQuery() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        SqlQuery<Integer, String> query = new SqlQuery<Integer, String>(String.class, "_key != -1");

        query.setLocal(true);

        List<Cache.Entry<Integer, String>> res = cache.query(query).getAll();

        assert !res.isEmpty() : "Wrong entries size: " + res.size();

        for (Cache.Entry<Integer, String> entry : res)
            checkVersionedEntry(entry.unwrap(CacheEntry.class));
    }

    /**
     * @throws Exception If failed.
     */
    public void testLocalSqlQueryNoVersion() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        SqlQuery<Integer, String> query = new SqlQuery<Integer, String>(String.class, "_key != -1");

        List<Cache.Entry<Integer, String>> res = cache.query(query).getAll();

        assert !res.isEmpty() : "Wrong entries size: " + res.size();

        for (Cache.Entry<Integer, String> entry : res) {
            CacheEntry<Integer, String> verEntry = entry.unwrap(CacheEntry.class);

            assertNull(verEntry.version());
        }
    }

    /**
     * @throws Exception If failed.
     */
    public void testLocalTextQuery() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        TextQuery<Integer, String> query = new TextQuery<Integer, String>(String.class, "value*");

        query.setLocal(true);

        List<Cache.Entry<Integer, String>> res = cache.query(query).getAll();

        assert !res.isEmpty() : "Wrong entries size: " + res.size();

        for (Cache.Entry<Integer, String> entry : res)
            checkVersionedEntry(entry.unwrap(CacheEntry.class));
    }

    /**
     * @throws Exception If failed.
     */
    public void testLocalTextQueryNoVersion() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        TextQuery<Integer, String> query = new TextQuery<Integer, String>(String.class, "value*");

        List<Cache.Entry<Integer, String>> res = cache.query(query).getAll();

        assert !res.isEmpty() : "Wrong entries size: " + res.size();

        for (Cache.Entry<Integer, String> entry : res) {
            CacheEntry<Integer, String> verEntry = entry.unwrap(CacheEntry.class);

            assertNull(verEntry.version());
        }
    }
}
