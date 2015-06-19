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

testPutGet = function() {
  startTest("mycache", {trace: [put, getExist], entry: "6"});
}

testRemove = function() {
  startTest("mycache", {trace: [put, getExist, remove, getNonExist], entry: "6"});
}

testRemoveNoKey = function() {
  startTest("mycache", {trace: [remove, getNonExist], entry: "6"});
}

testPutAllGetAll = function() {
  startTest("mycache", {trace: [putAll, getAll], entry: {"key1": "val1", "key2" : "val2"}});
}

testRemoveAll = function() {
  startTest("mycache", {trace: [putAll, getAll, removeAll, getNone], entry: {"key1": "val1", "key2" : "val2"}});
}

testIncorrectCacheName = function() {
  startTest("mycache1", {trace: [incorrectPut], entry: "6"});
}

function startTest(cacheName, testDescription) {
  TestUtils.startIgniteNode(onStart.bind(null, cacheName, testDescription));
}

function onStart(cacheName, testDescription, error, ignite) {
  var cache = ignite.cache(cacheName);
  callNext();

  function callNext(error) {
    assert(!error);
    var next = testDescription.trace.shift();
    if (next)
        next.call(null, cache, testDescription.entry, callNext);
    else
        TestUtils.testDone();
  }
}

function put(cache, entry, next) {
  cache.put("key", entry, next);
}

function getExist(cache, entry, next) {
  cache.get("key", onGet);

  function onGet(error, value) {
    assert(!error);
    assert(value === entry);
    next();
  }
}

function remove(cache, entry, next) {
  cache.remove("key", next);
}

function getNonExist(cache, entry, next) {
  cache.get("key", onGet);

  function onGet(error, value) {
    assert(!error);
    assert(!value);
    next();
  }
}

function putAll(cache, entries, next) {
  cache.putAll(entries, next);
}

function getAll(cache, entries, next) {
  cache.getAll(Object.keys(entries), onGetAll);
  var expected = entries;

  function onGetAll(error, values) {
    assert(!error, error);

    var keys = Object.keys(expected);

    for (var i = 0; i < keys.length; ++i) {
      var key = keys[i];

      assert(!!values[key], "Cannot find key. [key=" + key + "].");

      assert(values[key] === expected[key], "Incorrect value. [key=" + key +
        ", expected=" + expected[key] + ", val= " + values[key] + "].");
    }
    next();
  }
}

function removeAll(cache, entries, next) {
  cache.removeAll(Object.keys(entries), next)
}

function getNone(cache, entries, next) {
  cache.getAll(Object.keys(entries), onGetAll);

  function onGetAll(error, values) {
    assert(!error, error);
    assert(!values || !Object.keys(values).length);

    next();
  }
}

function incorrectPut(cache, entry, next) {
  cache.put("key", entry, callback);

  function callback(error) {
    assert(!!error, "Do not get error for not exist cache");
    assert(error.indexOf("Failed to find cache for given cache name") !== -1,
      "Incorrect message on not exist cache. " + error);

    next();
  }
}