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
var Server = require("./server").Server;
var Ignite = require("./ignite").Ignite
/**
 * Creates an instance of Ignition
 *
 * @constructor
 */
function Ignition() {
}

/**
 * Callback for Ignition start
 *
 * @callback Ignition~onStart
 * @param {string} error Error
 * @param {Ignite} ignite Connected ignite
 */

/**
 * Open connection with ignite node
 *
 * @param {string[]} address List of nodes hosts with ports
 * @param {string} secretKey Secret key.
 */
Ignition.start = function(address, secretKey) {
    return new Promise(function(resolve, reject) {
        var numConn = 0;

        var needVal = true;

        for (var addr of address) {
            var params = addr.split(":");

            var portsRange = params[1].split("..");

            var start;
            var end;

            if (portsRange.length === 1) {
                start = parseInt(portsRange[0], 10);
                end = start;
            }
            else if (portsRange.length === 2) {
                start = parseInt(portsRange[0], 10);
                end = parseInt(portsRange[1], 10);
            }

            if (isNaN(start) || isNaN(end)) {
                needVal = false;

                reject("Incorrect address format.");
            }
            else {
                for (var i = start; i <= end; i++) {
                    checkServer(params[0], i, secretKey);
                }
            }
        }

        function checkServer(host, port, secretKey) {
            numConn++;

            var server = new Server(host, port, secretKey);

            server.checkConnection(onConnect.bind(this, server));
        }

        function onConnect(server, error) {
            if (!needVal) return;

            numConn--;

            if (!error) {
                resolve(new Ignite(server));

                needVal = false;

                return;
            }

            if (!numConn) {
                reject("Cannot connect to servers. " + error);

                return;
            }
        }
    });
}

exports.Ignition = Ignition;