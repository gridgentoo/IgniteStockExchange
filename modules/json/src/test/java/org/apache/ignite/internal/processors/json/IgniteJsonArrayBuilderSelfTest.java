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

package org.apache.ignite.internal.processors.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.json.IgniteJson;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;

/**
 * Tests for {@link IgniteJsonArrayBuilder} implementation.
 */
public class IgniteJsonArrayBuilderSelfTest extends GridCommonAbstractTest {
    /** JSON provider. */
    private static JsonProvider json;

    /** {@inheritDoc} */
    @Override protected void beforeTestsStarted() throws Exception {
        super.beforeTestsStarted();

        json = IgniteJson.jsonProvider(startGrid());
    }

    /** {@inheritDoc} */
    @Override protected void afterTestsStopped() throws Exception {
        super.afterTestsStopped();

        stopAllGrids();
    }

    /**
     * @throws Exception If failed.
     */
    public void testAddJavaObjects() throws Exception {
        JsonArray arr = json.createArrayBuilder()
            .add(true)
            .add(false)
            .add(1)
            .add(1L)
            .add(1.0)
            .add(new BigInteger("1"))
            .add(new BigDecimal(1))
            .add("string")
            .build();

        List<Object> list = ((IgniteJsonArray)arr).list();

        assertEquals(JsonValue.TRUE, arr.get(0));
        assertEquals(Boolean.TRUE, list.get(0));

        assertEquals(JsonValue.FALSE, arr.get(1));
        assertEquals(Boolean.FALSE, list.get(1));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), arr.get(2));
        assertEquals(1, list.get(2));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), arr.get(3));
        assertEquals(1L, list.get(3));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), arr.get(4));
        assertEquals(1.0, (Double)list.get(4), 0);

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), arr.get(5));
        assertEquals(new BigInteger("1"), list.get(5));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), arr.get(6));
        assertEquals(new BigDecimal(1), list.get(6));

        assertEquals(new IgniteJsonString("string"), arr.get(7));
        assertEquals("string", list.get(7));
    }

    /**
     * @throws Exception If failed.
     */
    public void testAddJsonObjects() throws Exception {
        JsonArray arr = json.createArrayBuilder()
            .addNull()
            .add(JsonValue.NULL)
            .add(JsonValue.TRUE)
            .add(JsonValue.FALSE)
            .add(new IgniteJsonNumber(new BigDecimal(1)))
            .add(new IgniteJsonString("string"))
            .add(json.createArrayBuilder().add(1).add(2).build())
            .add(json.createArrayBuilder().add(1).add(2))
            .add(json.createObjectBuilder().add("k1", 1).add("k2", 2).build())
            .add(json.createObjectBuilder().add("k1", 1).add("k2", 2))
            .build();

        List<Object> list = ((IgniteJsonArray)arr).list();

        assertEquals(JsonValue.NULL, arr.get(0));
        assertNull(list.get(0));

        assertEquals(JsonValue.NULL, arr.get(1));
        assertNull(list.get(1));

        assertEquals(JsonValue.TRUE, arr.get(2));
        assertEquals(Boolean.TRUE, list.get(2));

        assertEquals(JsonValue.FALSE, arr.get(3));
        assertEquals(Boolean.FALSE, list.get(3));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), arr.get(4));
        assertEquals(new BigDecimal(1), list.get(4));

        assertEquals(new IgniteJsonString("string"), arr.get(5));
        assertEquals("string", list.get(5));

        assertEquals(json.createArrayBuilder().add(1).add(2).build(), arr.get(6));
        assertEqualsCollections(F.asList(1, 2), (List)list.get(6));

        assertEquals(json.createArrayBuilder().add(1).add(2).build(), arr.get(7));
        assertEqualsCollections(F.asList(1, 2), (List)list.get(7));

        JsonObject obj0 = json.createObjectBuilder().add("k1", 1).add("k2", 2).build();
        assertEquals(obj0, arr.get(8));
        assertEquals(((IgniteJsonObject)obj0).binaryObject(), list.get(8));

        JsonObject obj1 = json.createObjectBuilder().add("k1", 1).add("k2", 2).build();
        assertEquals(obj1, arr.get(9));
        assertEquals(((IgniteJsonObject)obj1).binaryObject(), list.get(9));
    }
}