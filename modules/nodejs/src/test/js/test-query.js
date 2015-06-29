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

var Ignite = require(TestUtils.scriptPath());
var SqlQuery = Ignite.SqlQuery;
var SqlFieldsQuery = Ignite.SqlFieldsQuery;

testSqlQuery = function() {
    function sqlQuery(ignite, error) {
        assert(error == null, "error on sql query [err=" + error + "]");

        var qry = new SqlQuery("select * from String");

        qry.setReturnType("String");

        var fullRes = [];

        qry.on("error", function(err) {
                TestUtils.testFails();
            });

        qry.on("page", function(res) {
            fullRes = fullRes.concat(res);
        });

        qry.on("end", function() {
                assert(fullRes.length, 1, "Result length is not correct" +
                    "[expected=1, val = " + fullRes.length + "]");

                assert(fullRes[0]["key"] === "key0", "Result value for key is not correct "+
                    "[expected=key0, real=" + fullRes[0]["key"] + "]");

                assert(fullRes[0]["value"] === "val0", "Result value for key is not correct "+
                    "[expected=val0, real=" + fullRes[0]["value"] + "]");

                TestUtils.testDone();
            });

        ignite.cache("mycache").query(qry);
    }

    function put(error, ignite) {
        assert(error == null, "error on put [err=" + error + "]");

        ignite.cache("mycache").put("key0", "val0", sqlQuery.bind(null, ignite))
    }

    TestUtils.startIgniteNode(put);
}

testSqlFieldsQuery = function() {
    function sqlFieldsQuery(error, ignite) {
        assert(error == null, "error on sqlfields query [err=" + error + "]");

        var qry = new SqlFieldsQuery("select concat(firstName, ' ', lastName) from Person");

        var fullRes = [];

        qry.on("error", function(err) {
                TestUtils.testFails();
            });

        qry.on("page", function(res) {
            console.log("PAGE   : " + res);
            fullRes = fullRes.concat(res);
        });

        qry.on("end", function() {
                console.log("END=" + fullRes);

                assert(fullRes.length, 1, "Result length is not correct" +
                    "[expected=1, val = " + fullRes.length + "]");

                assert(fullRes[0]["key"] === "key0", "Result value for key is not correct "+
                    "[expected=key0, real=" + fullRes[0]["key"] + "]");

                assert(fullRes[0]["value"] === "val0", "Result value for key is not correct "+
                    "[expected=val0, real=" + fullRes[0]["value"] + "]");

                TestUtils.testDone();
            });

        ignite.cache("person").query(qry);
    }

    TestUtils.startIgniteNode(sqlFieldsQuery.bind(null));
}
