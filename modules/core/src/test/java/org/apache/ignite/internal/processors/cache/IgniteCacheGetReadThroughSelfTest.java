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
package org.apache.ignite.internal.processors.cache;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.cache.store.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.transactions.*;

import javax.cache.*;
import javax.cache.integration.*;

/**
 *
 */
public class IgniteCacheGetReadThroughSelfTest extends GridCacheAbstractSelfTest {
    /** {@inheritDoc} */
    @Override protected int gridCount() {
        return 3;
    }

    public void testGet() throws Exception {
        IgniteCache<Object, Object> cache = grid(0).cache(null);

        assertEquals("1", cache.get("NO_TX"));

        for (TransactionConcurrency conc : TransactionConcurrency.values()) {
            for (TransactionIsolation iso : TransactionIsolation.values()) {
                try (Transaction ignored = grid(0).transactions().txStart(conc, iso)) {
                    String key = iso.toString() + "_" + conc.toString();

                    assertEquals("Failed for: " + key, "1", cache.get(key));
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        cfg.getTransactionConfiguration().setTxSerializableEnabled(true);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected CacheConfiguration cacheConfiguration(String gridName) throws Exception {
        CacheConfiguration ccfg = new CacheConfiguration();

        ccfg.setCacheStoreFactory(singletonFactory(new LoadingStore()));

        ccfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

        ccfg.setReadThrough(true);

        ccfg.setLoadPreviousValue(true);

        return ccfg;
    }

    /** {@inheritDoc} */
    private static class LoadingStore extends CacheStoreAdapter {
        /** {@inheritDoc} */
        @Override public Object load(Object key) throws CacheLoaderException {
            return "1";
        }

        /** {@inheritDoc} */
        @Override public void delete(Object key) throws CacheWriterException {

        }

        /** {@inheritDoc} */
        @Override public void write(Cache.Entry entry) throws CacheWriterException {

        }
    }
}
