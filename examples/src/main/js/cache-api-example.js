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

/**
  * This example demonstrates some of the cache rich API capabilities.
  * <p>
  * Start Ignite node with {@code examples/config/example-ignite.xml} configuration before running example.
  * <p>
  * Alternatively you can run ExampleJsNodeStartup which will
  * start node with {@code examples/config/example-ignite.xml} configuration.
  */
function main() {
    /** Cache name. */
    var cacheName = "ApiExampleCache";

    /** Connect to node that started with {@code examples/config/js/example-js-cache.xml} configuration. */
    Ignition.start(['127.0.0.1:8000..9000'], null, onConnect);

    function onConnect(err, ignite) {
        if (err !== null)
            throw "Start remote node with config examples/config/example-ignite.xml.";

        console.log(">>> Cache API example started.");

        // Create cache on server with cacheName.
        ignite.getOrCreateCache(cacheName, function(err, cache) {
            atomicMapOperations(ignite, cache);
        });
    }

    /**
     * Demonstrates cache operations similar to {@link ConcurrentMap} API. Note that
     * cache API is a lot richer than the JDK {@link ConcurrentMap}.
     */
    atomicMapOperations = function(ignite, cache) {
        console.log(">>> Cache atomic map operation examples.");

        cache.removeAllFromCache(function(err) {
            // Put and return previous value.
            cache.getAndPut(1, "1", onGetAndPut)
        });

        function onGetAndPut(err, entry) {
            console.log(">>> Get and put finished [result=" + entry + "]");

            // Put and do not return previous value.
            // Performs better when previous value is not needed.
            cache.put(2, "2", onPut);
        }

        onPut = function(err) {
            console.log(">>> Put finished.");

            // Put-if-absent.
            cache.putIfAbsent(4, "44", onPutIfAbsent);
        }

        onPutIfAbsent = function(err, res) {
            console.log(">>> Put if absent finished [result=" + res + "]");

            // Replace.
            cache.replaceValue(4, "55", "44", onReplaceValue);
        }

        onReplaceValue = function(err, res) {
            console.log(">>> Replace value finished [result=" + res + "]");

            // Replace not correct value.
            cache.replaceValue(4, "555", "44", onEnd);
        }

        onEnd = function(err) {
            console.log(">>> Replace finished.");

            // Destroying cache.
            ignite.destroyCache(cacheName, function(err) {
                console.log(">>> End of Cache API example.");
            });
        }
    }
}

main();