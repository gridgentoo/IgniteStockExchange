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
    TestUtils.startIgniteNode().then(function(ignite) {
        ignite.cache("mycache").put("key0", "val0").then(function() {
            var qry = new SqlQuery("select * from String");

            qry.setReturnType("String");

            var fullRes = [];

            function onQuery(cursor) {
                var page = cursor.page();

                fullRes = fullRes.concat(page);

                if (cursor.isFinished()) {
                    assert(fullRes.length === 1, "Result length is not correct" +
                        "[expected=1, val = " + fullRes.length + "]");

                    assert(fullRes[0]["key"] === "key0", "Result value for key is not correct "+
                        "[expected=key0, real=" + fullRes[0]["key"] + "]");

                    assert(fullRes[0]["value"] === "val0", "Result value for key is not correct "+
                        "[expected=val0, real=" + fullRes[0]["value"] + "]");

                    TestUtils.testDone();

                    return;
                }

                cursor.nextPage().then(onQuery);
            }

            var cursor = ignite.cache("mycache").query(qry);

            cursor.nextPage().then(onQuery);
        }).catch(function(err) {
            assert(err === null, err);
        })
    }).catch(function(err) {
        assert(err === null, err);
    });
}

testSqlFieldsQuery = function() {
    TestUtils.startIgniteNode().then(function(ignite) {
        var qry = new SqlFieldsQuery("select concat(firstName, ' ', lastName) from Person");

        var fullRes = [];

        function onQuery(cursor) {
            var page = cursor.page();

            fullRes = fullRes.concat(page);

            if (cursor.isFinished()) {
                assert(fullRes.length === 4, "Result length is not correct" +
                    "[expected=1, val = " + fullRes.length + "]");

                fullRes.sort();

                assert(fullRes[0].indexOf("Jane Doe") > -1,
                    "Result does not contain Jane Doe [res=" + fullRes[0] + "]");

                console.log("Result: " + JSON.stringify(fullRes));

                return ignite.cache("person").get("key");
            }

            return cursor.nextPage().then(onQuery);
        }

        ignite.cache("person").query(qry).nextPage().then(onQuery).then(function(){
            TestUtils.testDone();
        })
    }).catch(function(err) {
        assert(err === null, err);
    });
}

testCloseQuery = function() {
    TestUtils.startIgniteNode().then(function(ignite) {
        var qry = new SqlFieldsQuery("select concat(firstName, ' ', lastName) from Person");

        function onQuery(cursor) {
            return cursor.close();
        }

        ignite.cache("person").query(qry).nextPage().then(onQuery).then(function(res){
            TestUtils.testDone();
        }).catch(function(err){
            assert(err === null, err);
        })
    }).catch(function(err) {
        assert(err === null, err);
    });
}

testSqlFieldsGetAllQuery = function() {
    TestUtils.startIgniteNode().then(function(ignite) {
        var qry = new SqlFieldsQuery("select concat(firstName, ' ', lastName) from Person");

        var cursor = ignite.cache("person").query(qry);

        function onQuery(fullRes) {
            assert(fullRes.length === 4, "Result length is not correct" +
                "[expected=1, val = " + fullRes.length + "]");

            fullRes.sort();

            assert(fullRes[0].indexOf("Jane Doe") > -1,
                "Result does not contain Jane Doe [res=" + fullRes[0] + "]");

            console.log("Result: " + JSON.stringify(fullRes));

            var meta = cursor.fieldsMetadata();

            assert(meta[0]["fieldName"] !== null, "Incorrect fields meta.")

            return ignite.cache("person").get("key");
        }

        cursor.getAll().then(onQuery).then(function(){
            TestUtils.testDone();
        })
    }).catch(function(err) {
        assert(err === null, err);
    });
}

testSqlFieldsMeta = function() {
    TestUtils.startIgniteNode().then(function(ignite) {
        var qry = new SqlFieldsQuery("select firstName, lastName from Person");

        var fullRes = [];

        function onQuery(cursor) {
            var page = cursor.page();

            fullRes = fullRes.concat(page);

            var meta = cursor.fieldsMetadata();

            console.log("Fields meta: " + JSON.stringify(meta))
            assert(meta.length === 2, "Incorrect fields meta length [exp=2, val=" + meta.length + "]");

            assert(meta[0]["fieldName"] === "FIRSTNAME", "Incorrect fields meta " +
                "[exp=FIRSTNAME, val=" + meta[0]["fieldName"] + "]");
            assert(meta[0]["fieldTypeName"] === "java.lang.String", "Incorrect fields meta " +
                "[exp=java.lang.String, val=" + meta[0]["fieldTypeName"] + "]");
            assert(meta[0]["schemaName"] === "person", "Incorrect fields meta " +
                "[exp=person, val=" + meta[0]["schemaName"] + "]");
            assert(meta[0]["typeName"] === "PERSON", "Incorrect fields meta " +
                "[exp=PERSON, val=" + meta[0]["typeName"] + "]");

            if (cursor.isFinished()) {
                assert(fullRes.length === 4, "Result length is not correct" +
                    "[expected=1, val = " + fullRes.length + "]");

                console.log("Result: " + JSON.stringify(fullRes));

                TestUtils.testDone();

                return;
            }

            cursor.nextPage().then(onQuery);
        }

        ignite.cache("person").query(qry).nextPage().then(onQuery);
    }).catch(function(err) {
        assert(err === null, err);
    });
}

testSqlQueryWithParams = function() {
    TestUtils.startIgniteNode().then(function(ignite) {
        var qry = new SqlQuery("salary > ? and salary <= ?");

        qry.setReturnType("Person");

        qry.setArguments([1000, 2000]);

        var fullRes = [];

        function onQuery(cursor) {
            var page = cursor.page();

            fullRes = fullRes.concat(page);

            if (cursor.isFinished()) {
                assert(fullRes.length === 2, "Result length is not correct" +
                    "[expected=1, val = " + fullRes.length + "]");

                assert(((fullRes[0]["value"]["firstName"].indexOf("Jane") > -1) ||
                    (fullRes[0]["value"]["firstName"].indexOf("John") > -1)),
                    "Result does not contain Jane and John [res=" + fullRes[0]["value"]["firstName"] + "]");

                console.log("Result: " + JSON.stringify(fullRes));

                TestUtils.testDone();

                return;
            }

            cursor.nextPage().then(onQuery);
        }

        ignite.cache("person").query(qry).nextPage().then(onQuery);
    }).catch(function(err) {
        assert(err === null, err);
    });
}