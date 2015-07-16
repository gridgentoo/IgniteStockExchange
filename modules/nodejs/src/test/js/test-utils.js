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

var assert = require("assert");

/**
 * Create instance of TestUtils
 *
 * @constructor
 */
function TestUtils() {
}

/**
 * @returns {string} Path to script dir
 */
TestUtils.scriptPath = function() {
    return TestUtils.igniteHome() +
        TestUtils.sep() + "modules" +
        TestUtils.sep() + "nodejs" +
        TestUtils.sep() + "src" +
        TestUtils.sep() + "main" +
        TestUtils.sep() + "js" + TestUtils.sep();
}

/**
 * @returns {string} Ignite home path
 */
TestUtils.igniteHome = function() {
    return process.env.IGNITE_HOME;
}

/**
 * @returns {string} Path separator
 */
TestUtils.sep = function() {
    return require('path').sep;
}

TestUtils.compareObject = function(o1, o2) {
    if (typeof o1 !== 'object') {
        return o1 === o2;
    }
    else {
        if (Object.keys(o1).length !== Object.keys(o2).length)
            return false;

        for (var keyObj of Object.keys(o2)) {
            if (!TestUtils.compareObject(o1[keyObj], o2[keyObj])) {
                return false;
            }
        }
    }

    return true;
}

/**
 * @param {string} dir Directory with all ignite libs
 * @returns {string} Classpath for ignite node start
 */
TestUtils.classpath = function(dir) {
    var fs = require('fs');
    var path = require('path');
    function walk(dir, done) {
        var results = [];
        var list = fs.readdirSync(dir)

        for (var i = 0; i < list.length; ++i) {
            file = path.resolve(dir, list[i]);

            var stat = fs.statSync(file);

            if (stat && stat.isDirectory()) {
                if (list[i] != "optional" && file.indexOf("optional") !== -1 && file.indexOf("rest") == -1 )
                    continue;

                var sublist = walk(file);
                results = results.concat(sublist);
            }
            else {
                if (file.indexOf(".jar") !== -1) {
                    results.push(file);
                }
            }
        }
        return results;
    };

    return walk(dir);
};

/**
 * @returns Process that starts ignite node
 */
TestUtils.startIgniteNode = function() {
    var libs = classpath(igniteHome() +  TestUtils.sep() + "target" +
        TestUtils.sep() + "bin" +
        TestUtils.sep() + "apache-ignite-fabric-1.1.1-SNAPSHOT-bin" +
        TestUtils.sep() + "libs");

    var cp = libs.join(require('path').delimiter);

    var spawn = require('child_process').spawn;

    var child = spawn('java',['-classpath', cp, 'org.apache.ignite.startup.cmdline.CommandLineStartup',
        "test-node.xml"]);

    child.stdout.on('data', function (data) {
        console.log("" + data);
    });

    child.stderr.on('data', function (data) {
        console.log("" + data);
    });

    return child;
}

/**
 * Print error to console
 *
 * @param {string} error Error
 */
TestUtils.testFails = function(error) {
    console.log("Node JS test failed: " + error);
}

/**
 * Print ok message to console
 */
TestUtils.testDone = function() {
    console.log("Node JS test finished.")
}

/**
 * Starts ignite node with default config
 */
TestUtils.startIgniteNode = function() {
    var Ignite = require(TestUtils.scriptPath());
    var Ignition = Ignite.Ignition;

    return Ignition.start(['127.0.0.1:9095'], null);
}

/**
 * Starts ignite node with default config
 *
 * @param {string} secretKey Secret key
 */
TestUtils.startIgniteNodeWithKey = function(secretKey) {
    var Ignite = require(TestUtils.scriptPath());
    var Ignition = Ignite.Ignition;

    return Ignition.start(['127.0.0.1:9095'], secretKey);
}

exports.TestUtils = TestUtils;
