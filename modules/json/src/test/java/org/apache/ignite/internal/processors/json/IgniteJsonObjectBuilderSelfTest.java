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
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.json.IgniteJson;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;

/**
 * Tests for {@link IgniteJsonObjectBuilder} implementation.
 */
public class IgniteJsonObjectBuilderSelfTest extends GridCommonAbstractTest {
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
        JsonObject jsonObj = json.createObjectBuilder()
            .add("0", true)
            .add("1", false)
            .add("2", 1)
            .add("3", 1L)
            .add("4", 1.0)
            .add("5", new BigInteger("1"))
            .add("6", new BigDecimal(1))
            .add("7", "string")
            .build();

        BinaryObject binObj = ((IgniteJsonObject)jsonObj).binaryObject();

        assertEquals(JsonValue.TRUE, jsonObj.get("0"));
        assertEquals(Boolean.TRUE, binObj.field("0"));

        assertEquals(JsonValue.FALSE, jsonObj.get("1"));
        assertEquals(Boolean.FALSE, binObj.field("1"));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), jsonObj.get("2"));
        assertEquals(1, binObj.field("2"));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), jsonObj.get("3"));
        assertEquals(1L, binObj.field("3"));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), jsonObj.get("4"));
        assertEquals(1.0, (Double)binObj.field("4"), 0);

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), jsonObj.get("5"));
        assertEquals(new BigInteger("1"), binObj.field("5"));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), jsonObj.get("6"));
        assertEquals(new BigDecimal(1), binObj.field("6"));

        assertEquals(new IgniteJsonString("string"), jsonObj.get("7"));
        assertEquals("string", binObj.field("7"));
    }

    /**
     * @throws Exception If failed.
     */
    public void testAddJsonObjects() throws Exception {
        JsonObject jsonObj = json.createObjectBuilder()
            .addNull("0")
            .add("1", JsonValue.NULL)
            .add("2", JsonValue.TRUE)
            .add("3", JsonValue.FALSE)
            .add("4", new IgniteJsonNumber(new BigDecimal(1)))
            .add("5", new IgniteJsonString("string"))
            .add("6", json.createArrayBuilder().add(1).add(2).build())
            .add("7", json.createArrayBuilder().add(1).add(2))
            .add("8", json.createObjectBuilder().add("k1", 1).add("k2", 2).build())
            .add("9", json.createObjectBuilder().add("k1", 1).add("k2", 2))
            .build();

        BinaryObject binObj = ((IgniteJsonObject)jsonObj).binaryObject();

        assertEquals(JsonValue.NULL, jsonObj.get("0"));
        assertNull(binObj.field("0"));

        assertEquals(JsonValue.NULL, jsonObj.get("1"));
        assertNull(binObj.field("1"));

        assertEquals(JsonValue.TRUE, jsonObj.get("2"));
        assertEquals(Boolean.TRUE, binObj.field("2"));

        assertEquals(JsonValue.FALSE, jsonObj.get("3"));
        assertEquals(Boolean.FALSE, binObj.field("3"));

        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), jsonObj.get("4"));
        assertEquals(new BigDecimal(1), binObj.field("4"));

        assertEquals(new IgniteJsonString("string"), jsonObj.get("5"));
        assertEquals("string", binObj.field("5"));

        assertEquals(json.createArrayBuilder().add(1).add(2).build(), jsonObj.get("6"));
        assertEqualsCollections(F.asList(1, 2), (List)binObj.field("6"));

        assertEquals(json.createArrayBuilder().add(1).add(2).build(), jsonObj.get("7"));
        assertEqualsCollections(F.asList(1, 2), (List)binObj.field("7"));

        JsonObject obj0 = json.createObjectBuilder().add("k1", 1).add("k2", 2).build();
        assertEquals(obj0, jsonObj.get("8"));
        assertEquals(((IgniteJsonObject)obj0).binaryObject(), binObj.field("8"));

        JsonObject obj1 = json.createObjectBuilder().add("k1", 1).add("k2", 2).build();
        assertEquals(obj1, jsonObj.get("9"));
        assertEquals(((IgniteJsonObject)obj1).binaryObject(), binObj.field("9"));
    }

}