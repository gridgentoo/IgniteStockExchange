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
var ComputeTask = require("./compute-task").ComputeTask;

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
  this._server.runCommand("affrun", [Server.pair("cacheName", cacheName),
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
  this._server.runCommand("affcall", [Server.pair("cacheName", cacheName),
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
Compute.prototype.execute = function(task, arg, callback) {
  this._nodes(this._onNodesExecute.bind(this, task, arg, callback));
}

Compute.prototype._nodes = function(callback) {
  this._server.runCommand("top", [Server.pair("mtr", "false"), Server.pair("attr", "false")],
    this._onNodes.bind(this, callback))
}

Compute.prototype._onNodes = function(callback, error, results) {
  if (error) {
    callback.call(null, error, null);

    return;
  }

  var nodes = [];

  for (var res of results) {
    nodes.push(res["nodeId"])
  }

  callback.call(null, null, nodes);
}

Compute.prototype._onNodesExecute = function(task, arg, callback, err, nodes) {
  if (err) {
      callback.call(null, error, null);

      return;
  }

  var computeJobList = task.map(nodes, arg);

  var params = [];
  var i = 1;

  console.log("TASK" + computeJobList);
  for (var job of computeJobList) {
    params.push(Server.pair("f" + i, this._escape(job.func)));
    params.push(Server.pair("args" + i,  JSON.stringify(job.args)));
    params.push(Server.pair("n" + i, job.node));
    i++;
  }

  this._server.runCommand("exectask", params, this._onResExecute.bind(this, task, callback));
}


Compute.prototype._onResExecute = function(task, callback, err, results) {
  if (err) {
    callback.call(null, err, null);

    return;
  }

  console.log("ON RES EXEC = " + results);

  var res = task.reduce(results);

  callback.call(null, null, res);
}

exports.Compute = Compute


function ComputeJob(func, args, node) {
    this.func = func;
    this.args = args;
    this.node = node;
}

exports.ComputeJob = ComputeJob;