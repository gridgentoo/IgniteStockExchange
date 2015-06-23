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
  Ignition.start(['127.0.0.3:9091', '127.0.0.1:9092'], null, onConnect);

  function onConnect(error, server) {
    if (error) {
      if (error.indexOf("Cannot connect to servers.") == -1) {
        TestUtils.testFails("Incorrect error message: " + error);
      }
      else {
        TestUtils.testDone();
      }

      return;
    }

    TestUtils.testFails("Test should fail.");
  }
}

ignitionStartSuccess = function() {
  Ignition.start(['127.0.0.0:9095', '127.0.0.1:9095'], null, onConnect);

  function onConnect(error, server) {
    if (error) {
      TestUtils.testFails(error);

      return;
    }

    TestUtils.testDone();
  }
}

ignitionStartSuccessWithSeveralPorts = function() {
  Ignition.start(['127.0.0.1:9090..9100'], null, onConnect);

  function onConnect(error, ignite) {
    if (error) {
      TestUtils.testFails(error);

      return;
    }

    var server = ignite.server();

    var host = server.host();

    assert.ok(host.indexOf('127.0.0.1') !== -1, "Incorrect host.");

    TestUtils.testDone();
  }
}

ignitionNotStartWithSeveralPorts = function() {
  Ignition.start(['127.0.0.1:9090...9100'], null, onConnect);

  function onConnect(error, ignite) {
    if (error) {
      assert.ok(error.indexOf("Incorrect address format") !== -1, "Incorrect message.")

      TestUtils.testDone();

      return;
    }

    TestUtils.testFails("Exception should be thrown.");
  }
}