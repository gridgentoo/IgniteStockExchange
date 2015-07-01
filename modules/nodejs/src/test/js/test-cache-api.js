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

var Ignite = require(TestUtils.scriptPath());
var Entry = Ignite.Entry;

var assert = require("assert");

testPutGet = function() {
    startTest("mycache", {trace: [put, getExist], entry: ["key" , "6"]});
}

testPutGetObject = function() {
    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};

    startTest("mycache", {trace: [put, getExist], entry: [key , val]});
}

testPutContains = function() {
    startTest("mycache", {trace: [put, containsKey], entry: ["key" , "6"]});
}

testContains = function() {
    startTest("mycache", {trace: [notContainsKey], entry: ["key" , "6"]});
}

testPutContainsAll = function() {
    startTest("mycache", {trace: [putAll, containsKeys], entry: objectEntries()});
}

testNotContainsAll = function() {
    startTest("mycache", {trace: [notContainsKeys], entry: stringEntries()});
}

testRemove = function() {
    startTest("mycache", {trace: [put, getExist, remove, getNonExist], entry: ["key" , "6"]});
}

testRemoveNoKey = function() {
    startTest("mycache", {trace: [remove, getNonExist], entry: ["key" , "6"]});
}

testPutAllGetAll = function() {
    startTest("mycache", {trace: [putAll, getAll], entry: stringEntries()});
}

testPutAllObjectGetAll = function() {
    startTest("mycache", {trace: [putAll, getAll], entry: objectEntries()});
}

testRemoveAllObjectGetAll = function() {
    startTest("mycache", {trace: [putAll, getAll, removeAll, getNone], entry: objectEntries()});
}

testRemoveAll = function() {
    startTest("mycache", {trace: [putAll, getAll, removeAll, getNone], entry: stringEntries()});
}

testIncorrectCacheName = function() {
    startTest("mycache1", {trace: [incorrectPut], entry: ["key", "6"]});
}

testGetAndPut = function() {
    function onGetAndPut(err, res) {
        assert(err === null, "Get error on get and put [err=" + err + "]");
        assert(res === "6", "Incorrect result for getAndPut [expected=6, val" + res + "]");

        TestUtils.testDone();
    }

    function getAndPut(cache, entry, next) {
        cache.getAndPut("key", "7", onGetAndPut);
    }

    startTest("mycache", {trace: [put, getAndPut], entry: ["key", "6"]});
}

testGetAndPutIfAbsent = function() {
    function getAndPutIfAbsent(cache, entry, next) {
        cache.getAndPutIfAbsent("key", "7", onGetAndPutIfAbsent);

        function onGetAndPutIfAbsent(err, res) {
            assert(err === null, "Get error on get and put [err=" + err + "]");
            assert(res === "6", "Incorrect result for getAndPutIfAbsent [expected=6, val" + res + "]");

            next();
        }
    }

    startTest("mycache", {trace: [put, getAndPutIfAbsent, getExist], entry: ["key", "6"]});
}

testPutIfAbsent = function() {
    function putIfAbsent(cache, entry, next) {
        cache.putIfAbsent("key", "7", onPutIfAbsent);

        function onPutIfAbsent(err, res) {
            assert(err === null, "Get error on get and put [err=" + err + "]");
            assert(res === false, "Incorrect result for putIfAbsent [expected=false, val" + res + "]");

            next();
        }
    }

    startTest("mycache", {trace: [put, putIfAbsent, getExist], entry: ["key", "6"]});
}

testRemoveValue = function() {
    function removeValue(cache, entry, next) {
        cache.removeValue("key", "7", onRemoveValue);

        function onRemoveValue(err, res) {
            assert(err === null, "Get error on get and put [err=" + err + "]");
            assert(res === false, "Incorrect result for onRemoveValue [expected=false, val" + res + "]");

            next();
        }
    }

    startTest("mycache", {trace: [put, removeValue, getExist], entry: ["key", "6"]});
}

