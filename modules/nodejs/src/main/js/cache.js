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
var Server = require("./server").Server;
var Command = require("./server").Command;
var SqlFieldsQuery = require("./sql-fields-query").SqlFieldsQuery
var SqlQuery = require("./sql-query").SqlQuery

/**
 * Creates an instance of Cache
 *
 * @constructor
 * @this {Cache}
 * @param {Server} server Server class
 * @param {string} cacheName Cache name
 */
function Cache(server, cacheName) {
    this._server = server;
    this._cacheName = cacheName;
}

/**
 * Get cache name.
 *
 * @this{Cache}
 * @returns {string} Cache name.
 */
Cache.prototype.name = function() {
    return this._cacheName;
}

/**
 * Get cache value
 *
 * @this {Cache}
 * @param {string} key Key
 */
Cache.prototype.get = function(key) {
    return this.__createPromise(this._createCommand("get").
        setPostData(JSON.stringify({"key": key})));
};

/**
 * Put cache value
 *
 * @this {Cache}
 * @param {string} key Key
 * @param {string} value Value
 */
Cache.prototype.put = function(key, value) {
    return this.__createPromise(this._createCommand("put").
        setPostData(JSON.stringify({"key": key, "val" : value})));
}

/**
 * Put if absent
 *
 * @this {Cache}
 * @param {string} key Key
 * @param {string} value Value
 */
Cache.prototype.putIfAbsent = function(key, value) {
    return this.__createPromise(this._createCommand("putifabs").
        setPostData(JSON.stringify({"key": key, "val" : value})));
}

/**
 * Remove cache key
 *
 * @this {Cache}
 * @param key Key
 */
Cache.prototype.remove = function(key, callback) {
    return this.__createPromise(this._createCommand("rmv").
        setPostData(JSON.stringify({"key": key})));
}

/**
 * Remove cache key
 *
 * @this {Cache}
 * @param key Key
 * @param value Value
 */
Cache.prototype.removeValue = function(key, value, callback) {
    return this.__createPromise(this._createCommand("rmvval").
        setPostData(JSON.stringify({"key": key, "val" : value})));
}

/**
 * Get and remove cache key
 *
 * @this {Cache}
 * @param {string} key Key
 */
Cache.prototype.getAndRemove = function(key, callback) {
    return this.__createPromise(this._createCommand("getrmv").
        setPostData(JSON.stringify({"key": key})));
}

/**
 * Remove cache keys
 *
 * @this {Cache}
 * @param {string[]} keys Keys to remove
 */
Cache.prototype.removeAll = function(keys, callback) {
    return this.__createPromise(this._createCommand("rmvall").
        setPostData(JSON.stringify({"keys" : keys})));
}

/**
 * Remove all cache keys
 *
 * @this {Cache}
 */
Cache.prototype.removeAllFromCache = function(callback) {
    return this.__createPromise(this._createCommand("rmvall"));
}

/**
 * Put keys to cache
 *
 * @this {Cache}
 * @param {CacheEntry[]} List of entries to put in the cache
 */
Cache.prototype.putAll = function(entries) {
    return this.__createPromise(this._createCommand("putall").setPostData(
        JSON.stringify({"entries" : entries})));
}

/**
 * Get keys from the cache
 *
 * @this {Cache}
 * @param {Object[]} keys Keys
 */
Cache.prototype.getAll = function(keys, callback) {
    var cmd = this._createCommand("getall").setPostData(JSON.stringify({"keys" : keys}));

    var server = this._server;
    return new Promise(function(resolve, reject) {
        server.runCommand(cmd, function(err, res) {
            if(err != null) {
                reject(err);
            }
            else {
                var result = [];

                for (var key of res) {
                    result.push(new CacheEntry(key["key"], key["value"]));
                }

                resolve(result);
            }
        });
    });
}

/**
 * Determines if the cache contains an entry for the specified key.
 *
 * @this {Cache}
 * @param {Object} key Key
 */
Cache.prototype.containsKey = function(key) {
    return this.__createPromise(this._createCommand("conkey").
        setPostData(JSON.stringify({"key" : key})));
}

