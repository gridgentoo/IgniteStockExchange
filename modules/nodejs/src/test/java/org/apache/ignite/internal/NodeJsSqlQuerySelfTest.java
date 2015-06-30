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
        CacheConfiguration<UUID, Person> personCacheCfg = new CacheConfiguration<>("person");
        personCacheCfg.setIndexedTypes(UUID.class, Person.class);

        IgniteCache<UUID, Person> personCache = grid(0).getOrCreateCache(personCacheCfg);

        Person p1 = new Person("John", "Doe", 2000);
        Person p2 = new Person("Jane", "Doe", 1000);
        Person p3 = new Person("John", "Smith", 1000);
        Person p4 = new Person("Jane", "Smith", 2000);

        personCache.put(p4.getId(), p1);
        personCache.put(p4.getId(), p2);
        personCache.put(p4.getId(), p3);
        personCache.put(p4.getId(), p4);
    }

    /**
     * Person class.
     */
    public static class Person implements Serializable {
        /** Person ID (indexed). */
        @QuerySqlField(index = true)
        private UUID id;

        /** First name (not-indexed). */
        @QuerySqlField
        private String firstName;

        /** Last name (not indexed). */
        @QuerySqlField
        private String lastName;

        /** Salary (indexed). */
        @QuerySqlField(index = true)
        private double salary;

        /**
         * @param firstName First name.
         * @param lastName Last name.
         * @param salary Salary.
         */
        Person( String firstName, String lastName, double salary) {
            id = UUID.randomUUID();

            this.firstName = firstName;
            this.lastName = lastName;
            this.salary = salary;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public void setSalary(double salary) {
            this.salary = salary;
        }

        public double getSalary() {

            return salary;
        }

        public UUID getId() {
            return id;
        }
    }
}
