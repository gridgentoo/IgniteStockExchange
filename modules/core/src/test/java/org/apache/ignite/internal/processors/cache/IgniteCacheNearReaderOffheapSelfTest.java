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
import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.util.typedef.internal.*;

import java.util.concurrent.*;

/**
 *
 */
public class IgniteCacheNearReaderOffheapSelfTest extends GridCacheAbstractSelfTest {
    /** {@inheritDoc} */
    @Override protected int gridCount() {
        return 4;
    }

    /** {@inheritDoc} */
    @Override protected CacheConfiguration cacheConfiguration(String gridName) throws Exception {
        CacheConfiguration cfg = super.cacheConfiguration(gridName);

        cfg.setMemoryMode(CacheMemoryMode.OFFHEAP_TIERED);
        cfg.setBackups(1);
        cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        if (getTestGridName(2).equals(gridName) || getTestGridName(3).equals(gridName))
            cfg.setClientMode(true);

        cfg.setConnectorConfiguration(new ConnectorConfiguration());

        return cfg;
    }

    /**
     * @throws Exception If failed.
     */
    public void testReader() throws Exception {
        ignite(3).getOrCreateNearCache(null, new NearCacheConfiguration<>());

        awaitPartitionMapExchange();

        String prev = null;

        info("ITERATION: " + 0);

        int idx = 1;

        assertEquals(prev, ignite(2).cache(null).get("key"));

        info("DONE READING FROM CLIENT");

        ignite(idx).cache(null).put("key", "value" + 0);

        prev = "value" + 0;

        assertEquals(prev, ignite(3).cache(null).get("key"));

        info("Entry: " + ((IgniteKernal)ignite(0)).internalCache(null).peekEx("key"));

        ignite(idx).cache(null).put("key", "value" + 1);

        assertEquals("value1", ignite(3).cache(null).get("key"));
    }

    @Override
    protected long getTestTimeout() {
        return Long.MAX_VALUE;
    }
}
