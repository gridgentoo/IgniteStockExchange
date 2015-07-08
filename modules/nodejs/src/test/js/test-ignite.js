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

testIgniteVersion = function() {
    function igniteVer(err, res) {
        assert.equal(err, null);

        var verRegex = /([0-9]+)\.([0-9]+)\.([0-9]+)/;

        assert(verRegex.exec(res) !== null, "Incorrect ignite version [ver=" + res + "]");

        TestUtils.testDone();
    }

    function onStart(err, ignite) {
        assert.equal(err, null);

        ignite.version(igniteVer.bind(null));
    }

    TestUtils.startIgniteNode(onStart.bind(null));
}

testIgniteName = function() {
    function igniteName(err, res) {
        assert.equal(err, null);
        assert(res.indexOf("NodeJsIgniteSelfTest") > -1, "Incorrect ignite name [ver=" + res + "]");

        TestUtils.testDone();
    }

    function onStart(err, ignite) {
        assert.equal(err, null);

        ignite.name(igniteName.bind(null));
    }

    TestUtils.startIgniteNode(onStart.bind(null));
}

testCluster = function() {
    function igniteCluster(err, res) {
        assert.equal(err, null);
        assert(res.length > 0);

        assert(res[0].nodeId() !== null)

        var attrs = res[0].attributes();

        assert(attrs !== null);
        assert(attrs["os.version"] !== null, "Not correct node attributes [attr=" + res[0].attributes() + "]");

        TestUtils.testDone();
    }

    function onStart(err, ignite) {
        assert.equal(err, null);

        ignite.cluster(igniteCluster.bind(null));
    }

    TestUtils.startIgniteNode(onStart.bind(null));
}

testDestroyCache = function() {
    var cacheName = "NEW_CACHE";

    function onErrorPut(err) {
        assert(err !== null);

        TestUtils.testDone();
    }

    function onDestroy(cache, err) {
        assert(err === null, err);

        cache.put("1", "1", onErrorPut);
    }

    function onPut(ignite, cache, err) {
        assert(err === null, err);

        ignite.destroyCache(cacheName, onDestroy.bind(null, cache));
    }

    function onGetOrCreateCache(ignite, err, cache) {
        assert(err === null, err);

        cache.put("1", "1", onPut.bind(null, ignite, cache));
    }

    function onStart(err, ignite) {
        assert.equal(err, null);

        ignite.getOrCreateCache(cacheName, onGetOrCreateCache.bind(null, ignite));
    }

    TestUtils.startIgniteNode(onStart.bind(null));
}