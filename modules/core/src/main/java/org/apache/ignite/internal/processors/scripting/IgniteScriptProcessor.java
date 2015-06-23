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
    }

    /**
     * @param script Script.
     * @param args Arguments.
     * @return Script result.
     * @throws ScriptException If script failed.
     */
    public Object runJSFunction(String script, String... args) throws IgniteException {
        try {
            return jsEngine.eval(callJsFunction(script, args));
        }
        catch (ScriptException e) {
            throw new IgniteException("Cannot evaluate javascript function + " + script, e);
        }
    }

    /**
     * @param script Script.
     * @return Script result.
     * @throws ScriptException If script failed.
     */
    public Object runJSFunction(String script) throws IgniteException {
        return runJSFunction(script, new String[]{""});
    }

    /**
     * @param script JS function script.
     * @param args Arguments.
     * @return Script that calls function.
     */
    private String callJsFunction(String script, String[] args) {
        String callFuncScript = "(" + script + ")(";

        for (int i = 0; i < args.length; ++i)
            callFuncScript += args[i] + (i < args.length - 1 ? "," : "");

        callFuncScript += ");";

        return callFuncScript;
    }
}
