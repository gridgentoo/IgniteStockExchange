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
 * Test node js client.
 */
public class NodeJsIgnitionSelfTest extends NodeJsAbstractTest {
    /**
     * Constructor.
     */
    public NodeJsIgnitionSelfTest() {
        super("test-ignition.js");
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
    public void testIgnitionStartSuccess() throws Exception {
        runJsScript("testIgnitionStartSuccess");
    }

    /**
     * @throws Exception If failed.
     */
    public void testIgnitionFail() throws Exception {
        runJsScript("testIgnitionFail");
    }

    /**
     * @throws Exception If failed.
     */
    public void testIgnitionStartSuccessWithSeveralPorts() throws Exception {
        runJsScript("testIgnitionStartSuccessWithSeveralPorts");
    }

    /**
     * @throws Exception If failed.
     */
    public void testIgnitionNotStartWithSeveralPorts() throws Exception {
        runJsScript("testIgnitionNotStartWithSeveralPorts");
    }
}
