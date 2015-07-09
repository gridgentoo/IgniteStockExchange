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
var Command = require("./server").Command;

/**
 * @constructor
 * @this {Compute}
 * @param {Server} server Server
 */
function Compute(server) {
    this._server = server;
}

/**
 * @this {Compute}
 * @param job Function
 * @param args Function arguments
 * @param {onGet} callback Callback
 */
Compute.prototype.run = function(job, args, callback) {
    this._server.runCommand(new Command("runscript").addParam("func", job).
    setPostData(JSON.stringify({"arg" : args})), callback);
}

/**
 * @this {Compute}
 * @param {MapFunction} map Map function
 * @param {ReduceFunction} reduce Reduce function
 * @param {string} arg Argument
 * @param {onGet} callback Callback
 */
Compute.prototype.mapReduce = function(map, reduce, arg, callback) {
    var command = new Command("excmapreduce");

    command.addParam("map", map).addParam("reduce", reduce);
    command.setPostData(JSON.stringify({"arg" : arg}));

    this._server.runCommand(command, callback);
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
