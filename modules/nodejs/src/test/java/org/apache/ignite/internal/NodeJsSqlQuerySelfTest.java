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

package org.apache.ignite.internal;

import org.apache.ignite.*;
import org.apache.ignite.cache.affinity.*;
import org.apache.ignite.cache.query.annotations.*;
import org.apache.ignite.configuration.*;

import java.io.*;
import java.util.*;

/**
 * Node js sql query test.
 */
public class NodeJsSqlQuerySelfTest extends NodeJsAbstractTest {
    /**
     * Constructor.
     */
    public NodeJsSqlQuerySelfTest() {
        super("test-query.js");
    }

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        startGrid(0);
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    public void testSqlQuery() throws Exception {
        runJsScript("testSqlQuery");
    }

    /**
     * @throws Exception If failed.
     */
    public void testSqlFieldsQuery() throws Exception {
        initCache();

        runJsScript("testSqlFieldsQuery");
    }

    /**
     * @throws Exception If failed.
     */
    public void testSqlQueryWithParams() throws Exception {
        initCache();

        runJsScript("testSqlQueryWithParams");
    }

    /**
     * Init cache.
     */
    private void initCache() {
        CacheConfiguration<UUID, Organization> orgCacheCfg = new CacheConfiguration<>("organization");
        orgCacheCfg.setIndexedTypes(UUID.class, Organization.class);

        CacheConfiguration<AffinityKey<UUID>, Person> personCacheCfg = new CacheConfiguration<>("person");
        personCacheCfg.setIndexedTypes(AffinityKey.class, Person.class);

        IgniteCache<UUID, Organization> orgCache = grid(0).getOrCreateCache(orgCacheCfg);

        Organization org1 = new Organization("ApacheIgnite");
        Organization org2 = new Organization("Other");

        orgCache.put(org1.id, org1);
        orgCache.put(org2.id, org2);

        IgniteCache<AffinityKey<UUID>, Person> personCache = grid(0).getOrCreateCache(personCacheCfg);

        Person p1 = new Person(org1, "John", "Doe", 2000);
        Person p2 = new Person(org1, "Jane", "Doe", 1000);
        Person p3 = new Person(org2, "John", "Smith", 1000);
        Person p4 = new Person(org2, "Jane", "Smith", 2000);

        personCache.put(p1.key(), p1);
        personCache.put(p2.key(), p2);
        personCache.put(p3.key(), p3);
        personCache.put(p4.key(), p4);
    }

    /**
     * Person class.
     */
    private static class Person implements Serializable {
        /** Person ID (indexed). */
        @QuerySqlField(index = true)
        private UUID id;

        /** Organization ID (indexed). */
        @QuerySqlField(index = true)
        private UUID orgId;

        /** First name (not-indexed). */
        @QuerySqlField
        private String firstName;

        /** Last name (not indexed). */
        @QuerySqlField
        private String lastName;

        /** Salary (indexed). */
        @QuerySqlField(index = true)
        private double salary;

        /** Custom cache key to guarantee that person is always collocated with its organization. */
        private transient AffinityKey<UUID> key;

        /**
         * @param org Organization.
         * @param firstName First name.
         * @param lastName Last name.
         * @param salary Salary.
         */
        Person(Organization org, String firstName, String lastName, double salary) {
            id = UUID.randomUUID();

            orgId = org.id;

            this.firstName = firstName;
            this.lastName = lastName;
            this.salary = salary;
        }

        /**
         * @return Custom affinity key to guarantee that person is always collocated with organization.
         */
        public AffinityKey<UUID> key() {
            if (key == null)
                key = new AffinityKey<>(id, orgId);

            return key;
        }
    }

    /**
     * Organization class.
     */
    private static class Organization implements Serializable {
        /** Organization ID (indexed). */
        @QuerySqlField(index = true)
        private UUID id;

        /** Organization name (indexed). */
        @QuerySqlField(index = true)
        private String name;

        /**
         * @param name Organization name.
         */
        Organization(String name) {
            id = UUID.randomUUID();

            this.name = name;
        }
    }
}
