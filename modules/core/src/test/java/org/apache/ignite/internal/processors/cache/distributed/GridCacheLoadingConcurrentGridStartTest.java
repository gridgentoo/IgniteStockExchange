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
import org.apache.ignite.cache.store.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.cache.*;
import org.apache.ignite.internal.processors.cache.distributed.dht.*;
import org.apache.ignite.internal.processors.datastreamer.*;
import org.apache.ignite.internal.util.typedef.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.lang.*;
import org.apache.ignite.spi.discovery.tcp.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.*;
import org.apache.ignite.testframework.*;
import org.apache.ignite.testframework.junits.common.*;

import org.jetbrains.annotations.*;

import javax.cache.*;
import javax.cache.configuration.*;
import javax.cache.integration.*;
import java.io.*;
import java.util.concurrent.*;

import static org.apache.ignite.cache.CacheAtomicityMode.*;
import static org.apache.ignite.cache.CacheMode.*;
import static org.apache.ignite.cache.CacheRebalanceMode.*;

/**
 * Tests for cache data loading during simultaneous grids start.
 */
public class GridCacheLoadingConcurrentGridStartTest extends GridCommonAbstractTest {
    /** Ip finder. */
    private static final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /** Grids count */
    private static int GRIDS_CNT = 10;

    /** Keys count */
    private static int KEYS_CNT = 100000;

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        TcpDiscoverySpi spi = new TcpDiscoverySpi();

        spi.setIpFinder(IP_FINDER);

        cfg.setDiscoverySpi(spi);

        CacheConfiguration ccfg = new CacheConfiguration();

        ccfg.setCacheMode(PARTITIONED);

        ccfg.setAtomicityMode(ATOMIC);

        ccfg.setRebalanceMode(SYNC);

        ccfg.setBackups(1);

        ccfg.setNearConfiguration(null);

        CacheStore<Integer, String> store = new TestCacheStoreAdapter();

        ccfg.setCacheStoreFactory(new FactoryBuilder.SingletonFactory(store));

        cfg.setCacheConfiguration(ccfg);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();
    }

    /**
     * @throws Exception if failed
     */
    public void testLoadCacheWithDataStreamer() throws Exception {
        IgniteInClosure<Ignite> f = new IgniteInClosure<Ignite>() {
            @Override public void apply(Ignite grid) {

                try (IgniteDataStreamer<Integer, String> dataStreamer = grid.dataStreamer(null)) {
                    dataStreamer.perNodeBufferSize(1024);

                    for (int i = 0; i < KEYS_CNT; i++) {
                        dataStreamer.addData(i, Integer.toString(i));

                        if (i % 100 == 0) {
                            try {
                                U.sleep(5);
                            }
                            catch (IgniteInterruptedCheckedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };

        loadCache(f);
    }

    /**
     * @throws Exception if failed
     */
    public void testLoadCacheFromStore() throws Exception {
        loadCache(new IgniteInClosure<Ignite>() {
            @Override public void apply(Ignite grid) {
                grid.cache(null).loadCache(null);
            }
        });
    }

    /**
     * Loads cache using closure and asserts cache size.
     *
     * @param f cache loading closure
     * @throws Exception if failed
     */
    private void loadCache(IgniteInClosure<Ignite> f) throws Exception {
        Ignite g0 = startGrid(0);

        IgniteInternalFuture fut = GridTestUtils.runAsync(new Callable<Ignite>() {
            @Override public Ignite call() throws Exception {
                for (int i = 1; i < GRIDS_CNT - 1; i++) {
                    startGrid(i);

                    U.sleep(50);
                }

                return startGridsMultiThreaded(GRIDS_CNT - 1, 1);
            }
        });

        try {
            f.apply(g0);
        }
        finally {
            fut.get();
        }

        IgniteCache<Integer, String> cache = g0.cache(null);

        int missingCnt = 0;

        for (int i = 0; i < KEYS_CNT; i++) {
            if (cache.get(i) == null) {
                missingCnt++;

                System.out.println("----------------------------------------------------------------------------");
                System.out.println("!!! Lost key: " + i);
                System.out.println("----------------------------------------------------------------------------");

                for (DataStreamerImpl.DebugInfo debugInfo : DataStreamerImpl.DEBUG_MAP.get(i)) {
                    if (debugInfo == null)
                        continue;

                    System.out.println(debugInfo);

                    GridCacheContext cctx = ((IgniteCacheProxy)G.ignite(debugInfo.nodeId).cache(null)).context();

                    GridDhtPartitionTopology top = cctx.topology();

                    GridDhtLocalPartition histPart =
                        top.localPartition(debugInfo.part, debugInfo.topVer, false);

                    GridDhtLocalPartition curPart =
                        top.localPartition(debugInfo.part, cctx.affinity().affinityTopologyVersion(), false);

                    int expPart = cctx.affinity().partition(i);

                    System.out.println("Checking: Part state was: " + (histPart == null ? null : histPart.state()) + ", " +
                        "Part state now: " + (curPart == null ? null : curPart.state()) + ", " +
                        "Current part: " + expPart);
                }
            }
        }

        System.out.println("!!! Lost keys total: " + missingCnt);

        assertCacheSize();
    }

    /** Asserts cache size. */
    private void assertCacheSize() {
        IgniteCache<Integer, String> cache = grid(0).cache(null);

        printStats(cache);

        assertEquals(KEYS_CNT, cache.size());

        int total = 0;

        for (int i = 0; i < GRIDS_CNT; i++)
            total += grid(i).cache(null).localSize();

        assertEquals(KEYS_CNT, total);
    }

    /**
     * @param cache Cache.
     */
    private void printStats(IgniteCache<Integer, String> cache) {
        System.out.println("!!! Cache size: " + cache.size());

        System.out.println();

        int total = 0;

        for (int i = 0; i < GRIDS_CNT; i++) {
            int locSize = grid(i).cache(null).localSize();

            System.out.println("!!! Local cache size(" + i + "): " + locSize);

            total += locSize;
        }

        System.out.println("!!! Total cache size: " + total);
    }

    /**
     * Cache store adapter.
     */
    private static class TestCacheStoreAdapter extends CacheStoreAdapter<Integer, String> implements Serializable {
        /** {@inheritDoc} */
        @Override public void loadCache(IgniteBiInClosure<Integer, String> f, Object... args) {
            for (int i = 0; i < KEYS_CNT; i++)
                f.apply(i, Integer.toString(i));
        }

        /** {@inheritDoc} */
        @Nullable @Override public String load(Integer i) throws CacheLoaderException {
            return null;
        }

        /** {@inheritDoc} */
        @Override public void write(Cache.Entry<? extends Integer, ? extends String> entry)
            throws CacheWriterException {
            // No-op.
        }

        /** {@inheritDoc} */
        @Override public void delete(Object o) throws CacheWriterException {
            // No-op.
        }
    }
}