/**
 * Determines if the cache contains all keys.
 *
 * @this {Cache}
 * @param {Object[]} keys Keys
 */
Cache.prototype.containsKeys = function(keys, callback) {
    return this.__createPromise(this._createCommand("conkeys").
        setPostData(JSON.stringify({"keys" : keys})));
}

/**
 * Get and put cache value
 *
 * @this {Cache}
 * @param {string} key Key
 * @param {string} value Value
 */
Cache.prototype.getAndPut = function(key, val) {
    return this.__createPromise(this._createCommand("getput").
        setPostData(JSON.stringify({"key" : key, "val" : val})));
}

/**
 * replace cache value
 *
 * @this {Cache}
 * @param key Key
 * @param value Value
 */
Cache.prototype.replace = function(key, val, callback) {
    return this.__createPromise(this._createCommand("rep").
        setPostData(JSON.stringify({"key" : key, "val" : val})));
}

/**
 * replace cache value
 *
 * @this {Cache}
 * @param key Key
 * @param value Value
 * @param oldVal Old value
 */
Cache.prototype.replaceValue = function(key, val, oldVal) {
    return this.__createPromise(this._createCommand("repval").
        setPostData(JSON.stringify({"key" : key, "val" : val, "oldVal" : oldVal})));
}

/**
 * Get and put cache value
 *
 * @this {Cache}
 * @param {string} key Key
 * @param {string} value Value
 */
Cache.prototype.getAndReplace = function(key, val) {
    return this.__createPromise(this._createCommand("getrep").
        setPostData(JSON.stringify({"key" : key, "val" : val})));
}

/**
 * Stores given key-value pair in cache only if cache had no previous mapping for it.
 *
 * @this {Cache}
 * @param {string} key Key
 * @param {string} value Value
 */
Cache.prototype.getAndPutIfAbsent = function(key, val) {
    return this.__createPromise(this._createCommand("getputifabs").
        setPostData(JSON.stringify({"key" : key, "val" : val})));
}

/**
 * @this {Cache}
 */
Cache.prototype.size = function(callback) {
    return this.__createPromise(this._createCommand("size"));
}

/**
 * Execute sql query
 *
 * @param {SqlQuery|SqlFieldsQuery} qry Query
 * @returns {QueryCursor} Cursor for current query.
 */
Cache.prototype.query = function(qry) {
    return new QueryCursor(this, qry, true, null, null);
}

Cache.prototype.__createPromise = function(cmd) {
    var server = this._server;

    return new Promise(function(resolve, reject) {
        server.runCommand(cmd, function(err, res) {
            if(err != null) {
                reject(err);
            }
            else {
                resolve(res);
            }
        });
    });
}

Cache.prototype._createCommand = function(name) {
    var command = new Command(name);

    return command.addParam("cacheName", this._cacheName);
}

/**
 * Creates an instance of QueryCursor
 *
 * @constructor
 * @this {QueryCursor}
 * @param {Cache} cache Cache that runs query
 * @param {SqlQuery|SqlFieldsQuery} qry Sql query
 * @param {boolean} init True if query is not started
 * @param {Object[]} res Current page result
 * @param fieldsMeta Fields metadata.
 */
QueryCursor = function(cache, qry, init, res, fieldsMeta) {
    this._qry = qry;
    this._cache = cache;
    this._init = init;
    this._res = res;
    this._fieldsMeta = fieldsMeta;
}

/**
 * Gets Promise with all query results.
 * Use this method when you know in advance that query result is
 * relatively small and will not cause memory utilization issues.
 * <p>
 * Since all the results will be fetched, all the resources will be closed
 * automatically after this call, e.g. there is no need to call close() method in this case.
 *
 * @this{QueryCursor}
 * @returns {Promise} Promise with query result
 */
