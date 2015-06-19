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
 * @param {Compute~runnable} runnable Function without parameters
 */
Compute.prototype.affinityRun = function(runnable, callback) {
  var f = runnable.toString();
  var qs = require('querystring');
  f = qs.escape(f);
  this._server.runCommand("affrun", [Server.pair("func", f)], callback);
}

exports.Compute = Compute;