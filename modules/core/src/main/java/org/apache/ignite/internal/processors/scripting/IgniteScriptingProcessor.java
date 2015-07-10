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

package org.apache.ignite.internal.processors.scripting;

import org.apache.ignite.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.*;
import org.apache.ignite.json.*;

import javax.script.*;

import java.lang.reflect.*;

import static javax.script.ScriptContext.*;

/**
 * Ignite scripting processor.
 */
public class IgniteScriptingProcessor extends GridProcessorAdapter {
    /** Javascript engine name. */
    public static final String JAVA_SCRIPT_ENGINE_NAME = "JavaScript";

    /** Javascript engine. */
    private ScriptEngine jsEngine;

    /**
     * @param ctx Kernal context.
     */
    public IgniteScriptingProcessor(GridKernalContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    @Override public void start() throws IgniteCheckedException {
        ScriptEngineManager factory = new ScriptEngineManager();

        jsEngine = factory.getEngineByName(JAVA_SCRIPT_ENGINE_NAME);

        addBinding("ignite", new ScriptingJSIgnite(ctx.grid()));

        String createJSFunction = "function __createJSFunction(mapFunc) {" +
                "return eval('(function() { return ' + mapFunc.trim() + '})()'); }";

        String internalCall = "function __internalCall(funcSource, arg1, arg2) { " +
            "var func = __createJSFunction(funcSource); " +
            "return func.apply(null, [arg1, arg2]); }";

        String internalJSCall = "function __internalJSCall(funcSource, arg1, arg2) { " +
                "var func = __createJSFunction(funcSource); " +
                "return func.apply(null, [JSON.parse(arg1), arg2]); }";

        String entryFunction = "CacheEntry = function(key, val) {" +
            "this.key = key; this.value = val}";

        addEngineFunction(entryFunction);
        addEngineFunction(createJSFunction);
        addEngineFunction(internalCall);
        addEngineFunction(internalJSCall);
    }

    /**
     * Add function to scope.
     *
     * @param script Function script.
     * @throws IgniteCheckedException If script failed.
     */
    public void addEngineFunction(String script)  throws IgniteCheckedException {
        try {
            jsEngine.eval(script);
        }
        catch (ScriptException e) {
            throw new IgniteCheckedException("Script evaluation failed [script=" + script + "].", e);
        }
    }

    /**
     * Add binding.
     *
     * @param name Binding name.
     * @param o Object to bind.
     */
    public void addBinding(String name, Object o) {
        Bindings b = jsEngine.getBindings(ENGINE_SCOPE);

        b.put(name, o);

        jsEngine.setBindings(b, ENGINE_SCOPE);
    }

    /**
     * @param src Script src.
     * @return Result of the function.
     * @throws IgniteCheckedException If script failed.
     */
    public Object invokeFunction(String src) throws IgniteCheckedException {
        try {
            return jsEngine.eval("(" + src + ")()");
        }
        catch (ScriptException e) {
            throw new IgniteCheckedException("Function evaluation failed [funcName=" + src + "].");
        }
    }

    /**
     * @param src Script src.
     * @param arg Argument.
     * @return Result of the function.
     * @throws IgniteCheckedException If script failed.
     */
    public Object invokeFunction(String src, Object arg) throws IgniteCheckedException {
        return invokeFunction(src, arg, null);
    }

    /**
     * @param src Script src.
     * @param arg Argument.
     * @return Result of the function.
     * @throws IgniteCheckedException If script failed.
     */
    public Object invokeFunction(String src, Object arg, Object arg2) throws IgniteCheckedException {
        try {
            Invocable invocable = (Invocable) jsEngine;

            return invocable.invokeFunction("__internalCall", src, arg, arg2);
        }
        catch (ScriptException e) {
            throw new IgniteCheckedException("Function evaluation failed [funcName=" + src +
                ", err= " + e.getMessage() + "].");
        }
        catch (NoSuchMethodException e) {
            throw new IgniteCheckedException("Cannot find function [func=__internalCall" +
                ", err= " + e.getMessage() + "].");
        }
    }

    /**
     * @param o Object.
     * @return  Object for Ignite cache.
     */
    public Object toJavaObject(Object o) {
        return JSONCacheObject.toSimpleObject(o);
    }

    /**
     * @param o Object from script.
     * @return Object to store in cache.
     */
    public Object getField(String key, Object o) {
        if (o instanceof JSONCacheObject)
            return ((JSONCacheObject)o).getField(key);

        return null;
    }

    /**
     * @param key Key.
     * @param val Value.
     * @return Scripting entry.
     */
    public Object createScriptingEntry(Object key, Object val) {
        return new ScriptingCacheEntry(key, val);
    }

    /**
     * Scripting cache entry.
     */
    public static class ScriptingCacheEntry {
        /** Key. */
        private Object key;

        /** Value. */
        private Object val;

        /**
         * @param key Key.
         * @param val Value.
         */
        public ScriptingCacheEntry(Object key, Object val) {
            this.key = key;
            this.val = val;
        }

        /**
         * @return Key.
         */
        public Object getKey() {
            return key;
        }

        /**
         * @param key Key.
         */
        public void setKey(Object key) {
            this.key = key;
        }

        /**
         * @return Value.
         */
        public Object getValue() {
            return val;
        }

        /**
         * @param val Value.
         */
        public void setValue(Object val) {
            this.val = val;
        }
    }
}
