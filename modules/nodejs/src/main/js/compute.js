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

/**
 * @constructor
 * @this {Compute}
 * @param {Server} server Server class
 */
function Compute(server) {
  this._server = server;
}

/**
 * Callback for affinityRun
 * @callback Compute~runnable
 */

/**
 * @this {Compute}
 * @param {string} cacheName Cache name.
 * @param {string} key Key.
 * @param {Compute~runnable} runnable Function without parameters
 * @param {Cache~noValue} callback Callback
 */
Compute.prototype.affinityRun = function(cacheName, key, runnable, callback) {
  this._server.runCommand("affscriptrun", [Server.pair("cacheName", cacheName),
    Server.pair("key", key), Server.pair("func", this._escape(runnable))], callback);
}

/**
 * @this {Compute}
 * @param {string} cacheName Cache name.
 * @param {string} key Key.
 * @param {Compute~runnable} runnable Function without parameters
 * @param {Cache~onGet} callback Callback
 */
Compute.prototype.affinityCall = function(cacheName, key, runnable, callback) {
  this._server.runCommand("affscriptcall", [Server.pair("cacheName", cacheName),
    Server.pair("key", key), Server.pair("func", this._escape(runnable))], callback);
}

/**
 * @param{Cache~noValue} f Function
 * @returns {string} Encoding function
 */
Compute.prototype._escape = function(f) {
  var f = f.toString();
  var qs = require('querystring');
  return qs.escape(f);
}

/**
 * @this {Compute}
 * @param {ComputeTask} task Compute task
 * @param {string} arg  Argument
 * @param {} callback Callback
 */
Compute.prototype.execute = function(map, reduce, arg, callback) {
   var params = [];

    params.push(Server.pair("map", this._escape(map)));
    params.push(Server.pair("reduce", this._escape(reduce)));
    params.push(Server.pair("arg", this._escape(arg)));

    this._server.runCommand("execscripttask", params, callback);
}

/**
 * @name EmitFunction
 * @function
 * @param {function} func Remote job
 * @param {string[]} args Arguments for remote job
 * @param {string} node Node Id to call job on.
 */

/**
 * @name MapFunction
 * @function
 * @param {string[]} nodes Nodes Id
 * @param {string} arg Argument
 * @param {EmitFunction} emit Emit function to call for adding to result
 */

/**
 * @name ReduceFunction
 * @function
 * @param {string[]} results Results of executing jobs after mapping
 * @returns {string} Result
 */

exports.Compute = Compute
