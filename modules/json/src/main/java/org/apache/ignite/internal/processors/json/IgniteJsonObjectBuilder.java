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

import org.apache.ignite.internal.util.typedef.internal.*;

import javax.json.*;
import java.math.*;
import java.util.*;

/**
 * Json object builder implementation.
 */
public class IgniteJsonObjectBuilder implements JsonObjectBuilder {
    /** Json object map. */
    private Map<String, JsonValue> jsonMap = new HashMap<>();

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, JsonValue val) {
        A.notNull(name, "key", val, "value");

        jsonMap.put(name, val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, String val) {
        A.notNull(name, "key", val, "value");

        jsonMap.put(name, new IgniteJsonString(val));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, BigInteger val) {
        A.notNull(name, "key", val, "value");

        //TODO: optimize for value
        jsonMap.put(name, new IgniteJsonNumber(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, BigDecimal val) {
        A.notNull(name, "key", val, "value");

        //TODO: optimize for value
        jsonMap.put(name, new IgniteJsonNumber(val));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, int val) {
        A.notNull(name, "key");

        //TODO: optimize for value
        jsonMap.put(name, new IgniteJsonNumber(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, long val) {
        A.notNull(name, "key");

            //TODO: optimize for value
            jsonMap.put(name, new IgniteJsonNumber(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, double val) {
        A.notNull(name, "key");

        //TODO: optimize for value
        jsonMap.put(name, new IgniteJsonNumber(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, boolean val) {
        A.notNull(name, "key");

        jsonMap.put(name, val ? JsonValue.TRUE : JsonValue.FALSE);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder addNull(String name) {
        A.notNull(name, "key");

        jsonMap.put(name, JsonValue.NULL);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, JsonObjectBuilder bld) {
        A.notNull(name, "key", bld, "value");

        jsonMap.put(name, bld.build());

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder add(String name, JsonArrayBuilder bld) {
        A.notNull(name, "key", bld, "value");

        jsonMap.put(name, bld.build());

        return this;
    }

    /** {@inheritDoc} */
    @Override public javax.json.JsonObject build() {
        return new IgniteJsonObject(jsonMap);
    }
}
