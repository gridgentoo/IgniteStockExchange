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

/**
 * Node js ignite.
 */
public class ScriptingJSIgnite {
    /** Ignite. */
    private Ignite ignite;

    /**
     * @param ignite Ignite.
     */
    public ScriptingJSIgnite(Ignite ignite) {
        this.ignite = ignite;
    }

    /**
     * @param cache Cache name.
     * @return Node js cache.
     */
    public ScriptingJsCache cache(String cache) {
        return new ScriptingJsCache(ignite.cache(cache), ((IgniteKernal)ignite).context().scripting());
    }

    /**
     * @param cache Cache name.
     * @return Node js cache.
     */
    public ScriptingJsCache getOrCreateCache(String cache) {
        return new ScriptingJsCache(ignite.getOrCreateCache(cache), ((IgniteKernal)ignite).context().scripting());
    }

    /**
     * @return Local node.
     */
    public ScriptingClusterNode localNode() {
        return new ScriptingClusterNode(ignite.cluster().localNode());
    }

    /**
     * @return Ignite name.
     */
    public String name() {
        return ignite.name();
    }
}
