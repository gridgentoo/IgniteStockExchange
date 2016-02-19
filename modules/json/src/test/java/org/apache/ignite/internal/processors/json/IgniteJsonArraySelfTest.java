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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import javax.json.JsonArray;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.json.IgniteJson;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;

/**
 * Tests for {@link IgniteJsonArray} implementation.
 */
public class IgniteJsonArraySelfTest extends GridCommonAbstractTest {
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
        final JsonArray emptyArr = json.createArrayBuilder().build();

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyArr.isNull(0);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        final JsonArray arr = json.createArrayBuilder()
            .add(42)
            .addNull()
            .build();

        assertFalse(arr.isNull(0));
        assertTrue(arr.isNull(1));

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.isNull(-1);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.isNull(arr.size());

                return null;
            }
        }, IndexOutOfBoundsException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetBoolean() throws Exception {
        final JsonArray emptyArr = json.createArrayBuilder().build();

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyArr.getBoolean(0);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        final JsonArray arr = json.createArrayBuilder()
            .add(true)
            .add(false)
            .add(42)
            .addNull()
            .build();

        assertTrue(arr.getBoolean(0));
        assertFalse(arr.getBoolean(1));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getBoolean(2);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getBoolean(3);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getBoolean(-1);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getBoolean(arr.size());

                return null;
            }
        }, IndexOutOfBoundsException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetBooleanWithDefault() throws Exception {
        JsonArray emptyArr = json.createArrayBuilder().build();

        assertTrue(emptyArr.getBoolean(0, true));

        final JsonArray arr = json.createArrayBuilder()
            .add(true)
            .add(false)
            .add(42)
            .addNull()
            .build();

        assertTrue(arr.getBoolean(0, false));
        assertFalse(arr.getBoolean(1, true));
        assertTrue(arr.getBoolean(2, true));
        assertTrue(arr.getBoolean(3, true));
        assertTrue(arr.getBoolean(arr.size(), true));
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetInt() throws Exception {
        final JsonArray emptyArr = json.createArrayBuilder().build();

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyArr.getInt(0);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        final JsonArray arr = json.createArrayBuilder()
            .add(42)
            .add("string")
            .addNull()
            .build();

        assertEquals(42, arr.getInt(0));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getInt(1);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getInt(2);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getInt(-1);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getInt(arr.size());

                return null;
            }
        }, IndexOutOfBoundsException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetIntWithDefault() throws Exception {
        JsonArray emptyArr = json.createArrayBuilder().build();

        assertEquals(42, emptyArr.getInt(0, 42));

        final JsonArray arr = json.createArrayBuilder()
            .add(42)
            .add("string")
            .addNull()
            .build();

        assertEquals(42, arr.getInt(0, 666));
        assertEquals(51, arr.getInt(1, 51));
        assertEquals(51, arr.getInt(2, 51));
        assertEquals(51, arr.getInt(arr.size(), 51));
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetString() throws Exception {
        final JsonArray emptyArr = json.createArrayBuilder().build();

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyArr.getString(0);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        final JsonArray arr = json.createArrayBuilder()
            .add("string")
            .add(42)
            .addNull()
            .build();

        assertEquals("string", arr.getString(0));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getString(1);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getString(2);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getString(-1);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getString(arr.size());

                return null;
            }
        }, IndexOutOfBoundsException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetStringWithDefault() throws Exception {
        JsonArray emptyArr = json.createArrayBuilder().build();

        assertEquals("default", emptyArr.getString(0, "default"));

        final JsonArray arr = json.createArrayBuilder()
            .add("string")
            .add(42)
            .addNull()
            .build();

        assertEquals("string", arr.getString(0, "default"));
        assertEquals("default", arr.getString(1, "default"));
        assertEquals("default", arr.getString(2, "default"));
        assertEquals("default", arr.getString(arr.size(), "default"));
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetJsonNumber() throws Exception {
        final JsonArray emptyArr = json.createArrayBuilder().build();

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyArr.getJsonNumber(0);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        final JsonArray arr = json.createArrayBuilder()
            .add(42)
            .add("string")
            .addNull()
            .build();

        assertEquals(new IgniteJsonNumber(new BigDecimal(42)), arr.getJsonNumber(0));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonNumber(1);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonNumber(2);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonNumber(-1);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonNumber(arr.size());

                return null;
            }
        }, IndexOutOfBoundsException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetJsonString() throws Exception {
        final JsonArray emptyArr = json.createArrayBuilder().build();

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyArr.getJsonString(0);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        final JsonArray arr = json.createArrayBuilder()
            .add("string")
            .add(42)
            .addNull()
            .build();

        assertEquals(new IgniteJsonString("string"), arr.getJsonString(0));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonString(1);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonString(2);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonString(-1);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonString(arr.size());

                return null;
            }
        }, IndexOutOfBoundsException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetJsonObject() throws Exception {
        final JsonArray emptyArr = json.createArrayBuilder().build();

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                emptyArr.getJsonObject(0);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        final JsonArray arr = json.createArrayBuilder()
            .add(json.createObjectBuilder().add("key", "val").build())
            .add(42)
            .addNull()
            .build();

        assertEquals(json.createObjectBuilder().add("key", "val").build(), arr.getJsonObject(0));

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonObject(1);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonObject(2);

                return null;
            }
        }, ClassCastException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonObject(-1);

                return null;
            }
        }, IndexOutOfBoundsException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                arr.getJsonObject(arr.size());

                return null;
            }
        }, IndexOutOfBoundsException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testGetValuesAs() throws Exception {
        JsonArray emptyArr = json.createArrayBuilder().build();

        assertTrue(emptyArr.getValuesAs(JsonString.class).isEmpty());

        JsonArray strArr = json.createArrayBuilder().add("1").add("2").build();

        List<JsonString> expStrs = F.<JsonString>asList(new IgniteJsonString("1"), new IgniteJsonString("2"));
        assertEquals(expStrs, strArr.getValuesAs(JsonString.class));

        JsonArray arr = json.createArrayBuilder().add(1).add("2").build();

        final List<JsonString> actStrs = arr.getValuesAs(JsonString.class);

        GridTestUtils.assertThrows(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                for (JsonString ignored : actStrs) {
                    // No-op.
                }

                return null;
            }
        }, ClassCastException.class, null);
    }

    /**
     * @throws Exception If failed.
     */
    public void testSubList() throws Exception {
        JsonArray strArr = json.createArrayBuilder().add("1").add("2").add("3").add("4").build();

        List<JsonString> expStrs = F.<JsonString>asList(new IgniteJsonString("2"), new IgniteJsonString("3"));
        assertEquals(expStrs, strArr.subList(1, 3));
    }


    /**
     * @throws Exception If failed.
     */
    public void testContains() throws Exception {
        JsonArray arr = json.createArrayBuilder().build();
        assertFalse(arr.contains(JsonValue.TRUE));

        arr = json.createArrayBuilder().add(true).build();
        assertTrue(arr.contains(JsonValue.TRUE));

        arr = json.createArrayBuilder().add(false).build();
        assertTrue(arr.contains(JsonValue.FALSE));

        arr = json.createArrayBuilder().addNull().build();
        assertTrue(arr.contains(JsonValue.NULL));

        arr = json.createArrayBuilder().add("value").build();
        assertTrue(arr.contains(new IgniteJsonString("value")));

        arr = json.createArrayBuilder().add("value").build();
        assertTrue(arr.contains(new IgniteJsonString("value")));

        arr = json.createArrayBuilder().add(1).build();
        assertTrue(arr.contains(new IgniteJsonNumber(new BigDecimal(1L))));

        arr = json.createArrayBuilder().add(1L).build();
        assertTrue(arr.contains(new IgniteJsonNumber(new BigDecimal(1))));

        arr = json.createArrayBuilder().add(1.0).build();
        assertTrue(arr.contains(new IgniteJsonNumber(new BigDecimal(1))));

        arr = json.createArrayBuilder().add(new BigInteger("1")).build();
        assertTrue(arr.contains(new IgniteJsonNumber(new BigDecimal(1))));

        arr = json.createArrayBuilder().add(new BigDecimal(1)).build();
        assertTrue(arr.contains(new IgniteJsonNumber(new BigDecimal(1))));

        arr = json.createArrayBuilder().add(
            json.createArrayBuilder()
                .addNull()
                .add(JsonValue.TRUE)
                .add(JsonValue.FALSE)
                .add("string")
                .add(1)
                .add(1L)
                .add(1.0)
                .add(new BigInteger("1"))
                .add(new BigDecimal(1))
                .build()
        ).build();

        assertTrue(arr.contains(
            json.createArrayBuilder()
                .addNull()
                .add(JsonValue.TRUE)
                .add(JsonValue.FALSE)
                .add("string")
                .add(1)
                .add(1L)
                .add(1.0)
                .add(new BigInteger("1"))
                .add(new BigDecimal(1))
                .build()
        ));

        arr = json.createArrayBuilder().add(
            json.createObjectBuilder()
                .addNull("nullField")
                .add("trueField", true)
                .add("falseField", false)
                .add("strField", "string")
                .add("intField", 1)
                .add("longField", 1L)
                .add("doubleField", 1L)
                .add("bigIntField", new BigInteger("1"))
                .add("bigDecimalField", new BigDecimal(1))
                .build()
        ).build();

        assertTrue(arr.contains(
            json.createObjectBuilder()
                .addNull("nullField")
                .add("trueField", true)
                .add("falseField", false)
                .add("strField", "string")
                .add("intField", 1)
                .add("longField", 1L)
                .add("doubleField", 1L)
                .add("bigIntField", new BigInteger("1"))
                .add("bigDecimalField", new BigDecimal(1))
                .build()
            )
        );
    }

    /**
     * @throws Exception If failed.
     */
    public void testContainsAll() throws Exception {
        JsonArray emptyArr = json.createArrayBuilder().build();

        assertFalse(emptyArr.containsAll(F.asList(JsonValue.TRUE, JsonValue.NULL)));

        JsonArray arr = json.createArrayBuilder()
            .addNull()
            .add(JsonValue.TRUE)
            .add(JsonValue.FALSE)
            .add("string")
            .add(1)
            .add(1L)
            .add(1.0)
            .add(new BigInteger("1"))
            .add(new BigDecimal(1))
            .build();

        assertTrue(arr.containsAll(F.asList(JsonValue.TRUE, JsonValue.NULL, new IgniteJsonString("string"))));
        assertFalse(arr.containsAll(F.asList(JsonValue.TRUE, JsonValue.NULL, new IgniteJsonString("value"))));
    }

    /**
     * @throws Exception If failed.
     */
    public void testToArray() throws Exception {
        JsonArray jsonArr = json.createArrayBuilder()
            .addNull()
            .add(JsonValue.TRUE)
            .add("string")
            .add(1)
            .build();

        Object[] arr = jsonArr.toArray();

        assertEquals(JsonValue.NULL, arr[0]);
        assertEquals(JsonValue.TRUE, arr[1]);
        assertEquals(new IgniteJsonString("string"), arr[2]);
        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), arr[3]);
    }

    /**
     * @throws Exception If failed.
     */
    public void testIterator() throws Exception {
        JsonArray arr = json.createArrayBuilder()
            .addNull()
            .add(JsonValue.TRUE)
            .add("string")
            .add(1)
            .build();

        final Iterator<JsonValue> it = arr.iterator();

        assertTrue(it.hasNext());
        assertEquals(JsonValue.NULL, it.next());

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                it.remove();

                return null;
            }
        }, UnsupportedOperationException.class, null);

        assertTrue(it.hasNext());
        assertEquals(JsonValue.TRUE, it.next());

        assertTrue(it.hasNext());
        assertEquals(new IgniteJsonString("string"), it.next());

        assertTrue(it.hasNext());
        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), it.next());

        assertFalse(it.hasNext());
    }

    /**
     * @throws Exception If failed.
     */
    public void testListIterator() throws Exception {
        JsonArray arr = json.createArrayBuilder()
            .addNull()
            .add(JsonValue.TRUE)
            .add("string")
            .add(1)
            .build();

        final ListIterator<JsonValue> it = arr.listIterator();

        assertTrue(it.hasNext());
        assertEquals(-1, it.previousIndex());
        assertEquals(0, it.nextIndex());
        assertEquals(JsonValue.NULL, it.next());

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                it.remove();

                return null;
            }
        }, UnsupportedOperationException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                it.add(JsonValue.TRUE);

                return null;
            }
        }, UnsupportedOperationException.class, null);

        GridTestUtils.assertThrowsInherited(null, new Callable<Void>() {
            @Override public Void call() throws Exception {
                it.add(JsonValue.TRUE);

                return null;
            }
        }, UnsupportedOperationException.class, null);

        assertTrue(it.hasNext());
        assertEquals(0, it.previousIndex());
        assertEquals(1, it.nextIndex());
        assertEquals(JsonValue.TRUE, it.next());

        assertTrue(it.hasNext());
        assertEquals(1, it.previousIndex());
        assertEquals(2, it.nextIndex());
        assertEquals(new IgniteJsonString("string"), it.next());

        assertTrue(it.hasNext());
        assertEquals(2, it.previousIndex());
        assertEquals(3, it.nextIndex());
        assertEquals(new IgniteJsonNumber(new BigDecimal(1)), it.next());

        assertFalse(it.hasNext());
        assertEquals(3, it.previousIndex());
        assertEquals(4, it.nextIndex());
    }

    /**
     * @throws Exception If failed.
     */
    public void testIndexOf() throws Exception {
        JsonArray arr = json.createArrayBuilder()
            .addNull()
            .add(JsonValue.TRUE)
            .add("string")
            .add(1)
            .addNull()
            .add(JsonValue.TRUE)
            .add("string")
            .add(1)
            .build();

        assertEquals(0, arr.indexOf(JsonValue.NULL));
        assertEquals(1, arr.indexOf(JsonValue.TRUE));
        assertEquals(2, arr.indexOf(new IgniteJsonString("string")));
        assertEquals(3, arr.indexOf(new IgniteJsonNumber(new BigDecimal(1))));
        assertEquals(-1, arr.indexOf(JsonValue.FALSE));
    }

    /**
     * @throws Exception If failed.
     */
    public void testLastIndexOf() throws Exception {
        JsonArray arr = json.createArrayBuilder()
            .addNull()
            .add(JsonValue.TRUE)
            .add("string")
            .add(1)
            .addNull()
            .add(JsonValue.TRUE)
            .add("string")
            .add(1)
            .build();

        assertEquals(4, arr.lastIndexOf(JsonValue.NULL));
        assertEquals(5, arr.lastIndexOf(JsonValue.TRUE));
        assertEquals(6, arr.lastIndexOf(new IgniteJsonString("string")));
        assertEquals(7, arr.lastIndexOf(new IgniteJsonNumber(new BigDecimal(1))));
        assertEquals(-1, arr.lastIndexOf(JsonValue.FALSE));
    }
}