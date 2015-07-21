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
var CacheEntry = Ignite.CacheEntry;

var assert = require("assert");

testPutGet = function() {
    var key = "key";
    var val = "6";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");
        cache.put(key, val).then(function() {
            return cache.get(key);
        }).then(function(res) {
            assert(TestUtils.compareObject(val, res), "Get incorrect value on get [exp=" +
                JSON.stringify(val) + ", val=" + JSON.stringify(res) + "]");
            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    }).catch(function (err) {
        assert(err === null, err);
    });
}

testPutGetObject = function() {
    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.get(key);
        }).then(function(res) {
            assert(TestUtils.compareObject(val, res), "Get incorrect value on get [exp=" +
                JSON.stringify(val) + ", val=" + JSON.stringify(res) + "]");
            TestUtils.testDone();
        })
    });
}

testPutContains = function() {
    var key = "key";
    var val = "6";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.containsKey(key);
        }).then(function(res) {
            assert(res === true, "Incorrect result [expected=" + true + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testContains = function() {
    var key = "key";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.containsKey(key).then(function(res) {
            assert(res === false, "Incorrect result [expected=" + false + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testPutContainsAll = function() {
    var entries = objectEntries();

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.putAll(entries).then(function(res) {
            var keys = []

            for (var entry of entries) {
                keys.push(entry.key);
            }

            return cache.containsKeys(keys);
        }).then(function(res) {
            assert(res === true, "Incorrect result [expected=" + true + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testNotContainsAll = function() {
    var entries = stringEntries();

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        var keys = []

        for (var entry of entries) {
            keys.push(entry.key);
        }

        cache.containsKeys(entries).then(function(res) {
            assert(res === false, "Incorrect result [expected=" + false + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testRemove = function() {
    var key = "key";
    var val = "6";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function(res) {
            return cache.get(key);
        }).then(function(res) {
            assert (res === val, "Incorrect result [expected=" + val + ", val=" + res + "]");

            return cache.remove(key);
        }).then(function(res) {
            assert (res === true, "Incorrect result [expected=" + true + ", val=" + res + "]");

            return cache.get(key);
        }).then(function(res) {
            assert (res === null, "Incorrect result [expected=" + null + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testRemoveNoKey = function() {
    var key = "key";
    var val = "6";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.remove(key).then(function(res) {
            assert (res === false, "Incorrect result [expected=" + false + ", val=" + res + "]");

            return cache.get(key);
        }).then(function(res) {
            assert (res === null, "Incorrect result [expected=" + null + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testPutAllGetAll = function() {
    var entries = stringEntries();

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.putAll(entries).then(function(res) {
            var keys = getKeys(entries);

            return cache.getAll(keys);
        }).then(function(res) {
            onGetAll(entries, res);

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testPutAllObjectGetAll = function() {
    var entries = objectEntries();

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.putAll(entries).then(function(res) {
            var keys = getKeys(entries);

            return cache.getAll(keys);
        }).then(function(res) {
            onGetAll(entries, res);

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testRemoveAllObjectGetAll = function() {
    var entries = objectEntries();
    var keys = getKeys(entries);

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.putAll(entries).then(function(res) {
            return cache.getAll(keys);
        }).then(function(res) {
            onGetAll(entries, res);

            return cache.removeAll(keys);
        }).then(function(res) {
            assert (res === true, "Incorrect result [expected=" + true + ", val=" + res + "]");

            return cache.getAll(keys);
         }).then(function(res) {
             for (var i = 0; i < res.length; ++i) {
                assert(res[i] === null, "Incorrect result [expected=" + null + ", val=" + res[i] + "]");
             }

             TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testRemoveAll = function() {
    var entries = stringEntries();
    var keys = getKeys(entries);

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.putAll(entries).then(function(res) {
            return cache.getAll(keys);
        }).then(function(res) {
            onGetAll(entries, res);

            return cache.removeAll(keys);
        }).then(function(res) {
            assert (res === true, "Incorrect result [expected=" + true + ", val=" + res + "]");

            return cache.getAll(keys);
         }).then(function(res) {
             for (var i = 0; i < res.length; ++i) {
                assert(res[i] === null, "Incorrect result [expected=" + null + ", val=" + res[i] + "]");
             }

             TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testIncorrectCacheName = function() {
    var key = "key";
    var val = "6";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache1");

        cache.put(key, val).then(function(res) {
            assert(false, "Do not get exception.");
        }).catch(function (err) {
            assert(err !== null, err);
            assert(err.indexOf("Failed to find cache for given cache name") !== -1,
                "Incorrect message on not exist cache. " + err);

            TestUtils.testDone();
        })
    });
}

testGetOrCreateCacheName = function() {
    var key = "key";
    var val = "6";

    TestUtils.startIgniteNode().then(function(ignite) {
        return ignite.getOrCreateCache("mycache2");
    }).then(function(cache) {
        return cache.put(key, val);
    }).then(function(res) {
        TestUtils.testDone();
    }).catch(function (err) {
        assert(err === null, err);
    });
}

testGetAndPut = function() {
    var key = "key";
    var val = "6";
    var val2 = "7";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.getAndPut(key, val2);
        }).then(function(res) {
            assert(res === val, "Incorrect result [expected=" + val + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testGetAndPutIfAbsent = function() {
    var key = "key";
    var val = "6";
    var val2 = "7";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.getAndPutIfAbsent(key, val2);
        }).then(function(res) {
            assert(res === val, "Incorrect result [expected=" + val + ", val=" + res + "]");

            return cache.get(key);
        }).then(function(res) {
            assert(res === val, "Incorrect result [expected=" + val + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testPutIfAbsent = function() {
    var key = "key";
    var val = "6";
    var val2 = "7";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.putIfAbsent(key, val2);
        }).then(function(res) {
            assert(res === false, "Incorrect result [expected=" + false + ", val=" + res + "]");

            return cache.get(key);
        }).then(function(res) {
            assert(res === val, "Incorrect result [expected=" + val + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testRemoveValue = function() {
    var key = "key";
    var val = "6";
    var val2 = "7";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.removeValue(key, val2);
        }).then(function(res) {
            assert(res === false, "Incorrect result [expected=" + false + ", val=" + res + "]");

            return cache.get(key);
        }).then(function(res) {
            assert(res === val, "Incorrect result [expected=" + val + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testGetAndRemove = function() {
    var key = "key";
    var val = "6";
    var val2 = "7";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.getAndRemove(key, val2);
        }).then(function(res) {
            assert(res === val, "Incorrect result [expected=" + val + ", val=" + res + "]");

            return cache.get(key);
        }).then(function(res) {
            assert(res === null, "Incorrect result [expected=" + null + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testRemoveAllFromCache = function() {
    var key = "key";
    var val = "6";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.removeAllFromCache();
        }).then(function(res) {
            return cache.get(key);
        }).then(function(res) {
            assert(res === null, "Incorrect result [expected=" + null + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testReplace = function() {
    var key = "key";
    var val = "6";
    var val2 = "7";

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.replace(key, val2);
        }).then(function(res) {
            assert(res === true, "Incorrect result [expected=" + true + ", val=" + res + "]");

            return cache.get(key);
        }).then(function(res) {
            assert(res === val2, "Incorrect result [expected=" + val2 + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testReplaceObject = function() {
    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};
    var val2 = {"key" :"7"};

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.replace(key, val2);
        }).then(function(res) {
            assert(res === true, "Incorrect result [expected=" + true + ", val=" + res + "]");

            return cache.get(key);
        }).then(function(res) {
            assert(TestUtils.compareObject(val2, res), "Incorrect result [expected=" + val2 + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testGetAndReplaceObject = function() {
    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};
    var val2 = {"key" :"7"};

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.getAndReplace(key, val2);
        }).then(function(res) {
            assert(TestUtils.compareObject(val, res), "Incorrect result [expected=" + val + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testReplaceValueObject = function() {
    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};
    var val2 = {"key" :"7"};

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.put(key, val).then(function() {
            return cache.replaceValue(key, val2, val);
        }).then(function(res) {
            assert(res === true, "Incorrect result [expected=" + true + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

testSize = function() {
    var key = {"name" : "Paul"};
    var val = {"age" : 12, "books" : ["1", "Book"]};

    TestUtils.startIgniteNode().then(function(ignite) {
        var cache = ignite.cache("mycache");

        cache.size().then(function(res) {
            assert(res === 0, "Incorrect result [expected=" + 0 + ", val=" + res + "]");

            return cache.put(key, val);
        }).then(function() {
            return cache.size();
        }).then(function(res) {
            assert(res === 1, "Incorrect result [expected=" + 1 + ", val=" + res + "]");

            TestUtils.testDone();
        }).catch(function (err) {
            assert(err === null, err);
        })
    });
}

function objectEntries() {
    entries = [];

    var key1 = {"name" : "Ann"};
    var key2 = {"name" : "Paul"};
    var val1 = {"age" : 12, "books" : ["1", "Book"]};
    var val2 = {"age" : 13, "books" : ["1", "Book"]};

    entries.push(new CacheEntry(key1, val1));
    entries.push(new CacheEntry(key2, val2));

    return entries;
}

function stringEntries() {
    entries = [];

    entries.push(new CacheEntry("key1", "val1"));
    entries.push(new CacheEntry("key2", "val2"));

    return entries;
}

function onGetAll(expected, values) {
    var keys = getKeys(expected);

    assert(values.length === keys.length, "Values length is incorrect "
        + "[expected=" + keys.length + ", real=" + values.length + "]");

    for (var i = 0; i < keys.length; ++i) {
        var key = keys[i];

        var foundVal = null;

        for (var j = 0; j < values.length; ++j) {
            if (TestUtils.compareObject(key, values[j].key)) {
                foundVal = values[j];
            }
        }

        var foundExp = null;

        for (var j = 0; j < expected.length; ++j) {
            if (TestUtils.compareObject(key, expected[j].key)) {
                foundExp = expected[j];
            }
        }

        assert(foundVal !== null, "Cannot find key. [key=" + key + "].");
        assert(foundExp !== null, "Cannot find key. [key=" + key + "].");

        assert(TestUtils.compareObject(foundExp, foundVal), "Incorrect value");
    }

    return true;
}

function getKeys(entries) {
    var keys = []

    for (var entry of entries) {
        keys.push(entry.key);
    }

    return keys;
}