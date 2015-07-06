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

var Ignition = Ignite.Ignition;
var Entry = Ignite.Entry;

Ignition.start(['127.0.0.1:9095'], null, onConnect);

function onConnect(error, ignite) {
    if (error) {
        console.log("Error: " + error);

        throw new Error(error);
    }

    var cache = ignite.getOrCreateCache("PutGetExampleCache");

    putGet(cache);

    putAllGetAll(cache);
}

putGet = function(cache) {
    console.log(">>> Cache put-get example started.");

    var keyCnt = 20;

    var putCnt = 0;

    var onGet = function(err, res) {
        if (err) {
            console.log("Error: " + err);

            throw new Error(err);
        }

        console.log("Get val=" + res);
    }

    var onPut = function(err) {
        if (err) {
            console.log("Error: " + err);

            throw new Error(err);
        }

        if (putCnt < keyCnt - 1) {
            putCnt++;

            return;
        }

        console.log(">>> Stored values in cache.");

        for (var i = 0; i < keyCnt; i++) {
            cache.get(i, onGet);
        }
    }

    // Store keys in cache.
    for (var i = 0; i < keyCnt; i++) {
        cache.put(i, i.toString(), onPut);
    }
}

putAllGetAll = function(cache) {
    console.log(">>> Starting putAll-getAll example.");

    var keyCnt = 20;

    var batch = [];
    var keys = [];

    for (var i = keyCnt; i < keyCnt + keyCnt; ++i) {
        var key = i;

        var val = "bulk-" + i;

        keys.push(key);
        batch.push(new Entry(key, val));
    }

    var onGetAll = function(err, entries) {
        if (err) {
            console.log("Error: " + err);

            throw new Error(err);
        }

        for (var e of entries) {
            console.log("Got entry [key=" + e.key + ", val=" + e.value + ']');
        }
    }

    var onPutAll= function(err) {
        if (err) {
            console.log("Error: " + err);

            throw new Error(err);
        }

        cache.getAll(keys, onGetAll);
    }

    cache.putAll(batch, onPutAll);
}