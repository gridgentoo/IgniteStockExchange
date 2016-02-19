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
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.apache.ignite.internal.util.typedef.internal.A;

/**
 * Json array builder.
 */
public class IgniteJsonArrayBuilder implements JsonArrayBuilder {
    /** Values list. */
    private List<Object> list = new ArrayList<>();

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(JsonValue val) {
        A.notNull(val, "value");

        JsonValue.ValueType valType = val.getValueType();

        switch (valType) {
            case ARRAY:
                list.add(((IgniteJsonArray)val).list());

                break;

            case OBJECT:
                list.add(((IgniteJsonObject)val).binaryObject());

                break;
            case STRING:
                list.add(((JsonString)val).getString());

                break;
            case NUMBER:
                //TODO: Optimize for value
                list.add(((JsonNumber)val).bigDecimalValue());

                break;
            case TRUE:
                list.add(true);

                break;
            case FALSE:
                list.add(false);

                break;
            case NULL:
                list.add(null);

                break;
            default:
                throw new IllegalArgumentException("Unknown value type " + valType);
        }

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(String val) {
        A.notNull(val, "val");

        list.add(val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(BigDecimal val) {
        A.notNull(val, "val");

        list.add(val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(BigInteger val) {
        A.notNull(val, "val");

        list.add(val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(int val) {
        list.add(val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(long val) {
        list.add(val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(double val) {
        list.add(val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(boolean val) {
        list.add(val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder addNull() {
        list.add(null);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(JsonObjectBuilder bld) {
        A.notNull(bld, "bld");

        list.add(((IgniteJsonObject)bld.build()).binaryObject());

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(JsonArrayBuilder bld) {
        A.notNull(bld, "bld");

        list.add(((IgniteJsonArrayBuilder)bld).list());

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArray build() {
          return new IgniteJsonArray(list);
    }

    /**
     * Returns backing objects list.
     *
     * @return backing objects list.
     */
    List<Object> list() {
        return list;
    }
}
