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

package org.apache.ignite.examples;

import org.apache.ignite.configuration.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.util.typedef.internal.*;

/**
 * Test for nodejs examples.
 */
public class NodeJsExamplesSelfTest extends NodeJsAbstractTest {
    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        return loadConfiguration("examples/config/js/example-query.xml");
    }

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        startGrid(0);
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    public void testCacheApiExample() throws Exception {
        run("cache-api-example.js");
    }

    /**
     * @throws Exception If failed.
     */
    public void testCachePutGetExample() throws Exception {
        run("cache-put-get-example.js");
    }

    /**
     * @throws Exception If failed.
     */
    public void testQueryExample() throws Exception {
        run("cache-query-example.js");
    }

    /**
     * @throws Exception If failed.
     */
    public void testFieldsQueryExample() throws Exception {
        run("cache-sql-fields-query-example.js");
    }

    /**
     * @throws Exception If failed.
     */
    public void testComputeRunExample() throws Exception {
        run("compute-run-example.js");
    }

    /**
     * @throws Exception If failed.
     */
    public void testComputeMapReduceExample() throws Exception {
        run("map-reduce-example.js");
    }

    /**
     * @param fileName Example file name.
     * @throws Exception If failed.
     */
    private void run(String fileName) throws Exception {
        runJsScript(null, U.getIgniteHome() + "/examples/src/main/js/" + fileName, ">>> end");
    }
}
