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
var CacheEntry = apacheIgnite.CacheEntry;

/**
  * This example demonstrates very basic operations on cache, such as 'put' and 'get'.
  * <p>
  * Start Ignite node with {@code examples/config/example-ignite.xml} configuration before running example.
  * <p>
  * Alternatively you can run ExampleJsNodeStartup which will
  * start node with {@code examples/config/example-ignite.xml} configuration.
  */
function main() {
    /** Cache name. */
    var cacheName = "PutGetExampleCache";

    /** Connect to node that started with {@code examples/config/js/example-js-cache.xml} configuration. */
    Ignition.start(['127.0.0.1:8000..9000'], null).then(function(ignite) {
        console.log(">>> Cache put-get example started.");

        // Create cache on server with cacheName.
        ignite.getOrCreateCache(cacheName).then(function(cache){
            putGetExample(ignite, cache);
        });
    }).catch(function(err) {
        if (err !== null)
            console.log("Start remote node with config examples/config/example-ignite.xml.");
    });

    /** Execute puts and gets. */
    function putGetExample(ignite, cache) {
        var key = 1;

        var keyCnt = 20;

        // Create batch.
        var batch = [];
        var keys = [];

        for (var i = 0; i < keyCnt; ++i) {
            var key = i;
            var val = "bulk-" + i;

            keys.push(key);
            batch.push(new CacheEntry(key, val));
        }

        // Store key in cache.
        cache.put(key, "1").then(function(){
            console.log(">>> Stored values in cache.");

            // Get value.
            return cache.get(key);
        }).then(function(entry){
            console.log(">>> Get finished [result=" + entry + "]");

            console.log(">>> Starting putAll-getAll example.");

            // Bulk-store entries in cache.
            return cache.putAll(batch);
        }).then(function(){
            console.log(">>> Stored values in cache.");

            // GetAll keys.
            return cache.getAll(keys);
        }).then(function(entries){
            for (var e of entries) {
                console.log(">>> Got entry [key=" + e.key + ", value=" + e.value + ']');
            }

            // Destroying cache.
            return ignite.destroyCache(cacheName);
        }).then(function(){
             console.log(">>> End of cache put-get example.")
        })
    }
}

main();