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

var Cache = require("./cache").Cache;
var Compute = require("./compute").Compute;
var ClusterNode = require("./cluster-node").ClusterNode;
var Server = require("./server").Server;
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
 * @param callback Callback with cache.
 */
Ignite.prototype.getOrCreateCache = function(cacheName, callback) {
    var onCreateCallback = function(callback, err, res) {
        if (err !== null) {
            callback.call(null, err, null);

            return;
        }

        callback.call(null, null, new Cache(this._server, cacheName))
    }

    this._server.runCommand(new Command("getorcreatecache").addParam("cacheName", cacheName),
        onCreateCallback.bind(this, callback));
}

/**
 * Stops dynamically started cache
 *
 * @this {Ignite}
 * @param {string} cacheName Cache name to stop
 * @param {noValue} callback Callback contains only error
 */
Ignite.prototype.destroyCache = function(cacheName, callback) {
    this._server.runCommand(new Command("destroycache").addParam("cacheName", cacheName), callback);
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
 * @param {onGet} callback Result in callback contains string with Ignite version.
 */
Ignite.prototype.version = function(callback) {
    this._server.runCommand(new Command("version"), callback);
}

/**
 * Connected ignite name
 *
 * @this {Ignite}
 * @param {onGet} callback Result in callback contains string with Ignite name.
 */
Ignite.prototype.name = function(callback) {
    this._server.runCommand(new Command("name"), callback);
}

/**
 * @this {Ignite}
 * @param {onGet} callback Result in callback contains list of ClusterNodes
 */
Ignite.prototype.cluster = function(callback) {
    function onTop(callback, err, res) {
        if (err) {
            callback.call(null, err, null);

            return;
        }

        if (!res || res.length == 0) {
            callback.call(null, "Empty topology cluster.", null);

            return;
        }

        var nodes = [];

        for (var node of res) {
            nodes.push(new ClusterNode(node.nodeId, node.attributes));
        }

        callback.call(null, null, nodes);
    }

    this._server.runCommand(new Command("top").addParam("attr", "true").addParam("mtr", "false"),
        onTop.bind(null, callback));
}

exports.Ignite = Ignite;
