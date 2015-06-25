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