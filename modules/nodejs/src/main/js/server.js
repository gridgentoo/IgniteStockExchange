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

/**
 * Creates an instance of Server
 *
 * @constructor
 * @this {Server}
 * @param {string} host Host address
 * @param {number} port Port
 * @param {string} secretKey Secret key for connection
 */
function Server(host, port, secretKey) {
  this._host = host;
  this._port = port;
  this._secretKey = secretKey;
}

/**
 * Host value
 *
 * @this {Server}
 * @returns {string} Host value
 */
Server.prototype.host = function() {
  return this._host;
}

/**
 * Callback for Server runCommand
 *
 * @callback onGet
 * @param {string} error Error
 * @param {string} result Result value
 */

/**
 * Run http request
 *
 * @this {Server}
 * @param {string} cmdName command name.
 * @param params Parameters for command.
 * @param {onGet} Called on finish
 */
Server.prototype.runCommand = function(cmdName, params, callback) {
  var paramsString = "";

  for (var p of params) {
    paramsString += "&" + p.key + "=" + p.value;
  }

  var requestQry = "cmd=" + cmdName + paramsString;

  var http = require('http');

  var options = {
    host: this._host,
    port: this._port,
    path: "/ignite?" + requestQry,
    headers: this._signature()
  };

  function streamCallback(response) {
    var fullResponseString = '';

    response.on('data', function (chunk) {
      fullResponseString += chunk;
    });

    response.on('end', function () {
      if (response.statusCode !== 200) {
        if (response.statusCode === 401) {
          callback.call(null, "Authentication failed. Status code 401.");
        }
        else {
          callback.call(null, "Request failed. Status code " + response.statusCode);
        }

        return;
      }

      var igniteResponse;

      try {
        igniteResponse = JSON.parse(fullResponseString);
      }
      catch (e) {
        callback.call(null, e, null);
        return;
      }

      if (igniteResponse.successStatus) {
        callback.call(null, igniteResponse.error, null)
      }
      else {
        callback.call(null, null, igniteResponse.response);
      }
    });
  }

  console.log("!!!!!!!!!!!!!!!!!REQUEST " + options.path);
  var request = http.request(options, streamCallback);

  request.setTimeout(5000, callback.bind(null, "Request timeout: >5 sec"));

  request.on('error', callback);

  request.end();
}

/**
 * Check the connection with server node.
 *
 * @this {Server}
 * @param {onGet} callback Called on finish
 */
Server.prototype.checkConnection = function(callback) {
  this.runCommand("version", [], callback);
}

/**
 * Returns pair for runCommand
 *
 * @param {string} key Key
 * @param {string} value Value
 * @returns Pair of strings
 */
Server.pair = function(key, value) {
  return {key: key, value: value}
}

/**
 * Get signature for connection.
 *
 * @this {Server}
 * @returns Signature
 */
Server.prototype._signature = function() {
  if (!this._secretKey) {
    return "";
  }

  var loadTimeInMS = Date.now();

  var baseKey = '' + loadTimeInMS + ":" + this._secretKey;

  var crypto = require('crypto')

  var shasum = crypto.createHash('sha1');

  shasum.update(baseKey, 'binary');

  var hash = shasum.digest('base64');

  var key = loadTimeInMS + ":" + hash;

  return {"X-Signature" : key};
}

exports.Server = Server;