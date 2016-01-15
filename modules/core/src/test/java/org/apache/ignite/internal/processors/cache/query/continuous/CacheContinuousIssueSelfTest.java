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

package org.apache.ignite.internal.processors.cache.query.continuous;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.PA;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;

import static javax.cache.configuration.FactoryBuilder.factoryOf;

/**
 * Test from https://issues.apache.org/jira/browse/IGNITE-2384.
 */
public class CacheContinuousIssueSelfTest extends GridCommonAbstractTest {
    /** */
    static public TcpDiscoveryIpFinder ipFinder = new TcpDiscoveryVmIpFinder(true);

    /** Cache name. */
    public static final String CACHE_NAME = "test_cache";

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        super.beforeTest();

        startGridsMultiThreaded(2);

        awaitPartitionMapExchange();
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    public void testEvent() throws Exception {
        IgniteCache<Integer, String> cache1 = grid(0).getOrCreateCache(CACHE_NAME);

        final AllEventListener<Integer, String> lsnr1 = registerCacheListener(cache1);

        IgniteCache<Integer, String> cache2 = grid(1).getOrCreateCache(CACHE_NAME);

        int key = affinityKeyForNode(1, grid(0));
        cache1.put(key, "vodka");

        // Note the issue is only reproducible if the second registration is done right
        // here, after the first put() above.
        final AllEventListener<Integer, String> lsnr2 = registerCacheListener(cache2);

        assert GridTestUtils.waitForCondition(new PA() {
            @Override public boolean apply() {
                return lsnr1.createdCount.get() == 1;
            }
        }, 2000L) : "Unexpected number of events: " + lsnr1.createdCount.get();

        // Sanity check.
        assert GridTestUtils.waitForCondition(new PA() {
            @Override public boolean apply() {
                return lsnr2.createdCount.get() == 0;
            }
        }, 2000L) : "Expected no create events, but got: " + lsnr2.createdCount.get();

        // node2 now becomes the primary for the key.
        grid(0).close();

        awaitPartitionMapExchange();

        cache2.put(key, "peevo");

        // Sanity check.
        assert GridTestUtils.waitForCondition(new PA() {
            @Override public boolean apply() {
                return lsnr1.createdCount.get() == 1;
            }
        }, 2000L) : "Expected no change here, but got: " + lsnr1.createdCount.get();

        // Sanity check.
        assert GridTestUtils.waitForCondition(new PA() {
            @Override public boolean apply() {
                return lsnr2.updatedCount.get() == 0;
            }
        }, 2000L) : "Expected no update events, but got: " + lsnr2.updatedCount.get();

        System.out.println(">>>>> " + lsnr2.createdCount.get());

        // This assertion fails: 0 events get delivered.
        assert GridTestUtils.waitForCondition(new PA() {
            @Override public boolean apply() {
                return lsnr2.createdCount.get() == 1;
            }
        }, 2000L) : "Expected a single event due to 'peevo', but got: " + lsnr2.createdCount.get();
    }

    /**
     * @param cache Cache.
     * @return Event listener.
     */
    private AllEventListener<Integer, String> registerCacheListener(
        IgniteCache<Integer, String> cache) {
        AllEventListener<Integer, String> lsnr = new AllEventListener<>();
        cache.registerCacheEntryListener(
            new MutableCacheEntryListenerConfiguration<>(factoryOf(lsnr), null, true, false));
        return lsnr;
    }

    /**
     * @param startValue Start value.
     * @param node Ignite node.
     * @return Primary key.
     */
    private int affinityKeyForNode(int startValue, Ignite node) {
        Affinity<Integer> affinity = node.affinity(CACHE_NAME);

        ClusterNode localNode = node.cluster().localNode();

        int key;

        for (key = startValue + 1; !affinity.isPrimary(localNode, key); key++);

        return key;
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration() throws Exception {
        IgniteConfiguration cfg = super.getConfiguration();

        TcpDiscoverySpi spi = new TcpDiscoverySpi();

        spi.setIpFinder(ipFinder);

        cfg.setDiscoverySpi(spi);
        cfg.setCacheConfiguration(cache());

        return cfg;
    }

    /**
     * @return Cache configuration.
     */
    protected CacheConfiguration<Integer, String> cache() {
        CacheConfiguration<Integer, String> cfg = new CacheConfiguration<>(CACHE_NAME);

        cfg.setCacheMode(CacheMode.PARTITIONED);
        cfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        cfg.setRebalanceMode(CacheRebalanceMode.SYNC);
        cfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC);
        cfg.setStartSize(1024);

        return cfg;
    }

    /**
     * Event listener.
     */
    public static class AllEventListener<K, V> implements CacheEntryCreatedListener<K, V>,
        CacheEntryUpdatedListener<K, V>, CacheEntryRemovedListener<K, V>, CacheEntryExpiredListener<K, V>,
        Serializable {
        /** */
        final AtomicInteger createdCount = new AtomicInteger();

        /** */
        final AtomicInteger updatedCount = new AtomicInteger();

        /** {@inheritDoc} */
        @Override public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> evts) {
            createdCount.incrementAndGet();
            System.out.printf("onCreate: %s. \n", evts);
        }

        /** {@inheritDoc} */
        @Override public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> evts) {
            System.out.printf("onExpired: %s. \n", evts);
        }

        /** {@inheritDoc} */
        @Override public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> evts) {
            System.out.printf("onRemoved: %s. \n", evts);
        }

        /** {@inheritDoc} */
        @Override public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> evts) {
            updatedCount.incrementAndGet();
            System.out.printf("onUpdated: %s.", evts);
        }
    }
}
