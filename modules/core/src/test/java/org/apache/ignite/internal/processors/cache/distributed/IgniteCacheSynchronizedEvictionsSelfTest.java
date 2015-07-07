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

package org.apache.ignite.internal.processors.cache.distributed;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.cache.eviction.lru.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.util.typedef.*;
import org.apache.ignite.spi.discovery.tcp.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.*;
import org.apache.ignite.testframework.*;
import org.apache.ignite.testframework.junits.common.*;

/**
 *
 */
public class IgniteCacheSynchronizedEvictionsSelfTest extends GridCommonAbstractTest {
    /** */
    private static final int NODES_CNT = 4;

    /** */
    private static final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        startGrids(NODES_CNT);
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        if (getTestGridName(NODES_CNT - 1).equals(gridName))
            cfg.setClientMode(true);

        TcpDiscoverySpi discoSpi = new TcpDiscoverySpi();

        discoSpi.setIpFinder(IP_FINDER);

        cfg.setDiscoverySpi(discoSpi);

        return cfg;
    }

    /**
     * @throws Exception If failed.
     */
    public void testEvictSynchronizedPartitionedManual() throws Exception {
        testEvictSynchronizedManual(CacheMode.PARTITIONED, true, false);
    }

    /**
     * @throws Exception If failed.
     */
    public void testEvictSynchronizedReplicatedManual() throws Exception {
        testEvictSynchronizedManual(CacheMode.REPLICATED, true, false);
    }

    /**
     * @throws Exception If failed.
     */
    public void testEvictSynchronizedPartitionedPolicy() throws Exception {
        testEvictSynchronizedManual(CacheMode.PARTITIONED, false, false);
    }

    /**
     * @throws Exception If failed.
     */
    public void testEvictSynchronizedReplicatedPolicy() throws Exception {
        testEvictSynchronizedManual(CacheMode.REPLICATED, false, false);
    }

    /**
     * @throws Exception If failed.
     */
    public void testEvictSynchronizedPartitionedManualTopChange() throws Exception {
        testEvictSynchronizedManual(CacheMode.PARTITIONED, true, true);
    }

    /**
     * @throws Exception If failed.
     */
    public void testEvictSynchronizedReplicatedManualTopChange() throws Exception {
        testEvictSynchronizedManual(CacheMode.REPLICATED, true, true);
    }

    /**
     * @throws Exception If failed.
     */
    public void testEvictSynchronizedPartitionedPolicyTopChange() throws Exception {
        testEvictSynchronizedManual(CacheMode.PARTITIONED, false, true);
    }

    /**
     * @throws Exception If failed.
     */
    public void testEvictSynchronizedReplicatedPolicyTopChange() throws Exception {
        testEvictSynchronizedManual(CacheMode.REPLICATED, false, true);
    }

    /**
     * @throws Exception If failed.
     */
    private void testEvictSynchronizedManual(CacheMode cacheMode, boolean manual, final boolean topChange) throws Exception {
        for (int i = 0; i < NODES_CNT; i++)
            info("Node [i=" + i + ", id=" + grid(i).localNode().id() + ']');

        CacheConfiguration<Object, Object> ccfg = new CacheConfiguration<>();

        ccfg.setEvictionPolicy(new LruEvictionPolicy(10));
        ccfg.setCacheMode(cacheMode);
        ccfg.setBackups(1);
        ccfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        ccfg.setRebalanceMode(CacheRebalanceMode.SYNC);
        ccfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);

        ccfg.setEvictSynchronized(true);
        ccfg.setEvictSynchronizedKeyBufferSize(1);

        IgniteCache<Object, Object> cache = grid(0).createCache(ccfg);

        try {
            for (int i = 0; i < 10; i++)
                cache.put(i, i);

            final IgniteCache<Object, Object> nearCache = grid(3).createNearCache(null, new NearCacheConfiguration<>());

            // Initialize readers.
            for (int i = 0; i < 10; i++)
                assertEquals(i, nearCache.get(i));

            // Check readers updated.
            for (int i = 0; i < 10; i++)
                cache.put(i, i + 1);

            for (int i = 0; i < 10; i++)
                assertEquals(i + 1, nearCache.localPeek(i));

            if (topChange) {
                IgniteEx started = startGrid(NODES_CNT);

                info("Started grid: " + started.localNode().id());
            }

            // Force server eviction.
            if (manual) {
                for (int i = 0; i < 10; i++) {
                    ClusterNode primary = grid(0).affinity(null).mapKeyToNode(i);

                    grid(primary).cache(null).localEvict(F.asList(i));
                }
            }
            else {
                for (int i = 100; i < 200; i++)
                    cache.put(i, i);
            }

            GridTestUtils.waitForCondition(new PA() {
                /** {@inheritDoc} */
                @Override public boolean apply() {
                    for (int i = 0; i < 10; i++) {
                        if (nearCache.localPeek(i) != null) {
                            info("Entry is still present on near cache: " + i);

                            return false;
                        }
                    }

                    for (int g = 0; g < (topChange ? NODES_CNT + 1 : NODES_CNT); g++) {
                        if (g == NODES_CNT - 1)
                            continue;

                        IgniteCache<Object, Object> cache0 = grid(g).cache(null);

                        for (int i = 0; i < 10; i++) {
                            if (cache0.localPeek(i) != null) {
                                info("Entry is still present on DHT node [key=" + i + ", node=" + g + ']');

                                return false;
                            }
                        }
                    }

                    return true;
                }
            }, getTestTimeout());
        }
        finally {
            grid(0).destroyCache(null);

            if (topChange)
                stopGrid(NODES_CNT);
        }
    }
}
