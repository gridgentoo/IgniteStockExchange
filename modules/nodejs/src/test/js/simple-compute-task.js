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

function CharacterCountTask() {
}

CharacterCountTask.prototype.map = function(nodes, arg) {
  var words = arg.split(" ");

  var results = [];

  var nodeId = 0;

  function compute(args) {
    println(">>> Printing " + args);

    return args[0].length;
  }


  for (var word of words) {
    var node = nodes[nodeId];

    if (nodeId < nodes.length - 1) {
      nodeId++;
    }

    var TestUtils = require("./test-utils").TestUtils;
    var Apache = require(TestUtils.scriptPath());
    var ComputeJob = Apache.ComputeJob;
    results.push(new ComputeJob(compute, [word], node));
  }

  return results;
}

CharacterCountTask.prototype.reduce = function(results) {
  var sum = 0;

  for (var res of results) {
    sum += parseInt(res, 10);
  }

  return sum;
}

exports.CharacterCountTask = CharacterCountTask