testGetAndRemove = function() {
    function getAndRemove(cache, entry, next) {
        cache.getAndRemove("key", onGetAndRemove);

        function onGetAndRemove(err, res) {
            assert(err === null, "Get error on get and put [err=" + err + "]");
            assert(res === "6", "Incorrect result for getAndPut [expected=6, val" + res + "]");

            next();
        }
    }

    startTest("mycache", {trace: [put, getAndRemove, getNone], entry: ["key", "6"]});
}

testRemoveAllFromCache = function() {
    function removeAllFromCache(cache, entry, next) {
        cache.removeAllFromCache(next);
    }

    startTest("mycache", {trace: [put, removeAllFromCache, getNone], entry: ["key", "6"]});
}

testReplace = function() {
    function replace(cache, entry, next) {
        cache.replace(entry[0], "7", onReplace.bind(null, cache));

        function onReplace(cache, err, res) {
            assert(err === null, "Get error on get and put [err=" + err + "]");
            assert(res === true, "Incorrect result for replace [expected=true, val" + res + "]");

            cache.get(entry[0], function(err, res) {
                assert(!err);
                assert("7" === res, "Get incorrect value on get [exp=7, val=" + res + "]");
                next();
            });
        }
    }

    startTest("mycache", {trace: [put, replace], entry: ["key", "6"]});
}

testReplaceObject = function() {
    function replace(cache, entry, next) {
        var newKey = {"key" :"7"};
        cache.replace(entry[0], newKey, onReplace.bind(null, cache));

        function onReplace(cache, err, res) {
            assert(err === null, "Get error on get and put [err=" + err + "]");
            assert(res === true, "Incorrect result for replace [expected=true, val" + res + "]");

            cache.get(entry[0], function(err, res) {
                assert(!err);
                assert(TestUtils.compareObject(newKey, res), "Get incorrect value on get.");

                next();
            });
        }
    }

    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};

    startTest("mycache", {trace: [put, replace], entry: [key, val]});
}

testGetAndReplaceObject = function() {
    function getAndReplace(cache, entry, next) {
        var newKey = {"key" :"7"};
        cache.getAndReplace(entry[0], newKey, onGetAndReplace.bind(null, cache));

        function onGetAndReplace(cache, err, res) {
            assert(err === null, "Get error on get and put [err=" + err + "]");
            assert(TestUtils.compareObject(val, res), "Get incorrect value on get.");

            next();
        }
    }

    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};

    startTest("mycache", {trace: [put, getAndReplace], entry: [key, val]});
}

testReplaceValueObject = function() {
    function replaceValue(cache, entry, next) {
        var newKey = {"key" :"7"};
        cache.replaceValue(entry[0], newKey, entry[1], onReplaceValue.bind(null, cache));

        function onReplaceValue(cache, err, res) {
            assert(err === null, "Get error on get and put [err=" + err + "]");
            assert(res === true, "Incorrect result for replace [expected=true, val" + res + "]");
            next();
        }
    }

    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};

    startTest("mycache", {trace: [put, replaceValue], entry: [key, val]});
}

testIncorrectReplaceObject = function() {
    function replace(cache, entry, next) {
        cache.replace(entry[0], "7", onReplace.bind(null, cache));

        function onReplace(cache, err, res) {
            assert(err !== null, "Do not get error");
            assert(err.indexOf("Failed to update keys") > -1, "Incorrect error message: " + err);
            next();
        }
    }

    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};

    startTest("mycache", {trace: [put, replace], entry: [key, val]});
}

testSize = function() {
    function onSize(exp, next, cache, err, res) {
            assert(err === null, "Do not get error");
            assert(res === exp, "Incorrect size: " + res);

            next();
    }

    function size0(cache, entry, next) {
        cache.size(onSize.bind(null, 0, next, cache));
    }

     function size1(cache, entry, next) {
        cache.size(onSize.bind(null, 1, next, cache));
    }

    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};

    startTest("mycache", {trace: [size0, put, size1], entry: [key, val]});
}

