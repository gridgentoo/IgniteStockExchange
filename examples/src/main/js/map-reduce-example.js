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

Ignition.start(['127.0.0.1:9095'], null, onConnect);

function onConnect(err, ignite) {
    console.log(">>> Compute task split example started.");

    var map = function(nodes, args) {
        var words = args.split(" ");

        for (var i = 0; i < words.length; i++) {
            var f = function (word) {
                print(">>> Printing '" + word + "' on this node from ignite job.");

                return word.length;
            };

            emit(f, words[i], nodes[i %  nodes.length]);
        }
    }

    var reduce = function(results) {
        var sum = 0;

        for (var i = 0; i < results.length; ++i) {
            sum += results[i];
        }

        return sum;
    }

    var onMapReduce = function(err, cnt) {
        console.log(">>> Total number of characters in the phrase is '" + cnt + "'.");
        console.log(">>> Check all nodes for output (this node is also part of the cluster).");
    }

    ignite.compute().execute(map, reduce, "Hello Ignite Enabled World!", onMapReduce);
}