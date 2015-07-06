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

var Ignite = require("../../");
var assert = require("assert");

var Ignition = Ignite.Ignition;
var SqlQuery = Ignite.SqlQuery;
var SqlFieldsQuery = Ignite.SqlFieldsQuery;
var Entry = Ignite.Entry;

var cacheName = "CacheQueryExample";

Ignition.start(['127.0.0.1:9095'], null, onConnect);

function onConnect(err, ignite) {
    assert(err === null, err);

    console.log(">>> Cache query example started.");

    var entries = [new Entry("key0", "val0"), new Entry("key1", "val1")];

    ignite.getOrCreateCache(cacheName).putAll(entries, onCachePut.bind(null, ignite));
}

function onCachePut(ignite, err) {
    assert(err == null, err);

    var qry = new SqlQuery("Select * from String");
    qry.setReturnType("String");

     var fullRes = [];

    qry.on("page", function(res) {
        fullRes = fullRes.concat(res);
    });

    qry.on("end", function(err) {
        assert(err == null, err);

        console.log(fullRes);
    });

    ignite.cache(cacheName).query(qry);
}