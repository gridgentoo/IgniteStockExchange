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
    this._pageSz = 0;
    this._endFunc = function(res) {console.log("Empty end function is called [res=" + res + "]")};
    this._errFunc = function(err) {console.log("Empty error function is called [err=" + err + "]")}
}

SqlQuery.prototype.on = function(code, f) {
    switch(code) {
        case "end":
            this._endFunc = f;

            break;
        case "error" :
            this._errFunc = f;

            break;
        default :
            throw "Sql do not have method " + code;
    }
}

SqlQuery.prototype.end = function(res) {
    return this._endFunc(res);
}

SqlQuery.prototype.error = function(err) {
    return this._errFunc(err);
}

SqlQuery.prototype.query = function() {
    return this._sql;
}

SqlQuery.prototype.arguments = function() {
    return this._arg;
}

SqlQuery.prototype.pageSize = function() {
    return this._pageSz;
}

exports.SqlQuery = SqlQuery;