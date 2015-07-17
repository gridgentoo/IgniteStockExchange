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

package org.apache.ignite.internal.processors.json;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.cache.query.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.json.*;
import org.apache.ignite.spi.discovery.tcp.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.*;
import org.apache.ignite.testframework.junits.common.*;

import javax.cache.*;
import javax.json.*;
import javax.json.spi.*;
import java.util.*;

/**
 *
 */
public class IgniteJsonCacheTest extends GridCommonAbstractTest {
    /** */
    protected static TcpDiscoveryIpFinder ipFinder = new TcpDiscoveryVmIpFinder(true);

    /** */
    private boolean client;

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        ((TcpDiscoverySpi)cfg.getDiscoverySpi()).setIpFinder(ipFinder);

        CacheConfiguration ccfg = new CacheConfiguration();

        CacheTypeMetadata meta = new CacheTypeMetadata();

        meta.setValueType(JsonObject.class);

        Map<String, Class<?>> ascFields = new HashMap<>();

        ascFields.put("name", String.class);
        ascFields.put("id", Integer.class);
        ascFields.put("address.street", String.class);

        meta.setAscendingFields(ascFields);

        Map<String, Class<?>> qryFields = new HashMap<>();

        qryFields.put("salary", Integer.class);
        qryFields.put("address", JsonObject.class);

        meta.setQueryFields(qryFields);

        ccfg.setTypeMetadata(Collections.singleton(meta));

        cfg.setCacheConfiguration(ccfg);

        cfg.setClientMode(client);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        super.beforeTestsStarted();

        startGrid(0);

        client = true;

        startGrid(1);
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        super.afterTestsStopped();

        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    public void testQuery() throws Exception {
        IgniteCache<Integer, JsonObject> cache = ignite(1).cache(null);

        assertNotNull(cache);

        JsonProvider provider = IgniteJson.jsonProvider(ignite(1));

        for (int i = 0; i < 10; i++) {
            JsonObjectBuilder person = provider.createObjectBuilder();

            person.add("name", "n-" + i);
            person.add("salary", (i + 1) * 1000);
            person.add("id", i);

            JsonObjectBuilder addr = provider.createObjectBuilder();

            addr.add("street", "s-" + i);

            person.add("address", addr);

            JsonObject obj = person.build();

            cache.put(i, obj);
        }

        SqlQuery<Integer, JsonObject> nameQry = new SqlQuery<>(JsonObject.class, "name = ?");

        nameQry.setArgs("n-5");

        List<Cache.Entry<Integer, JsonObject>> res = cache.query(nameQry).getAll();

        log.info("Res: " + res);

        assertEquals(1, res.size());

        SqlQuery<Integer, JsonObject> idQry = new SqlQuery<>(JsonObject.class, "id > ?");

        idQry.setArgs(5);

        res = cache.query(idQry).getAll();

        log.info("Res: " + res);

        assertEquals(4, res.size());

        SqlFieldsQuery avgQry = new SqlFieldsQuery("select avg(salary) from JsonObject");

        log.info("Res: " + cache.query(avgQry).getAll());

        SqlFieldsQuery fieldsQry = new SqlFieldsQuery("select name, salary from JsonObject where street = 's-3'");

        log.info("Res: " + cache.query(fieldsQry).getAll());

        fieldsQry = new SqlFieldsQuery("select address from JsonObject where street = 's-3'");

        List<List<?>> fieldsRes = cache.query(fieldsQry).getAll();

        assertEquals(1, fieldsRes.size());

        List<?> fields = fieldsRes.get(0);

        assertEquals(1, fields.size());

        JsonObject obj = (JsonObject)fields.get(0);

        log.info("Res: " + obj);

        assertEquals("s-3", obj.getString("street"));

        log.info("Res: " +
            cache.query(new SqlFieldsQuery("select street from JsonObject where street = 's-3'")).getAll());
    }

    /**
     * @throws Exception If failed.
     */
    public void testJson() throws Exception {
        IgniteCache<Integer, JsonObject> clientCache = ignite(1).cache(null);

        JsonProvider provider = IgniteJson.jsonProvider(ignite(1));

        JsonObjectBuilder builder = provider.createObjectBuilder();

        builder.add("name", "n1");
        builder.add("id", 1);

        JsonObject obj = builder.build();

        clientCache.put(1, obj);

        obj = clientCache.get(1);

        log.info("Get: " + obj);
    }
}
