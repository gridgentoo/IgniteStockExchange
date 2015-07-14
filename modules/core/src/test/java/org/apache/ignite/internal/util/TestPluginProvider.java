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

package org.apache.ignite.internal.util;

import org.apache.ignite.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.plugin.*;
import org.apache.ignite.testframework.*;

import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 *
 */
public class TestPluginProvider extends PluginProviderAdapter<TestPluginProvider.TestPluginCfg> {
    /** */
    public static boolean bfStart, start, afStart, bfStop, stop, afStop;

    /** */
    public static boolean enableAssert = false;

    /** */
    IgniteKernal ignite;

    /** {@inheritDoc} */
    @Override public String name() {
        return "TestPlugin";
    }

    /** {@inheritDoc} */
    @Override public IgnitePlugin plugin() {
        return new IgnitePlugin() {};
    }

    /** {@inheritDoc} */
    @Override public void onBeforeStart() throws IgniteCheckedException {
        bfStart = true;

        if (enableAssert)
            assertFalse(start || afStart || bfStop || stop || afStop);
    }

    /** {@inheritDoc} */
    @Override public void start(PluginContext ctx) throws IgniteCheckedException {
        ignite = (IgniteKernal)ctx.grid();

        start = true;

        if (enableAssert)
            assertFalse(afStart || bfStop || stop || afStop);
    }

    @Override public void onAfterStart() throws IgniteCheckedException {
        afStart = true;

        if (enableAssert)
            assertFalse(bfStop || stop || afStop);
    }

    @Override public void onBeforeStop(boolean cancel) {
        bfStop = true;

        if (enableAssert)
            assertFalse(stop || afStop);
    }

    @Override public void stop(boolean cancel) throws IgniteCheckedException {
        stop = true;

        if (enableAssert)
            assertFalse(afStop);
    }

    @Override public void onAfterStop(boolean cancel) {
        if (enableAssert)
            GridTestUtils.assertThrows(null, new Callable<Object>() {
                @Override public Object call() throws Exception {
                    return ignite.cache(null);
                }
            }, IllegalStateException.class, null);

        afStop = true;
    }

    /** */
    public static class TestPluginCfg implements PluginConfiguration {
        // No-op.
    }
}
