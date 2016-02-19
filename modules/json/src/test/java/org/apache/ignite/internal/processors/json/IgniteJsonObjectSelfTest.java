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
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.json.IgniteJson;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;

/**
 * Tests for {@link IgniteJsonObject} implementation.
 */
public class IgniteJsonObjectSelfTest extends GridCommonAbstractTest {
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
    public void testIsNull() throws Exception {
        final JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("notNullField", "notNullField")
            .addNull("nullField")
            .build();

        // for emptyObj
        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyObj.isNull("notExistingField");

                return null;
            }
        }, NullPointerException.class, null);

        // for obj
        assertFalse(obj.isNull("notNullField"));
        assertTrue(obj.isNull("nullField"));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.isNull("notExistingField");

                return null;
            }
        }, NullPointerException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetBoolean() throws Exception {
        final JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("trueField", true)
            .add("falseField", false)
            .add("otherTypeField", "string")
            .addNull("nullField")
            .build();

        // for emptyObj
        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyObj.getBoolean("notExistingField");

                return null;
            }
        }, NullPointerException.class, null);


        // for obj
        assertTrue(obj.getBoolean("trueField"));
        assertFalse(obj.getBoolean("falseField"));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getBoolean("otherTypeField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getBoolean("nullField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getBoolean("notExistingField");

                return null;
            }
        }, NullPointerException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetBooleanWithDefault() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("trueField", true)
            .add("falseField", false)
            .add("otherTypeField", "string")
            .addNull("nullField")
            .build();

        // for emptyObj
        assertTrue(emptyObj.getBoolean("notExistingField", true));

        // for obj
        assertTrue(obj.getBoolean("trueField", false));
        assertFalse(obj.getBoolean("falseField", true));
        assertTrue(obj.getBoolean("otherTypeField", true));
        assertTrue(obj.getBoolean("nullField", true));
        assertTrue(obj.getBoolean("notExistingField", true));
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetInt() throws Exception {
        final JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("intField", 42)
            .add("otherTypeField", "string")
            .addNull("nullField")
            .build();

        // for emptyObj
        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyObj.getInt("notExistingField");

                return null;
            }
        }, NullPointerException.class, null);


        // for obj
        assertEquals(42, obj.getInt("intField"));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getInt("otherTypeField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getInt("nullField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getInt("notExistingField");

                return null;
            }
        }, NullPointerException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetIntWithDefault() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("intField", 42)
            .add("otherTypeField", "string")
            .addNull("nullField")
            .build();

        // for emptyObj
        assertEquals(42, emptyObj.getInt("notExistingField", 42));

        // for obj
        assertEquals(42, obj.getInt("intField", 666));
        assertEquals(42, obj.getInt("otherTypeField", 42));
        assertEquals(42, obj.getInt("nullField", 42));
        assertEquals(42, obj.getInt("notExistingField", 42));
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetString() throws Exception {
        final JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("strField", "string")
            .add("otherTypeField", 42)
            .addNull("nullField")
            .build();

        // for emptyObj
        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyObj.getString("notExistingField");

                return null;
            }
        }, NullPointerException.class, null);

        // for obj
        assertEquals("string", obj.getString("strField"));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getString("otherTypeField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getString("nullField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getString("notExistingField");

                return null;
            }
        }, NullPointerException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetStringWithDefault() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("strField", "string")
            .add("otherTypeField", 42)
            .addNull("nullField")
            .build();

        // for emptyObj
        assertEquals("default", emptyObj.getString("notExistingField", "default"));

        // for obj
        assertEquals("string", obj.getString("strField", "default"));
        assertEquals("default", obj.getString("otherTypeField", "default"));
        assertEquals("default", obj.getString("nullField", "default"));
        assertEquals("default", obj.getString("notExistingField", "default"));
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetJsonNumber() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("numField", 42)
            .add("otherTypeField", "string")
            .addNull("nullField")
            .build();

        // for emptyObj
        assertNull(emptyObj.getJsonNumber("notExistingField"));

        // for obj
        assertEquals(new IgniteJsonNumber(new BigDecimal(42)), obj.getJsonNumber("numField"));

        assertNull(obj.getJsonNumber("notExistingField"));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getJsonNumber("otherTypeField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getJsonNumber("nullField");

                return null;
            }
        }, ClassCastException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetJsonString() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("strField", "string")
            .add("otherTypeField", 42)
            .addNull("nullField")
            .build();

        // for emptyObj
        assertNull(emptyObj.getJsonString("notExistingField"));

        // for obj
        assertEquals(new IgniteJsonString("string"), obj.getJsonString("strField"));

        assertNull(obj.getJsonString("notExistingField"));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getJsonString("otherTypeField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getJsonString("nullField");

                return null;
            }
        }, ClassCastException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetJsonObject() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("objField", json.createObjectBuilder().add("key", "val").build())
            .add("otherTypeField", 42)
            .addNull("nullField")
            .build();

        // for emptyObj
        assertNull(emptyObj.getJsonObject("notExistingField"));

        // for obj
        assertEquals(json.createObjectBuilder().add("key", "val").build(), obj.getJsonObject("objField"));

        assertNull(obj.getJsonObject("notExistingField"));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getJsonObject("otherTypeField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getJsonObject("nullField");

                return null;
            }
        }, ClassCastException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetJsonArray() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        final JsonObject obj = json.createObjectBuilder()
            .add("arrField", json.createArrayBuilder().add(1).add(2).build())
            .add("otherTypeField", 42)
            .addNull("nullField")
            .build();

        // for emptyObj
        assertNull(emptyObj.getJsonArray("notExistingField"));

        // for emptyObj
        assertEquals(json.createArrayBuilder().add(1).add(2).build(), obj.getJsonArray("arrField"));

        assertNull(obj.getJsonArray("notExistingField"));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getJsonArray("otherTypeField");

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                obj.getJsonArray("nullField");

                return null;
            }
        }, ClassCastException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testIsEmptyAndSize() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        JsonObject obj1 = json.createObjectBuilder().add("k1", "v1").build();

        JsonObject obj2 = json.createObjectBuilder().add("k2", "v2").build();

        JsonObject obj3 = json.createObjectBuilder()
            .add("k1", "v1")
            .add("k2", "v2")
            .build();

        JsonObject obj4 = json.createObjectBuilder()
            .add("k1", "v1")
            .addNull("nullField")
            .build();

        assertEquals(0, emptyObj.size());
        assertTrue(emptyObj.isEmpty());

        assertEquals(1, obj1.size());
        assertFalse(obj1.isEmpty());

        assertEquals(1, obj2.size());
        assertFalse(obj2.isEmpty());

        assertEquals(2, obj3.size());
        assertFalse(obj3.isEmpty());

        assertEquals(2, obj4.size());
        assertFalse(obj4.isEmpty());
    }

    /**
     * @throws Exception If failed.
     */
    public void testContainsKey() throws Exception {
        final JsonObject emptyObj = json.createObjectBuilder().build();

        JsonObject obj1 = json.createObjectBuilder().add("k1", "v1").build();

        JsonObject obj2 = json.createObjectBuilder().add("k2", "v2").build();

        JsonObject obj3 = json.createObjectBuilder()
            .add("k1", "v1")
            .add("k2", "v2")
            .build();

        JsonObject obj4 = json.createObjectBuilder()
            .add("k1", "v1")
            .addNull("nullField")
            .build();

        assertFalse(emptyObj.containsKey("key"));

        assertTrue(obj1.containsKey("k1"));
        assertFalse(obj1.containsKey("k2"));

        assertTrue(obj2.containsKey("k2"));
        assertFalse(obj2.containsKey("k1"));

        assertTrue(obj3.containsKey("k1"));
        assertTrue(obj3.containsKey("k2"));
        assertFalse(obj3.containsKey("key"));

        assertTrue(obj4.containsKey("k1"));
        assertTrue(obj4.containsKey("nullField"));
        assertFalse(obj4.containsKey("key"));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyObj.containsKey(null);

                return null;
            }
        }, NullPointerException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testContainsValue() throws Exception {
        final JsonObject emptyObj = json.createObjectBuilder().build();

        JsonObject obj1 = json.createObjectBuilder().add("k1", "v1").build();

        JsonObject obj2 = json.createObjectBuilder().add("k2", "v2").build();

        JsonObject obj3 = json.createObjectBuilder()
            .add("k1", "v1")
            .add("k2", "v2")
            .build();

        JsonObject obj4 = json.createObjectBuilder()
            .add("k1", "v1")
            .addNull("nullField")
            .build();

        assertFalse(emptyObj.containsValue(new IgniteJsonString("value")));

        assertTrue(obj1.containsValue(new IgniteJsonString("v1")));
        assertFalse(obj1.containsValue(new IgniteJsonString("v2")));

        assertTrue(obj2.containsValue(new IgniteJsonString("v2")));
        assertFalse(obj2.containsValue(new IgniteJsonString("v1")));

        assertTrue(obj3.containsValue(new IgniteJsonString("v1")));
        assertTrue(obj3.containsValue(new IgniteJsonString("v2")));
        assertFalse(obj3.containsValue(new IgniteJsonString("value")));

        assertTrue(obj4.containsValue(new IgniteJsonString("v1")));
        assertTrue(obj4.containsValue(JsonValue.NULL));
        assertFalse(obj4.containsValue(new IgniteJsonString("value")));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyObj.containsValue(null);

                return null;
            }
        }, NullPointerException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testKeySet() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        JsonObject obj = json.createObjectBuilder()
            .add("k1", "v1")
            .add("k2", "v2")
            .addNull("k3")
            .build();

        assertTrue(emptyObj.keySet().isEmpty());

        assertEquals(F.asSet("k1", "k2", "k3"), obj.keySet());
    }

    /**
     * @throws Exception If failed.
     */
    public void testValues() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        JsonObject obj = json.createObjectBuilder()
            .add("k1", "v1")
            .add("k2", "v2")
            .addNull("k3")
            .build();

        assertTrue(emptyObj.values().isEmpty());

        Set<JsonValue> exp = F.asSet(new IgniteJsonString("v1"), new IgniteJsonString("v2"), JsonValue.NULL);
        assertEquals(exp, new HashSet<>(obj.values()));
    }

    /**
     * @throws Exception If failed.
     */
    public void testEntrySet() throws Exception {
        JsonObject emptyObj = json.createObjectBuilder().build();

        JsonObject obj = json.createObjectBuilder()
            .add("k1", "v1")
            .add("k2", "v2")
            .addNull("k3")
            .build();

        assertTrue(emptyObj.entrySet().isEmpty());

        Set<Map.Entry<String, JsonValue>> exp = new HashSet<>();
        exp.add(new AbstractMap.SimpleImmutableEntry<String, JsonValue>("k1", new IgniteJsonString("v1")));
        exp.add(new AbstractMap.SimpleImmutableEntry<String, JsonValue>("k2", new IgniteJsonString("v2")));
        exp.add(new AbstractMap.SimpleImmutableEntry<>("k3", JsonValue.NULL));

        assertEquals(exp, obj.entrySet());
    }
}