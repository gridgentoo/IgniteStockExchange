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

testStartWithoutKey = function() {
  TestUtils.startIgniteNode(onIncorrectStart);
}

testStartWithKey = function() {
  TestUtils.startIgniteNodeWithKey("secret-key", onStart);
}

testStartWithIncorrectKey = function() {
  TestUtils.startIgniteNodeWithKey("secret-key1", onIncorrectStart);
}

function onIncorrectStart(error, ignite) {
  assert(error != null, "Do not get authentication error");

  assert(error.indexOf("Authentication failed. Status code 401.") !== -1, "Incorrect error message: " + error);

  TestUtils.testDone();
}

function onStart(error, ignite) {
  assert(error === null, "Get error: " + error);

  assert(ignite !== null, "Cannot connect. Get null ignite.");

  var cache = ignite.cache("mycache");

  assert(cache !== null, "Cache is null.")

  cache.put("key", "6", onPut);
}

function onPut(error) {
    assert(error === null, "Error on put:" + error);

    TestUtils.testDone();
}