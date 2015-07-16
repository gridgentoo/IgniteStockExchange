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
    Ignition.start(['127.0.0.1:8000..9000'], null).then(function(ignite) {
        console.log(">>> Cache API example started.");

        // Create cache on server with cacheName.
        ignite.getOrCreateCache(cacheName).then(function(cache){
            atomicMapOperations(ignite, cache);
        });
    }).catch(function(err) {
        if (err !== null)
            console.log("Start remote node with config examples/config/example-ignite.xml.");
    });

    /**
     * Demonstrates cache operations similar to {@link ConcurrentMap} API. Note that
     * cache API is a lot richer than the JDK {@link ConcurrentMap}.
     */
    function atomicMapOperations (ignite, cache) {
        console.log(">>> Cache atomic map operation examples.");

        cache.removeAllFromCache().then(function(){
            // Put and return previous value.
            return cache.getAndPut(1, "1");
        }).then(function(entry){
            console.log(">>> Get and put finished [result=" + entry + "]");

            // Put and do not return previous value.
            // Performs better when previous value is not needed.
            return cache.put(2, "2")
        }).then(function(){
            console.log(">>> Put finished.");

            // Put-if-absent.
            return cache.putIfAbsent(4, "44");
        }).then(function(res){
            console.log(">>> Put if absent finished [result=" + res + "]");

            // Replace.
            return cache.replaceValue(4, "55", "44");
        }).then(function(res) {
            console.log(">>> Replace value finished [result=" + res + "]");

            // Replace not correct value.
            return cache.replaceValue(4, "555", "44");
        }).then(function(res) {
            console.log(">>> Replace finished.");

            //Destroying cache.
            return ignite.destroyCache(cacheName);
        }).then(function(){
             console.log(">>> End of Cache API example.");
        })
    }
}

main();