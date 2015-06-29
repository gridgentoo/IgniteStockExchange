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

var SqlFieldsQuery = require("./sql-fields-query").SqlFieldsQuery

/**
 * @this {SqlQuery}
 * @param {string} Sql query
 */
function SqlQuery(sql) {
    SqlFieldsQuery.apply(this, arguments);
    this._type = null;
    this._qryType = "Sql";
}

SqlQuery.prototype = SqlFieldsQuery.prototype;

SqlQuery.prototype.constructor = SqlQuery;


/**
 * @this {SqlQuery}
 * @param type Return class name
 */
SqlQuery.prototype.setReturnType = function(type) {
    this._type = type;
}

/**
 * @this {SqlQuery}
 * @returns Return class name
 */
SqlQuery.prototype.returnType = function() {
    return this._type;
}

exports.SqlQuery = SqlQuery;