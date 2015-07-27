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
import org.apache.ignite.cache.query.*;
import org.apache.ignite.cache.query.annotations.*;
import org.apache.ignite.configuration.*;

import java.io.*;

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
    public void _testSqlQuery() throws Exception {
        //TODO: fix query for simple strings.
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
    public void testSqlFieldsMeta() throws Exception {
        initCache();

        runJsScript("testSqlFieldsMeta");
    }

    /**
     * @throws Exception If failed.
     */
    public void testCloseQuery() throws Exception {
        initCache();

        runJsScript("testCloseQuery");
    }

    /**
     * @throws Exception If failed.
     */
    public void testSqlFieldsGetAllQuery() throws Exception {
        initCache();

        runJsScript("testSqlFieldsGetAllQuery");
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
        CacheConfiguration<Integer, Person> personCacheCfg = new CacheConfiguration<>("person");
        personCacheCfg.setIndexedTypes(Integer.class, Person.class);

        IgniteCache<Integer, Person> personCache = grid(0).getOrCreateCache(personCacheCfg);

        personCache.clear();

        Person p1 = new Person("John", "Doe", 2000);
        Person p2 = new Person("Jane", "Doe", 1000);
        Person p3 = new Person("John", "Smith", 1000);
        Person p4 = new Person("Jane", "Smith", 2000);

        personCache.put(p1.getId(), p1);
        personCache.put(p2.getId(), p2);
        personCache.put(p3.getId(), p3);
        personCache.put(p4.getId(), p4);

        SqlQuery<Integer, Person> qry = new SqlQuery<>(Person.class, "salary > ? and salary <= ?");

        qry.setArgs(1000, 2000);

        assertEquals(2, personCache.query(qry).getAll().size());
    }

    /**
     * Person class.
     */
    public static class Person implements Serializable {
        /** Person id. */
        private static int PERSON_ID = 0;

        /** Person ID (indexed). */
        @QuerySqlField(index = true)
        private Integer id;

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
        Person(String firstName, String lastName, double salary) {
            id = PERSON_ID++;

            this.firstName = firstName;
            this.lastName = lastName;
            this.salary = salary;
        }

        /**
         * @param firstName First name.
         */
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        /**
         * @return First name.
         */
        public String getFirstName() {
            return firstName;
        }

        /**
         * @param lastName Last name.
         */
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        /**
         * @return Last name.
         */
        public String getLastName() {
            return lastName;
        }

        /**
         * @param id Id.
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         * @param salary Salary.
         */
        public void setSalary(double salary) {
            this.salary = salary;
        }

        /**
         * @return Salary.
         */
        public double getSalary() {

            return salary;
        }

        /**
         * @return Id.
         */
        public Integer getId() {
            return id;
        }
    }
}