QueryCursor.prototype.getAll = function() {
    if (!this._init) {
        return new Promise(function(resolve, reject){
            reject("GetAll is called after nextPage.");
        });
    }

    var cmd = this._getQueryCommand();
    var server = this._cache._server;
    var cursor = this;

    return new Promise(function(resolve, reject) {
        var fullRes = [];

        onResult = function (err, res){
            if (err !== null) {
                reject(err);
            }
            else {
                cursor._res = res;

                if (cursor._fieldsMeta === null) {
                    cursor._fieldsMeta = res["fieldsMetadata"];
                }

                fullRes = fullRes.concat(res["items"]);

                if (res["last"]) {
                    resolve(fullRes);
                }
                else {
                    server.runCommand(cursor._getQueryCommand(), onResult);
                }
            }
        }

        server.runCommand(cmd, onResult);
    });
}

/**
 * Gets Promise with Cursor on next page of the query results.
 *
 * @this{QueryCursor}
 * @returns {Promise} Promise with Cursor on next page
 */
QueryCursor.prototype.nextPage = function() {
    if (this._res !== null && this._res["last"]) {
        throw "All pages are returned.";
    }

    var cmd = this._getQueryCommand();
    var server = this._cache._server;
    var qry = this._qry;
    var cache = this._cache;
    var fieldsMeta = this._fieldsMeta;

    return new Promise(function(resolve, reject) {
       server.runCommand(cmd, function(err, res) {
           if(err !== null) {
                reject(err);
           }
           else {
                if (fieldsMeta !== null) {
                    resolve(new QueryCursor(cache, qry, false, res, fieldsMeta));
                }
                else {
                    resolve(new QueryCursor(cache, qry, false, res, res["fieldsMetadata"]))
                }
           }
       });
    });
}

/**
 * Gets query fields metadata
 *
 * @this{QueryCursor}
 * @returns {Object[]} Query fields metadata.
 */
QueryCursor.prototype.fieldsMetadata = function() {
    return this._fieldsMeta;
}

/**
 * Gets collections of the query page results.
 *
 * @this{QueryCursor}
 * @returns {Object[]} Query page result.
 */
QueryCursor.prototype.page = function() {
    if (this._res === null)
        return null;

    return this._res["items"];
}

/**
 * Closes all resources related to this cursor.
 *
 * @this{QueryCursor}
 * @returns {Promise} Promise on cursor close.
 */
QueryCursor.prototype.close = function() {
    if (this._init) {
        return new Promise(function(resolve, reject) {
            return resolve(true);
        });
    }

    var server = this._cache._server;
    var cmd = this._createQueryCommand("qrycls", this._qry).addParam("qryId", this._res.queryId);

    return new Promise(function(resolve, reject) {
       server.runCommand(cmd, function(err, res) {
           if(err != null) {
               reject(err);
           }
           else {
               resolve(true);
           }
       });
    });
}

/**
 * Returns True if the iteration has no more elements.
 *
 * @this{QueryCursor}
 * @returns {boolean} True if it is the last page
 */
QueryCursor.prototype.isFinished = function() {
    if (this._res === null)
        return false;

    return this._res["last"];
}

QueryCursor.prototype._getQueryCommand = function() {
    if (this._init) {
        if (this._qry.type() === "Sql") {
            return this._sqlQuery(this._qry);
        }

        this._init = false;

        return this._sqlFieldsQuery(this._qry);
    }

    return this._cache._createCommand("qryfetch").addParam("qryId", this._res.queryId).
        addParam("psz", this._qry.pageSize());
}

QueryCursor.prototype._sqlFieldsQuery = function(qry) {
    return this._createQueryCommand("qryfldexe", qry).
        setPostData(JSON.stringify({"arg" : qry.arguments()}));
}

QueryCursor.prototype._sqlQuery = function(qry) {
    return this._createQueryCommand("qryexe", qry).addParam("type", qry.returnType()).
        setPostData(JSON.stringify({"arg" : qry.arguments()}));
}

QueryCursor.prototype._createQueryCommand = function(name, qry) {
    return new Command(name).addParam("cacheName", this._cache._cacheName).
        addParam("qry", qry.query()).addParam("psz", qry.pageSize());
}

/**
 * @this{CacheEntry}
 * @param key Key
 * @param val Value
 */
function CacheEntry(key0, val0) {
    this.key = key0;
    this.value = val0;
}

exports.Cache = Cache
exports.CacheEntry = CacheEntry