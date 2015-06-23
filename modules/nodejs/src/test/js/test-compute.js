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
  TestUtils.startIgniteNode(onStart.bind(null, onPut));
}

testComputeAffinityCall = function() {
  TestUtils.startIgniteNode(onStart.bind(null, onPut1));
}

testComputeExecute = function() {
  TestUtils.startIgniteNode(onStart1);
}

function onStart(locOnPut, error, ignite) {
  var cache = ignite.cache("mycache");

  var params = {}

  for (var i = 900; i < 1000; ++i)
    params["key" + i] = "val" + i;

  cache.putAll(params, locOnPut.bind(null, ignite))
}

function onPut(ignite, error) {
  var comp = ignite.compute();

  var f = function () {
    println("Hello world!");

    ignite.hello();
  }

  comp.affinityRun("mycache", "key999", f, onError.bind(null));
}

function onError(error) {
  console.log("Error "  + error);

  assert(error == null);

  TestUtils.testDone();
}

function onPut1(ignite, error) {
  var comp = ignite.compute();

  var f = function () {
    println("Hello world!");

    ignite.hello();
  }

  comp.affinityCall("mycache", "key999", f, onError1.bind(null));
}

function onError1(error, res) {
  console.log("Error "  + error);

  assert(error == null);

  console.log("!!!!!!!!RES = " + res);

  TestUtils.testDone();
}

function onStart1(error, ignite) {
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

  ignite.compute().execute(map, reduce, "Hi Alice", onComputeResult);
}

function onComputeResult(error, res) {
  console.log("Error "  + error);

  assert(error == null);

  console.log("!!!!!!!!EXECUTE TASK RESULT = " + res);

  assert(res === 7, "Result is not correct. [expected=7, value=" + res + "].")

  TestUtils.testDone();
}
