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

testComputeRunScript = function() {
    TestUtils.startIgniteNode(onStart.bind(null, computeRunScript));
}

testComputeExecute = function() {
    TestUtils.startIgniteNode(computeExecute);
}

testComputeAllNodeExecute = function() {
    TestUtils.startIgniteNode(computeAllNodeExecute);
}

function onStart(onPut, error, ignite) {
    var cache = ignite.cache("mycache");

    var params = {}

    for (var i = 900; i < 1000; ++i) {
        params["key" + i] = "val" + i;
    }

    cache.putAll(params, onPut.bind(null, ignite))
}

function computeRunScript(ignite, error) {
    var comp = ignite.compute();

    var f = function (args) {
        return args + " " + ignite.name();
    }

    function onEnd(err, res) {
        assert(err == null);
        assert(res.indexOf("NodeJsComputeSelfTest") !== -1, "Incorrect result message. [mes=" + res + "].");
        assert(res.indexOf("GridGain") !== -1, "Incorrect result message. [mes=" + res + "].");

        TestUtils.testDone();
    }

    comp.runScript(f, "GridGain", onEnd.bind(null));
}

function computeExecute(error, ignite) {
    var map = function(nodes, arg) {
        var words = arg.split(" ");

        for (var i = 0; i < words.length; i++) {
            var f = function (word) {
                println(">>> Printing " + word);

                return word.length;
            };

            emit(f, words[i], nodes[i %  nodes.length]);
        }
    };

    var reduce = function(results) {
        var sum = 0;

        for (var i = 0; i < results.length; ++i) {
            sum += parseInt(results[i], 10);
        }

        return sum;
    };

    var callback = function(err, res) {
        assert(err == null, "Get error on compute task [err=" + err + "]");
        assert.equal(res, 7);

        TestUtils.testDone();
    }

    ignite.compute().execute(map, reduce, "Hi Alice", callback);
}

function computeAllNodeExecute(error, ignite) {
    var map = function(nodes, arg) {
        for (var i = 0; i < nodes.length; i++) {
            var f = function (node) {
                println(">>> Printing " + node.id().toString());

                return "";
            };

            emit(f, nodes[i %  nodes.length], nodes[i %  nodes.length]);
        }
    };

    var reduce = function(results) {};

    var callback = function(err, res) {
        assert(err == null, "Get error on compute task [err=" + err + "]");
        TestUtils.testDone();
    }

    ignite.compute().execute(map, reduce, "", callback);
}

testComputeFuncWithErrorExecute = function() {
    var map = function(nodes, arg) {
        var f = function(args){throw "Bad function";};

        for (var i = 0; i < nodes.length; i++) {
            emit(f, "", nodes[i %  nodes.length]);
        }
    };

    testComputeWithErrors(map);
}

testComputeIncorrectFuncExecute = function() {
    var map = function(nodes, arg) {
        var f = function() {
            prin("hi");
        };

        for (var i = 0; i < nodes.length; i++) {
            emit(f, "", nodes[i %  nodes.length]);
        }
    };

    testComputeWithErrors(map);
}

testComputeIncorrectMapExecute = function() {
    var map = function(nodes, arg) {
        var f = function() {
            print("hi");
        };

        for (i = 0; i < nodes.length; i++) {
            emit(f, "", nodes[a %  nodes.length]);
        }
    };

    testComputeWithErrors(map);
}

function testComputeWithErrors(map) {
    function computeErrorExecute(error, ignite) {
        var callback = function(err, res) {
            assert(err != null, "Do not get error on compute task.");

            assert(err.indexOf("Function evaluation failed") > -1, "Incorrect error "+
                "[expected=function evaluation failed, value=" + err + "]");

            TestUtils.testDone();
        }

        ignite.compute().execute(map, function (args) {}, "Hi Alice", callback);
    }

    TestUtils.startIgniteNode(computeErrorExecute);
}
