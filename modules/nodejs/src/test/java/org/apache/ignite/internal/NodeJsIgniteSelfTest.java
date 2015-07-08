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

package org.apache.ignite.internal;

/**
 * Test for node js ignite.
 */
public class NodeJsIgniteSelfTest extends NodeJsAbstractTest {
    /**
     * Constructor.
     */
    public NodeJsIgniteSelfTest() {
        super("test-ignite.js");
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
    public void testIgniteVersion() throws Exception {
        runJsScript("testIgniteVersion");
    }

    /**
     * @throws Exception If failed.
     */
    public void testIgniteName() throws Exception {
        runJsScript("testIgniteName");
    }

    /**
     * @throws Exception If failed.
     */
    public void testCluster() throws Exception {
        runJsScript("testCluster");
    }

    /**
     * @throws Exception If failed.
     */
    public void testDestroyCache() throws Exception {
        runJsScript("testDestroyCache");
    }
}
