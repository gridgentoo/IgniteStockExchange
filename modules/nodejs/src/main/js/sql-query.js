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
 * @this {SqlQuery}
 * @param {string} Sql query
 */
function SqlQuery(sql) {
    this._sql = sql;
    this._arg = [];
    this._pageSz = 1;
    this._type = null;
    this._endFunc = function(res) {console.log("Empty end function is called [res=" + res + "]")};
    this._pageFunc = function(res) {console.log("Empty page function is called [res=" + res + "]")}
    this._errFunc = function(err) {console.log("Empty error function is called [err=" + err + "]")}
}

/**
 * Set the callbacks for query events.
 *
 * @this {SqlQuery}
 * @param {string} code Function code could be "end", "page", "error"
 * @param function Functions "error" and "page" are one argument functions and "end" is function without arguments.
 */
SqlQuery.prototype.on = function(code, f) {
    switch(code) {
        case "end":
            this._endFunc = f;

            break;
        case "page":
            this._pageFunc = f;

            break;
        case "error" :
            this._errFunc = f;

            break;
        default :
            throw "Sql do not have method " + code;
    }
}

/**
 * @this {SqlQuery}
 * @param res Query result
 */
SqlQuery.prototype.end = function(res) {
    this._endFunc(res);
}

/**
 * @this {SqlQuery}
 * @param err Query error
 */
SqlQuery.prototype.error = function(err) {
    this._errFunc(err);
}

/**
 * @this {SqlQuery}
 * @param res Query data
 */
SqlQuery.prototype.page = function(res) {
    this._pageFunc(res);
}

/**
 * @this {SqlQuery}
 * @param {int} pageSz Page size.
 */
SqlQuery.prototype.setPageSize = function(pageSz) {
    this._pageSize = pageSz;
}

/**
 * @this {SqlQuery}
 * @param args Arguments
 */
SqlQuery.prototype.setArguments = function(args) {
    this._arg = args;
}

/**
 * @this {SqlQuery}
 * @param type Return class name
 */
SqlQuery.prototype.setReturnType = function(type) {
    this._type = type;
}

/**
 * @this {SqlQuery}
 * @returns Sql query
 */
SqlQuery.prototype.query = function() {
    return this._sql;
}

/**
 * @this {SqlQuery}
 * @returns arguments
 */
SqlQuery.prototype.arguments = function() {
    return this._arg;
}

/**
 * @this {SqlQuery}
 * @returns pageSize
 */
SqlQuery.prototype.pageSize = function() {
    return this._pageSz;
}

/**
 * @this {SqlQuery}
 * @returns Return class name
 */
SqlQuery.prototype.returnType = function() {
    return this._type;
}

/**
 * @this {SqlQuery}
 * @returns "Sql"
 */
SqlQuery.prototype.type = function() {
    return SqlQuery.type();
}

/**
 * @returns "Sql"
 */
SqlQuery.type = function() {
    return "Sql"
}

exports.SqlQuery = SqlQuery;