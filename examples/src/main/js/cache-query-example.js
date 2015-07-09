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

var apacheIgnite = require("apache-ignite");

var Ignition = apacheIgnite.Ignition;
var SqlQuery = apacheIgnite.SqlQuery;
var SqlFieldsQuery = apacheIgnite.SqlFieldsQuery;
var CacheEntry = apacheIgnite.CacheEntry;

var cacheName = "CacheQueryExample";

Ignition.start(['127.0.0.1:9095'], null, onConnect);

function onConnect(err, ignite) {
    console.log(">>> Cache query example started.");

    var entries = [new CacheEntry("key0", "val0"), new CacheEntry("key1", "val1")];

    ignite.getOrCreateCache(cacheName, function(err, cache) {
            cache.putAll(entries, onCachePut.bind(null, ignite));
        });
}

function onCachePut(ignite, err) {
    var qry = new SqlQuery("Select * from String");
    qry.setReturnType("String");

     var fullRes = [];

    qry.on("page", function(res) {
        fullRes = fullRes.concat(res);
    });

    qry.on("end", function(err) {
        console.log(fullRes);
    });

    ignite.cache(cacheName).query(qry);
}