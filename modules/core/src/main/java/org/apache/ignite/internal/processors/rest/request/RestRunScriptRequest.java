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
 * Run script request.
 */
public class RestRunScriptRequest extends GridRestRequest {
    /** Java script function. */
    private String script;

    /** Function arguments. */
    private Object arg;

    /** Key for affinity run. */
    private Object key;

    /** Cache name for affinity run. */
    private String cacheName;

    /**
     * @return Java script function.
     */
    public String script() {
        return script;
    }

    /**
     * @param script Java script function.
     */
    public void script(String script) {
        this.script = script;
    }

    /**
     * @return Function argument.
     */
    public Object argument() {
        return arg;
    }

    /**
     * @param arg Function argument.
     */
    public void argument(Object arg) {
        this.arg = arg;
    }

    /**
     * @return Key for affinity run.
     */
    public Object affinityKey() {
        return key;
    }

    /**
     * @param key Key for affinity run.
     */
    public void affinityKey(Object key) {
        this.key = key;
    }

    /**
     * @return Cache name for affinity run.
     */
    public String cacheName() {
        return cacheName;
    }

    /**
     * @param cacheName Cache name for affinity run.
     */
    public void cacheName(String cacheName) {
        this.cacheName = cacheName;
    }
}
