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

package org.apache.ignite;

import org.apache.ignite.internal.util.typedef.internal.*;

import javax.json.*;
import java.math.*;
import java.util.*;

/**
 * Json array builder.
 */
public class JsonArrayBuilderImpl implements JsonArrayBuilder {
    /** Json array list. */
    private List<JsonValue> jsonList = new ArrayList<>();

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(JsonValue val) {
        A.notNull(val, "value");

        jsonList.add(val);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(String val) {
        A.notNull(val, "value");

        jsonList.add(new JsonStringImpl(val));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(BigDecimal val) {
        A.notNull(val, "value");

        jsonList.add(new JsonNumberImpl(val));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(BigInteger val) {
        A.notNull(val, "value");

        //TODO: optimize for value
        jsonList.add(new JsonNumberImpl(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(int val) {
        //TODO: optimize for value
        jsonList.add(new JsonNumberImpl(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(long val) {
        //TODO: optimize for value
        jsonList.add(new JsonNumberImpl(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(double val) {
        //TODO: optimize for value
        jsonList.add(new JsonNumberImpl(new BigDecimal(val)));

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(boolean val) {
        jsonList.add(val ? JsonValue.TRUE : JsonValue.FALSE);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder addNull() {
        jsonList.add(JsonValue.NULL);

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(JsonObjectBuilder bld) {
        A.notNull(bld, "value");

        jsonList.add(bld.build());

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder add(JsonArrayBuilder bld) {
        A.notNull(bld, "value");

        jsonList.add(bld.build());

        return this;
    }

    /** {@inheritDoc} */
    @Override public JsonArray build() {
        return new JsonArrayImpl(jsonList);
    }
}
