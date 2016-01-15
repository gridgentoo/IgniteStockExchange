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
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.apache.ignite.internal.util.typedef.internal.A;

/**
 * JSON object builder implementation.
 */
public class IgniteJsonObjectBuilder implements JsonObjectBuilder {
    /** JSON object map. */
    private Map<String, JsonValue> jsonMap = new HashMap<>();

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, JsonValue val) {
        A.notNull(name, "name", val, "val");

        jsonMap.put(name, val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, String val) {
        A.notNull(name, "name", val, "val");

        jsonMap.put(name, new IgniteJsonString(val));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, BigInteger val) {
        A.notNull(name, "name", val, "val");

        //TODO: optimize for value
        jsonMap.put(name, new IgniteJsonNumber(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, BigDecimal val) {
        A.notNull(name, "name", val, "val");

        //TODO: optimize for value
        jsonMap.put(name, new IgniteJsonNumber(val));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, int val) {
        A.notNull(name, "name");

        //TODO: optimize for value
        jsonMap.put(name, new IgniteJsonNumber(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, long val) {
        A.notNull(name, "name");

        //TODO: optimize for value
        jsonMap.put(name, new IgniteJsonNumber(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, double val) {
        A.notNull(name, "name");

        //TODO: optimize for value
        jsonMap.put(name, new IgniteJsonNumber(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, boolean val) {
        A.notNull(name, "name");

        jsonMap.put(name, val ? JsonValue.TRUE : JsonValue.FALSE);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder addNull(String name) {
        A.notNull(name, "name");

        jsonMap.put(name, JsonValue.NULL);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
        A.notNull(name, "name", builder, "builder");

        jsonMap.put(name, builder.build());

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
        A.notNull(name, "name", builder, "builder");

        jsonMap.put(name, builder.build());

        return this;
    }

    /** {@inheritDoc} */
    @Override public javax.json.JsonObject build() {
        return new IgniteJsonObject(jsonMap);
    }
}
