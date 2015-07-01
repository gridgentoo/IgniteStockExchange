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

package org.apache.ignite.internal.processors.rest.handlers.scripting;

import org.apache.ignite.internal.util.typedef.*;

import java.util.*;

/**
 * Emit result binding.
 */
public class IgniteJsEmitResult {
    /** Thread local emit result.*/
    private ThreadLocal<List<T3<Object, Object, Object>>> emitResPerCall = new ThreadLocal<>();

    /**
     * @param f JS function.
     * @param args Function arguments.
     * @param node Node.
     */
    public void add(Object f, Object args, Object node) {
        List<T3<Object, Object, Object>> res = emitResPerCall.get();

        if (res == null)
            res = new ArrayList<>();

        res.add(new T3<>(f, args, node));

        emitResPerCall.set(res);
    }

    /**
     * @return Emit result.
     */
    public List<T3<Object, Object, Object>> getEmitResult() {
        List<T3<Object, Object, Object>> res = emitResPerCall.get();

        emitResPerCall.set(null);

        return res;
    }
}
