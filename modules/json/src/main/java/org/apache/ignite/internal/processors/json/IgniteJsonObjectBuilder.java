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
import java.util.Map;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.binary.builder.BinaryObjectBuilderImpl;
import org.apache.ignite.internal.util.typedef.internal.A;

/**
 * JSON object builder implementation.
 */
public class IgniteJsonObjectBuilder implements JsonObjectBuilder {
    /** Binary object builder. */
    private final BinaryObjectBuilder binObjBuilder;

    /**
     * @param kernal Kernal.
     */
    public IgniteJsonObjectBuilder(IgniteKernal kernal) {
        this.binObjBuilder = kernal.binary().builder(JsonObject.class.getName());
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, JsonValue val) {
        A.notNull(name, "name", val, "val");

        JsonValue.ValueType valType = val.getValueType();

        switch (valType) {
            case ARRAY:
                binObjBuilder.setField(name, ((IgniteJsonArray)val).list(), Object.class);

                break;
            case OBJECT:
                binObjBuilder.setField(name, ((IgniteJsonObject)val).binaryObject(), Object.class);

                break;
            case STRING:
                add(name, ((JsonString)val).getString());

                break;
            case NUMBER:
                add(name, ((JsonNumber)val).bigDecimalValue());

                break;
            case TRUE:
                add(name, true);

                break;
            case FALSE:
                add(name, false);

                break;
            case NULL:
                addNull(name);

                break;
            default:
                throw new IllegalArgumentException("Unknown value type " + valType);
        }

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, String val) {
        A.notNull(name, "name", val, "val");

        binObjBuilder.setField(name, val, Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, BigInteger val) {
        A.notNull(name, "name", val, "val");

        binObjBuilder.setField(name, val, Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, BigDecimal val) {
        A.notNull(name, "name", val, "val");

        binObjBuilder.setField(name, val, Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, int val) {
        A.notNull(name, "name");

        binObjBuilder.setField(name, val, Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, long val) {
        A.notNull(name, "name");

        binObjBuilder.setField(name, val, Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, double val) {
        A.notNull(name, "name");

        binObjBuilder.setField(name, val, Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, boolean val) {
        A.notNull(name, "name");

        binObjBuilder.setField(name, val, Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder addNull(String name) {
        A.notNull(name, "name");

        binObjBuilder.setField(name, null, Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
        A.notNull(name, "name", builder, "builder");

        binObjBuilder.setField(name, ((IgniteJsonObject)builder.build()).binaryObject(), Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
        A.notNull(name, "name", builder, "builder");

        binObjBuilder.setField(name, ((IgniteJsonArrayBuilder)builder).list(), Object.class);

        return this;
    }

    /** {@inheritDoc} */
    @Override public javax.json.JsonObject build() {
        //TODO: user defined hashCode()
        int h = 0;

        Map<String, Object> assignedVals = ((BinaryObjectBuilderImpl)binObjBuilder).assignedVals();

        if (assignedVals != null) {
            for (Map.Entry<String, Object> e : assignedVals.entrySet())
                h += e.getKey().hashCode() ^ e.getValue().hashCode();
        }

        binObjBuilder.hashCode(h);

        return new IgniteJsonObject(binObjBuilder.build(), assignedVals == null ? 0 : assignedVals.size());
    }
}
