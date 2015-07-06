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
 * Test node js client put/get.
 */
public class NodeJsCacheApiSelfTest extends NodeJsAbstractTest {
    /**
     * Constructor.
     */
    public NodeJsCacheApiSelfTest() {
        super("test-cache-api.js");
    }

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        startGrid(0);
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        stopAllGrids();
    }

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        grid(0).cache(NodeJsAbstractTest.CACHE_NAME).removeAll();
    }

    /**
     * @throws Exception If failed.
     */
    public void testPutGet() throws Exception {
        runJsScript("testPutGet");
    }

    /**
     * @throws Exception If failed.
     */
    public void testPutGetObject() throws Exception {
        runJsScript("testPutGetObject");
    }

    /**
     * @throws Exception If failed.
     */
    public void testIncorrectCache() throws Exception {
        runJsScript("testIncorrectCacheName");
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetOrCreateCacheName() throws Exception {
        runJsScript("testGetOrCreateCacheName");
    }

    /**
     * @throws Exception If failed.
     */
    public void testRemove() throws Exception {
        runJsScript("testRemove");
    }

    /**
     * @throws Exception If failed.
     */
    public void testRemoveNoKey() throws Exception {
        runJsScript("testRemoveNoKey");
    }

    /**
     * @throws Exception If failed.
     */
    public void testRemoveAll() throws Exception {
        runJsScript("testRemoveAll");
    }

    /**
     * @throws Exception If failed.
     */
    public void testPutAllGetAll() throws Exception {
        runJsScript("testPutAllGetAll");
    }

    /**
     * @throws Exception If failed.
     */
    public void testPutAllObjectGetAll() throws Exception {
        runJsScript("testPutAllObjectGetAll");
    }

    /**
     * @throws Exception If failed.
     */
    public void testRemoveAllObjectGetAll() throws Exception {
        runJsScript("testRemoveAllObjectGetAll");
    }

    /**
     * @throws Exception If failed.
     */
    public void testContains() throws Exception {
        runJsScript("testContains");
    }

    /**
     * @throws Exception If failed.
     */
    public void testPutContains() throws Exception {
        runJsScript("testPutContains");
    }

    /**
     * @throws Exception If failed.
     */
    public void testPutContainsAll() throws Exception {
        runJsScript("testPutContainsAll");
    }

    /**
     * @throws Exception If failed.
     */
    public void testNotContainsAll() throws Exception {
        runJsScript("testNotContainsAll");
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetAndPut() throws Exception {
        runJsScript("testGetAndPut");
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetAndPutIfAbsent() throws Exception {
        runJsScript("testGetAndPutIfAbsent");
    }

    /**
     * @throws Exception If failed.
     */
    public void testPutIfAbsent() throws Exception {
        runJsScript("testPutIfAbsent");
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetAndRemove() throws Exception {
        runJsScript("testGetAndRemove");
    }

    /**
     * @throws Exception If failed.
     */
    public void testRemoveValue() throws Exception {
        runJsScript("testRemoveValue");
    }

    /**
     * @throws Exception If failed.
     */
    public void testRemoveAllFromCache() throws Exception {
        runJsScript("testRemoveAllFromCache");
    }

    /**
     * @throws Exception If failed.
     */
    public void testReplace() throws Exception {
        runJsScript("testReplace");
    }

    /**
     * @throws Exception If failed.
     */
    public void testIncorrectReplaceObject() throws Exception {
        runJsScript("testIncorrectReplaceObject");
    }

    /**
     * @throws Exception If failed.
     */
    public void testReplaceObject() throws Exception {
        runJsScript("testReplaceObject");
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetAndReplaceObject() throws Exception {
        runJsScript("testGetAndReplaceObject");
    }

    /**
     * @throws Exception If failed.
     */
    public void testReplaceValueObject() throws Exception {
        runJsScript("testReplaceValueObject");
    }

    /**
     * @throws Exception If failed.
     */
    public void testSize() throws Exception {
        runJsScript("testSize");
    }
}
