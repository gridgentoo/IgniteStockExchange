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

Ignition.start(['127.0.0.1:9095'], null, onConnect);

function onConnect(error, ignite) {
    if (error) {
        console.log("Error: " + error);

        throw new Error(error);
    }

    var cache = ignite.getOrCreateCache("mycache");

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