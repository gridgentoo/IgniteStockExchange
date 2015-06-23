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
import org.apache.ignite.internal.util.typedef.*;

import javax.script.*;
import java.util.*;

/**
 * Ignite scripting manager.
 */
public class IgniteScriptProcessor extends GridProcessorAdapter {
    /** Javascript engine name. */
    public static final String JAVA_SCRIPT_ENGINE_NAME = "JavaScript";

    /** Script factory **/
    private final ScriptEngineManager factory = new ScriptEngineManager();

    /**
     * @param ctx Kernal context.
     */
    public IgniteScriptProcessor(GridKernalContext ctx) {
        super(ctx);
    }

    /**
     * @param engName Engine name.
     * @param script Script.
     * @throws ScriptException If script failed.
     */
    public Object run(String engName, String script) throws ScriptException {
        if (!engName.equals(JAVA_SCRIPT_ENGINE_NAME))
            throw new IgniteException("Engine is not supported. [engName=" + engName + "]");

        return runJS(script);
    }

    /**
     * @param script Script.
     * @param args Arguments.
     * @return Script result.
     * @throws ScriptException If script failed.
     */
    public Object runJS(String script, String[] args) throws ScriptException {
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        Bindings b = engine.createBindings();

        b.put("ignite", new Ignite());

        engine.setBindings(b, ScriptContext.ENGINE_SCOPE);

        script = "(" + script + ")(" ;

        for (int i = 0; i < args.length; ++i)
            script += args[i] + (i < args.length - 1 ? "," : "");

        script += ");";

        return engine.eval(script);
    }

    /**
     * @param script Script.
     * @return Script result.
     * @throws ScriptException If script failed.
     */
    public Object runJS(String script) throws ScriptException {
        return runJS(script, new String[]{""});
    }

    /**
     * Ignite JS binding.
     */
    public static class Ignite {
        public void hello() {
            System.out.println("HELLO HAPPY WORLD!!!");
        }
    }
}
