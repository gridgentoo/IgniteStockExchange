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
  * Remote nodes should always be started with special configuration file which
  * enables P2P class loading: {@code 'ignite.{sh|bat} examples/config/js/example-js-cache.xml'}.
  * <p>
  * Alternatively you can run {@link ExampleJsNodeStartup} in another JVM which will
  * start node with {@code examples/config/js/example-js-cache.xml} configuration.
  */
function main() {
    /** Cache name. */
    var cacheName = "PutGetExampleCache";

    /** Connect to node that started with {@code examples/config/js/example-js-cache.xml} configuration. */
    Ignition.start(['127.0.0.1:9095'], null, onConnect);

    function onConnect(err, ignite) {
       ignite.getOrCreateCache(cacheName, function(err, cache) { putGetExample(ignite, cache); });
    }

    /** Execute individual puts and gets. */
    putGetExample = function(ignite, cache) {
        console.log(">>> Cache put-get example started.");

        var key = 1;

        // Store key in cache.
        cache.put(key, "1", onPut);

        function onPut(err) {
            console.log(">>> Stored values in cache.");

            cache.get(key, onGet);
        }

        function onGet(err, res) {
            console.log("Get value=" + res);

            putAllGetAll(ignite, cache);
        }
    }

    /** Execute bulk {@code putAll(...)} and {@code getAll(...)} operations. */
    function putAllGetAll(ignite, cache) {
        console.log(">>> Starting putAll-getAll example.");

        var keyCnt = 20;

        // Create batch.
        var batch = [];
        var keys = [];

        for (var i = keyCnt; i < keyCnt + keyCnt; ++i) {
            var key = i;
            var val = "bulk-" + i;

            keys.push(key);
            batch.push(new CacheEntry(key, val));
        }

        // Bulk-store entries in cache.
        cache.putAll(batch, onPutAll);

        function onPutAll(err) {
            console.log(">>> Stored values in cache.");

            // Bulk-get values from cache.
            cache.getAll(keys, onGetAll);
        }

        function onGetAll(err, entries) {
            for (var e of entries) {
                console.log("Got entry [key=" + e.key + ", value=" + e.value + ']');
            }

            // Destroying cache.
            ignite.destroyCache(cacheName, function(err) { console.log(">>> End of cache put-get example."); });
        }
    }
}

main();