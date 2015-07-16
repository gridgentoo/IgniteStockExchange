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
    TestUtils.startIgniteNode().then(function(ignite) {
        return ignite.version();
    }).then(function(res) {
        var verRegex = /([0-9]+)\.([0-9]+)\.([0-9]+)/;

        assert(verRegex.exec(res) !== null, "Incorrect ignite version [ver=" + res + "]");

        TestUtils.testDone();
    }).catch(function (err) {
        assert(err === null, err);
    });
}

testIgniteName = function() {
    TestUtils.startIgniteNode().then(function(ignite) {
        return ignite.name();
    }).then(function(res) {
        assert(res.indexOf("NodeJsIgniteSelfTest") > -1, "Incorrect ignite name [ver=" + res + "]");

        TestUtils.testDone();
    }).catch(function (err) {
        assert(err === null, err);
    });
}

testCluster = function() {
    TestUtils.startIgniteNode().then(function(ignite) {
        return ignite.cluster();
    }).then(function(res) {
        assert(res.length > 0);
        assert(res[0].nodeId() !== null)

        var attrs = res[0].attributes();

        assert(attrs !== null);
        assert(attrs["os.version"] !== null, "Not correct node attributes [attr=" + res[0].attributes() + "]");

        TestUtils.testDone();
    }).catch(function (err) {
        assert(err === null, err);
    });
}

testDestroyCache = function() {
    var cacheName = "NEW_CACHE";

    TestUtils.startIgniteNode().then(function(ignite) {
        ignite.getOrCreateCache(cacheName).then(function(cache) {
            return cache.put("1", "1");
        }).then(function() {
            return ignite.destroyCache(cacheName);
        }).then(function() {
            var cache0 = ignite.cache(cacheName);

            cache0.put("1", "1").then(function() {
                assert(false, "Do not get an error.");
            }).catch(function(err){
                assert(err !== null, "Do nto get an error");
                assert(err.indexOf("Failed to find cache for given cache name") > -1, "Incorrect error message: " + err);

                TestUtils.testDone();
            });
        }).catch(function(err) {
            assert(err === null, err);
        })
    }).catch(function (err) {
        assert(err === null, err);
    });
}