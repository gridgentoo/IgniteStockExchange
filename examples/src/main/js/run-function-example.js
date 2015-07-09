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
  * Demonstrates using of Compute job execution on the cluster.
  * <p>
  * This example takes a sentence composed of multiple words and counts number of non-space
  * characters in the sentence by having each compute job count characters in each individual
  * word.
* <p>
  * <p>
  * Remote nodes should always be started with special configuration file which
  * enables P2P class loading: {@code 'ignite.{sh|bat} examples/config/js/example-js-cache.xml'}.
  * <p>
  * Alternatively you can run ExampleJsNodeStartup in another JVM which will
  * start node with {@code examples/config/js/example-js-cache.xml} configuration.
  */
function main() {
    /** Connect to node that started with {@code examples/config/js/example-js-cache.xml} configuration. */
    Ignition.start(['127.0.0.1:9095'], null, onConnect);

    function onConnect(err, ignite) {
        if (err !== null)
            throw "Start remote node with config examples/config/js/example-js-cache.xml.";

        console.log(">>> Compute callable example started");

        var job = function (args) {
            var words = args.split(" ");

            var sum = 0;

            for (var i = 0; i < words.length; ++i) {
                sum += words[i].length;
            }

            return sum;
        }

        var onRun = function(err, sum) {
            console.log(">>> Total number of characters in the phrase is '" + sum + "'.");
            console.log(">>> End of compute callable example.");
        }

        // Execute job on ignite server node.
        ignite.compute().run(job, "Hello Ignite Enabled World!", onRun);
    }
}

main();