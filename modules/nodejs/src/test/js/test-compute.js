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

var Apache = require(TestUtils.scriptPath());
var Cache = Apache.Cache;
var Server = Apache.Server;

var assert = require("assert");

testCompute = function() {
  TestUtils.startIgniteNode(onStart.bind(null));
}

function onStart(error, ignite) {
  var cache = ignite.cache("mycache");

  var params = {"key0" : "val0"}

  for (var i = 0; i < 1000; ++i)
    params["key" + i] = "val" + i;

  cache.putAll(params, onPut.bind(null, ignite))

}

function onPut(ignite, error) {
  var comp = ignite.compute();

  var f = function () {
    print("Hello world!");
  }

  comp.affinityRun("mycache", "key999", f, onError.bind(null));
}

function onError(error, res) {
  console.log("Error "  + error);

  assert(error == null);

  assert(res.indexOf("AFFINITY RUN") !== -1);

  console.log("!!!!!!!!RES = " + res);

  TestUtils.testDone();
}