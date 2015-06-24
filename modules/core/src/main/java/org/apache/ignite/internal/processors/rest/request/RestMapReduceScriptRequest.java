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

package org.apache.ignite.internal.processors.rest.request;

/**
 * Execute map reduce script request.
 */
public class RestMapReduceScriptRequest extends GridRestRequest {
    /** Mapping tasks to nodes. */
    private String mapFunc;

    /** Function argument. */
    private Object arg;

    /** Reduce function. */
    private String reduceFunc;

    /**
     * @param reduceFunc Reduce function.
     */
    public void reduceFunction(String reduceFunc) {
        this.reduceFunc = reduceFunc;
    }

    /**
     * @return Reduce function.
     */
    public String reduceFunction() {
        return reduceFunc;
    }

    /**
     * @param mapFunc Map function.
     */
    public void mapFunction(String mapFunc) {
        this.mapFunc = mapFunc;
    }

    /**
     * @return Map function.
     */
    public String mapFunction() {
        return mapFunc;
    }

    /**
     * @param arg Argument.
     */
    public void argument(Object arg) {
        this.arg = arg;
    }

    /**
     * @return Argument.
     */
    public Object argument() {
        return arg;
    }
}
