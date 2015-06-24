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

import javax.script.*;

import static javax.script.ScriptContext.*;

/**
 * Ignite scripting processor.
 */
public class IgniteScriptProcessor extends GridProcessorAdapter {
    /** Javascript engine name. */
    public static final String JAVA_SCRIPT_ENGINE_NAME = "JavaScript";

    /** Javascript engine. */
    private ScriptEngine jsEngine;

    /**
     * @param ctx Kernal context.
     */
    public IgniteScriptProcessor(GridKernalContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    @Override public void start() throws IgniteCheckedException {
        ScriptEngineManager factory = new ScriptEngineManager();

        jsEngine = factory.getEngineByName(JAVA_SCRIPT_ENGINE_NAME);

        Bindings bind = jsEngine.createBindings();

        bind.put("ignite", new IgniteJS());

        jsEngine.setBindings(bind, ENGINE_SCOPE);

        String createJSFunction = "function __createJSFunction(mapFunc) {" +
                "return eval('(function() { return ' + mapFunc.trim() + '})()'); }";

        String internalCall  = "function __internalCall(funcSource) { " +
                "var func = __createJSFunction(funcSource); " +
                "var arg = Array.prototype.slice.call(arguments, 0);" +
                "arg.shift();" +
                "return func.apply(null, arg);" +
                "}";

        addEngineFunction(createJSFunction);
        addEngineFunction(internalCall);
    }

    /**
     * Add function to scope.
     *
     * @param script Function script.
     */
    public void addEngineFunction(String script) {
        try {
            jsEngine.eval(script);
        }
        catch (ScriptException e) {
            throw new IgniteException("Script Engine does not work.", e);
        }
    }

    /**
     * @param source Script source.
     * @param args Arguments.
     * @return Result of the function.
     */
    public Object invokeFunction(String source, Object... args) {
        Object[] newArgs = new Object[args.length + 1];

        newArgs[0] = source;

        System.arraycopy(args, 0, newArgs, 1, args.length);

        return invokeFunctionByName("__internalCall", newArgs);
    }

    /**
     * Invoke function.
     *
     * @param nameFunc Function name.
     * @param args Function arguments.
     * @return Result of the function.
     */
    public Object invokeFunctionByName(String nameFunc, Object... args) {
        Invocable invocable = (Invocable) jsEngine;

        try {
            return invocable.invokeFunction(nameFunc, args);
        }
        catch (ScriptException e) {
            throw new IgniteException("Script Engine does not work.", e);
        }
        catch (NoSuchMethodException e) {
            throw new IgniteException("Script Engine does not work.", e);
        }
    }
}
