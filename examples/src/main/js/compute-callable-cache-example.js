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

var cacheName = "ComputeCallableCacheExample";

Ignition.start(['127.0.0.1:9095'], null, onConnect);

function onConnect(err, ignite) {
    console.log(">>> Compute callable example started.");

    var f = function (args) {
        print(">>> Hello node: " + ignite.name());

        var cache = ignite.getOrCreateCache(args);

        cache.put(ignite.name(), "Hello");

        return ignite.name();
    }

    var onRunScript = function(err, igniteName) {
        var cache = ignite.cache(cacheName);

        cache.get(igniteName, function(err, res) {
                console.log(res+ " " + igniteName);

                console.log(">>> Check all nodes for output (this node is also part of the cluster).");
            });
    }

    ignite.compute().runScript(f, cacheName, onRunScript);
}