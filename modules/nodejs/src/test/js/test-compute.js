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

var TestUtils = require("./test-utils").TestUtils;

var assert = require("assert");

testComputeAffinityRun = function() {
  TestUtils.startIgniteNode(onStart.bind(null, computeAffinityRun));
}

testComputeAffinityCall = function() {
  TestUtils.startIgniteNode(onStart.bind(null, computeAffinityCall));
}

testComputeExecute = function() {
  TestUtils.startIgniteNode(computeExecute);
}

function onStart(onPut, error, ignite) {
  var cache = ignite.cache("mycache");

  var params = {}

  for (var i = 900; i < 1000; ++i) {
    params["key" + i] = "val" + i;
  }

  cache.putAll(params, onPut.bind(null, ignite))
}

function computeAffinityRun(ignite, error) {
  var comp = ignite.compute();

  var f = function () {
    println("Hello world!");

    ignite.hello();
  }

  function onEnd(error) {
    assert(error == null);

    TestUtils.testDone();
  }

  comp.affinityRun("mycache", "key999", f, onEnd.bind(null));
}

function computeAffinityCall(ignite, error) {
  var comp = ignite.compute();

  var f = function () {
    return ignite.hello();
  }

  function onEnd(err, res) {
    assert(err == null);

    assert(res.indexOf("HAPPY") !== -1, "Incorrect result message. [mes=" + res + "].");

    TestUtils.testDone();
  }

  comp.affinityCall("mycache", "key999", f, onEnd.bind(null));
}

function computeExecute(error, ignite) {
  var map = function(nodes, arg, emit) {
    var words = arg.split(" ");

    for (var i = 0; i < words.length; i++) {
      var f = function (word) {
        println(">>> Printing " + word);

        return word.length;
      };

      emit(f, [words[i]], nodes[i %  nodes.length]);
    }
  };

  var reduce = function(results) {
    var sum = 0;

    for (var i = 0; i < results.length; ++i)
     sum += parseInt(results[i], 10);

    return sum;
  };

  var callback = function(err, res) {
    assert(err == null, "Get error on compute task. [err=" + err + "].");
    assert(res === 7, "Result is not correct. [expected=7, value=" + res + "].");

    TestUtils.testDone();
  }

  ignite.compute().execute(map, reduce, "Hi Alice", callback);
}
