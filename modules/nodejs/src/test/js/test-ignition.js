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
var Ignition = Ignite.Ignition;

var assert = require("assert");

testIgnitionFail = function ()  {
    Ignition.start(['127.0.0.3:9091', '127.0.0.1:9092'], null).then(function(ignite) {
        assert(false, "Do not get an error.")
    }).catch(function(err){
        assert(err !== null);
        assert(err.indexOf("Cannot connect to servers.") > -1, "Incorrect error message: " + err);

        TestUtils.testDone();
    });
}

testIgnitionStartSuccess = function() {
    Ignition.start(['127.0.0.0:9095', '127.0.0.1:9095'], null).then(function(ignite) {
        TestUtils.testDone();
    }).catch(function(err){
        assert(err === null);
    });
}

testIgnitionStartSuccessWithSeveralPorts = function() {
    Ignition.start(['127.0.0.1:9090..9100'], null).then(function(ignite) {
        var server = ignite.server();
        var host = server.host();

        assert(host.indexOf('127.0.0.1') !== -1, "Incorrect host.");

        TestUtils.testDone();
    }).catch(function(err){
        assert(err === null);
    });
}

testIgnitionNotStartWithSeveralPorts = function() {
    Ignition.start(['127.0.0.1:9090...9100'], null).then(function(ignite) {
        assert(false, "Do not get an error.")
    }).catch(function(err){
        assert(err !== null);
        assert(err.indexOf("Incorrect address format") > -1, "Incorrect error message: " + err);

        TestUtils.testDone();
    });
}