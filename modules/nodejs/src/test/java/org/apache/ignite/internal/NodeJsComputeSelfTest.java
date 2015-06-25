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

import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.testframework.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Test compute node js.
 */
public class NodeJsComputeSelfTest extends NodeJsAbstractTest {
    /**
     * Constructor.
     */
    public NodeJsComputeSelfTest() {
        super("test-compute.js");
    }

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        startGrids(2);
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    public void testComputeRunScript() throws Exception {
        runJsScript("testComputeRunScript");
    }

    /**
     * @throws Exception If failed.
     */
    public void testComputeExecute() throws Exception {
        runJsScript("testComputeExecute");
    }

    /**
     * @throws Exception If failed.
     */
    public void testComputeFuncWithErrorExecute() throws Exception {
        runJsScript("testComputeFuncWithErrorExecute");
    }

    /**
     * @throws Exception If failed.
     */
    public void testComputeIncorrectFuncExecute() throws Exception {
        runJsScript("testComputeIncorrectFuncExecute");
    }

    /**
     * @throws Exception If failed.
     */
    public void testComputeIncorrectMapExecute() throws Exception {
        runJsScript("testComputeIncorrectMapExecute");
    }

    /**
     * @throws Exception If failed.
     */
    public void testRestartGrid() throws Exception {
        final AtomicInteger id = new AtomicInteger(2);
        IgniteInternalFuture<Long> fut = GridTestUtils.runMultiThreadedAsync(new Callable<Object>() {
            @Override public Object call() throws Exception {
                ArrayList<Integer> ids = new ArrayList<Integer>();

                for (int i = 0 ; i < 3; ++i) {
                    int cur = id.getAndIncrement();

                    startGrid(cur);

                    ids.add(cur);
                }

                for (int i = 0; i < ids.size(); ++i)
                    stopGrid(ids.get(i));

                return null;
            }
        }, 2, "runIgnite");

        while (!fut.isDone())
            runJsScript("testComputeAllNodeExecute");

        stopGrid(1);

        U.sleep(500);

        startGrid(1);

        U.sleep(3000);
    }
}
