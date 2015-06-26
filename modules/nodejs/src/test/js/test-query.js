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

testSqlQuery = function() {
    function sqlQuery(ignite, error) {
        assert(error == null, "error on sql query [err=" + error + "]");

        var qry = new SqlQuery("select * from String");

        qry.on("error", function(err) {
                console.log("!!!!!!!!!!!!Error: " + err);

                TestUtils.testFails();
            });

        qry.on("end", function(res) {
                console.log("!!!!!!!!!!!Result: " + res);

                TestUtils.testDone();
            });

        ignite.cache("mycache").query(qry);
    }

    function put(error, ignite) {
        assert(error == null, "error on put [err=" + error + "]");

        ignite.cache("mycache").put("key", "val", sqlQuery.bind(null, ignite))
    }

    TestUtils.startIgniteNode(put);
}
