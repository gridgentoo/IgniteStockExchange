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
  * Start Ignite node with {@code examples/config/example-ignite.xml} configuration before running example.
  * <p>
  * Alternatively you can run ExampleJsNodeStartup which will
  * start node with {@code examples/config/example-ignite.xml} configuration.
  */
function main() {
    /** Connect to node that started with {@code examples/config/js/example-js-cache.xml} configuration. */
    Ignition.start(['127.0.0.1:8000..9000'], null).then(function(ignite) {
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

                //Add job with arguments to the node.
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

        return ignite.compute().mapReduce(map, reduce, "Hello Ignite World!");
    }).then(function(cnt){
        console.log(">>> Total number of characters in the phrase is '" + cnt + "'.");
        console.log(">>> End of compute map reduce example.");
    }).catch(function(err) {
        if (err !== null)
            console.log("Start remote node with config examples/config/example-ignite.xml. ");
    });
}

main();