function objectEntries() {
    entries = [];

    var key1 = {"name" : "Ann"};
    var key2 = {"name" : "Paul"};
    var val1 = {"age" : 12, "books" : ["1", "Book"]};
    var val2 = {"age" : 13, "books" : ["1", "Book"]};

    entries.push(new Entry(key1, val1));
    entries.push(new Entry(key2, val2));

    return entries;
}

function stringEntries() {
    entries = [];

    entries.push(new Entry("key1", "val1"));
    entries.push(new Entry("key2", "val2"));

    return entries;
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
    cache.put(entry[0], entry[1], next);
}

function containsKey(cache, entry, next) {
    cache.containsKey(entry[0], onContainsKey);

    function onContainsKey(err, val) {
        assert(err === null, "Error on contains key [err=" + err + "]");
        assert(val === true, "Incorrect result [expected=" + true + ", val=" + val + "]");

        TestUtils.testDone();
    }
}

function notContainsKey(cache, entry, next) {
    cache.containsKey(entry[0], onContainsKey);

    function onContainsKey(err, val) {
        assert(err === null, "Error on contains key [err=" + err + "]");
        assert(val === false, "Incorrect result [expected=" + false + ", val=" + val + "]");

        TestUtils.testDone();
    }
}

function containsKeys(cache, entries, next) {
    var keys = []

    for (var entry of entries) {
        keys.push(entry.key());
    }

    cache.containsKeys(keys, onContainsKeys);

    function onContainsKeys(err, val) {
        assert(err === null, "Error on contains key [err=" + err + "]");
        assert(val === true, "Incorrect result [expected=" + true + ", val=" + val + "]");

        TestUtils.testDone();
    }
}

function notContainsKeys(cache, entries, next) {
    var keys = []

    for (var entry of entries) {
        keys.push(entry.key());
    }

    cache.containsKeys(keys, onContainsKeys);

    function onContainsKeys(err, val) {
        assert(err === null, "Error on contains key [err=" + err + "]");
        assert(val === false, "Incorrect result [expected=" + false + ", val=" + val + "]");

        TestUtils.testDone();
    }
}

function getExist(cache, entry, next) {
    function onGet(error, value) {
        assert(!error);
        assert(TestUtils.compareObject(entry[1], value), "Get incorrect value on get [exp=" +
            JSON.stringify(entry[1]) + ", val=" + JSON.stringify(value) + "]");
        next();
    }

    cache.get(entry[0], onGet);
}

function remove(cache, entry, next) {
    cache.remove(entry[0], next);
}

function getNonExist(cache, entry, next) {
    cache.get(entry[0], onGet);

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
    var keys = []

    for (var entry of entries) {
        keys.push(entry.key());
    }

    cache.getAll(keys, onGetAll.bind(null, keys));

    var expected = entries;

    function onGetAll(keys, error, values) {
        assert(!error, error);

        assert(values.length === keys.length, "Values length is incorrect "
            + "[expected=" + keys.length + ", real=" + values.length + "]");

        for (var i = 0; i < keys.length; ++i) {
            var key = keys[i];

            var foundVal = null;

            for (var j = 0; j < values.length; ++j) {
                if (TestUtils.compareObject(key, values[j].key())) {
                    foundVal = values[j];
                }
            }

            var foundExp = null;

            for (var j = 0; j < expected.length; ++j) {
                if (TestUtils.compareObject(key, expected[j].key())) {
                    foundExp = expected[j];
                }
            }

            assert(foundVal !== null, "Cannot find key. [key=" + key + "].");
            assert(foundExp !== null, "Cannot find key. [key=" + key + "].");

            assert(TestUtils.compareObject(foundExp, foundVal), "Incorrect value");
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
    cache.put(entry[0], entry[1], callback);

    function callback(error) {
        assert(!!error, "Do not get error for not exist cache");
        assert(error.indexOf("Failed to find cache for given cache name") !== -1,
            "Incorrect message on not exist cache. " + error);

        next();
    }
}