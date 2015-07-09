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
 * Demonstrates a simple use of Compute.mapReduce.
 * <p>
 * Phrase passed as task argument is split into jobs each taking one word. Then jobs are distributed among
 * cluster nodes. Each node computes word length and returns result to master node where total phrase length
 * is calculated on reduce stage.
 * <p>
 * Remote nodes should always be started with special configuration file which
 * enables P2P class loading: {@code examples/config/js/example-js-cache.xml}.
 * <p>
 * Alternatively you can run ExampleJsNodeStartup in another JVM which will start node
 * with {@code examples/config/js/example-js-cache.xml} configuration.
 */
function main() {
    /** Connect to node that started with {@code examples/config/js/example-js-cache.xml} configuration. */
    Ignition.start(['127.0.0.1:9095'], null, onConnect);

    function onConnect(err, ignite) {
        console.log(">>> Compute map reduce example started.");

        /**
         * Splits the received string to words, creates a child job for each word, and sends
         * these jobs to other nodes for processing. Each such job simply prints out the received word.
         */
        var map = function(nodes, str) {
            var words = str.split(" ");

            for (var i = 0; i < words.length; i++) {
                var job = function (word) {
                    print(">>> Printing '" + word + "' on this node from job.");

                    return word.length;
                };

                emit(job, words[i], nodes[i %  nodes.length]);
            }
        }

        /**
         * Reduces results received so far into one compound result to be returned.
         */
        var reduce = function(results) {
            var sum = 0;

            for (var i = 0; i < results.length; ++i) {
                sum += results[i];
            }

            return sum;
        }

        // Called when map reduced finished.
        var onMapReduce = function(err, cnt) {
            console.log(">>> Total number of characters in the phrase is '" + cnt + "'.");
            console.log(">>> End of compute map reduce example.");
        }

        ignite.compute().execute(map, reduce, "Hello Ignite Enabled World!", onMapReduce);
    }
}

main();