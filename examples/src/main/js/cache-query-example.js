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

var apacheIgnite = require("apache-ignite");

var Ignition = apacheIgnite.Ignition;
var SqlQuery = apacheIgnite.SqlQuery;
var SqlFieldsQuery = apacheIgnite.SqlFieldsQuery;
var CacheEntry = apacheIgnite.CacheEntry;


/**
  * Cache queries example. This example demonstrates SQL queries over cache.
  * <p>
  * Start Ignite node with {@code examples/config/example-ignite.xml} configuration before running example.
  * <p>
  * Alternatively you can run ExampleJsNodeStartup which will
  * start node with {@code examples/config/example-ignite.xml} configuration.
  */
main = function() {
    /** Cache name. */
    var cacheName = "CacheQueryExample";

    /** Connect to node that started with {@code examples/config/js/example-js-cache.xml} configuration. */
    Ignition.start(['127.0.0.1:8000..9000'], null, onConnect);

    function onConnect(err, ignite) {
        if (err !== null)
            throw "Start remote node with config examples/config/example-ignite.xml.";

        console.log(">>> Cache query example started.");

        var entries = initializeEntries();

        ignite.getOrCreateCache(cacheName, function(err, cache) {
            cacheQuery(ignite, cache, entries);
        });
    }

    function cacheQuery(ignite, cache, entries) {
        cache.putAll(entries, onCachePut);

        function onCachePut(err) {
            console.log(">>> Create cache for people.")

            //SQL clause which selects salaries based on range.
            var qry = new SqlQuery("salary > ? and salary <= ?");
            qry.setReturnType("Object");

            // Set page size for query.
            qry.setPageSize(2);

            //Set salary range.
            qry.setArguments([0, 2000]);

            var fullRes = [];

            //This function is called when we get part of query result.
            qry.on("page", function(res) {
                console.log(">>> Get result on page: " + JSON.stringify(res));

                fullRes = fullRes.concat(res);
            });

            //This function is called when query is finished.
            qry.on("end", function(err) {
                console.log(">>> People with salaries between 0 and 2000 (queried with SQL query): " +
                    JSON.stringify(fullRes));

                // Destroying cache.
                ignite.destroyCache(cacheName, function(err) {
                    console.log(">>> End of query example.");
                });
            });

            //Run query.
            ignite.cache(cacheName).query(qry);
        }
    }

    // Initialize cache for people.
    function initializeEntries() {
        var key1 = "1";
        var value1 = {"firstName" : "John", "lastName" : "Doe", "salary" : 2000};
        var key2 = "2";
        var value2 = {"firstName" : "Jane", "lastName" : "Doe", "salary" : 1000};
        var key3 = "3";
        var value3 = {"firstName" : "John", "lastName" : "Smith", "salary" : 1000};
        var key4 = "4";
        var value4 = {"firstName" : "Jane", "lastName" : "Smith", "salary" : 2000};
        var key5 = "5";
        var value5 = {"firstName" : "Ann", "lastName" : "Smith", "salary" : 3000};

        return [new CacheEntry(key1, value1), new CacheEntry(key2, value2),
            new CacheEntry(key3, value3), new CacheEntry(key4, value4)];
    }
}

main();