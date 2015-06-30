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
 * @this {SqlFieldsQuery}
 * @param {string} Sql query
 */
function SqlFieldsQuery(sql) {
    this._qryType = "SqlFields";
    this._sql = sql;
    this._arg = [];
    this._pageSz = 1;
    this._type = null;
    this._endFunc = function(err) {console.log("Empty end function is called [err=" + err + "]")};
    this._pageFunc = function(res) {console.log("Empty page function is called [res=" + res + "]")}
}

/**
 * Set the callbacks for query events.
 *
 * @this {SqlFieldsQuery}
 * @param {string} code Function code could be "end", "page"
 * @param function Functions "end" and "page" are one argument functions.
 */
SqlFieldsQuery.prototype.on = function(code, f) {
    switch(code) {
        case "end":
            this._endFunc = f;

            break;
        case "page":
            this._pageFunc = f;

            break;
        default :
            throw "Sql do not have method " + code;
    }
}

/**
 * @this {SqlFieldsQuery}
 * @param res Query result
 */
SqlFieldsQuery.prototype.end = function(err) {
    this._endFunc(err);
}

/**
 * @this {SqlFieldsQuery}
 * @param res Query data
 */
SqlFieldsQuery.prototype.page = function(res) {
    this._pageFunc(res);
}

/**
 * @this {SqlFieldsQuery}
 * @param {int} pageSz Page size.
 */
SqlFieldsQuery.prototype.setPageSize = function(pageSz) {
    this._pageSize = pageSz;
}

/**
 * @this {SqlFieldsQuery}
 * @param args Arguments
 */
SqlFieldsQuery.prototype.setArguments = function(args) {
    this._arg = args;
}

/**
 * @this {SqlFieldsQuery}
 * @returns Sql query
 */
SqlFieldsQuery.prototype.query = function() {
    return this._sql;
}

/**
 * @this {SqlFieldsQuery}
 * @returns arguments
 */
SqlFieldsQuery.prototype.arguments = function() {
    return this._arg;
}

/**
 * @this {SqlFieldsQuery}
 * @returns pageSize
 */
SqlFieldsQuery.prototype.pageSize = function() {
    return this._pageSz;
}

/**
 * @this {SqlFieldsQuery}
 * @returns "SqlFields"
 */
SqlFieldsQuery.prototype.type = function() {
    return this._qryType;
}

exports.SqlFieldsQuery = SqlFieldsQuery;