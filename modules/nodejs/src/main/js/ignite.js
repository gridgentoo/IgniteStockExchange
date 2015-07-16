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

var Promise = require("bluebird");
var Cache = require("./cache").Cache;
var Compute = require("./compute").Compute;
var ClusterNode = require("./cluster-node").ClusterNode;
var Command = require("./server").Command;

/**
 * Create an instance of Ignite
 *
 * @constructor
 * @this {Ignite}
 * @param {Server} Server
 */
function Ignite(server) {
    this._server = server;
}

/**
 * @this {Ignite}
 * @returns {Server} Server
 */
Ignite.prototype.server = function() {
    return this._server;
}

/**
 * Get an instance of cache
 *
 * @this {Ignite}
 * @param {string} Cache name
 * @returns {Cache} Cache
 */
Ignite.prototype.cache = function(cacheName) {
    return new Cache(this._server, cacheName);
}

/**
 * Get or create an instance of cache
 *
 * @this {Ignite}
 * @param {string} Cache name
 */
Ignite.prototype.getOrCreateCache = function(cacheName) {
    var server = this._server;
    return new Promise(function(resolve, reject) {
        server.runCommand(new Command("getorcreatecache").addParam("cacheName", cacheName),
            function(err, res) {
                if (err != null) {
                    reject(err);
                }
                else {
                    resolve(new Cache(server, cacheName));
                }
            });
    });
}

/**
 * Stops dynamically started cache
 *
 * @this {Ignite}
 * @param {string} cacheName Cache name to stop
 */
Ignite.prototype.destroyCache = function(cacheName) {
    return this._createPromise(new Command("destroycache").addParam("cacheName", cacheName));
}

/**
 * Get an instance of compute
 *
 * @this {Ignite}
 * @returns {Compute} Compute
 */
Ignite.prototype.compute = function() {
    return new Compute(this._server);
}

/**
 * Ignite version
 *
 * @this {Ignite}
 */
Ignite.prototype.version = function() {
    return this._createPromise(new Command("version"));
}

/**
 * Connected ignite name
 *
 * @this {Ignite}
 */
Ignite.prototype.name = function() {
    return this._createPromise(new Command("name"));
}

/**
 * @this {Ignite}
 */
Ignite.prototype.cluster = function() {
    var cmd = new Command("top").addParam("attr", "true").addParam("mtr", "false");

    var server = this._server;
    return new Promise(function(resolve, reject) {
        server.runCommand(cmd, function(err, res) {
            if (err != null) {
                reject(err);
            }
            else {
                if (!res || res.length == 0) {
                    reject("Empty topology cluster.");
                }
                else {
                    var nodes = [];

                    for (var node of res) {
                        nodes.push(new ClusterNode(node.nodeId, node.attributes));
                    }

                    resolve(nodes);
                }
            }
        });
    });
}

Ignite.prototype._createPromise = function(cmd) {
    var server = this._server;
    return new Promise(function(resolve, reject) {
        server.runCommand(cmd, function(err, res) {
            if (err != null) {
                reject(err);
            }
            else {
                resolve(res);
            }
        });
    });
}

exports.Ignite = Ignite;
