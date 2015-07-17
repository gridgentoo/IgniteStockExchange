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

import javax.json.*;
import javax.json.spi.*;
import javax.json.stream.*;
import java.io.*;
import java.util.*;

/**
 * Json provider implementation.
 */
public class IgniteJsonProvider extends JsonProvider {
    /** {@inheritDoc} */
    @Override public JsonParser createParser(Reader reader) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonParser createParser(InputStream in) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonParserFactory createParserFactory(Map<String, ?> config) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator createGenerator(Writer writer) {
        return new IgniteJsonGenerator(writer);
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator createGenerator(OutputStream out) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonReader createReader(Reader reader) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonReader createReader(InputStream in) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonWriter createWriter(Writer writer) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonWriter createWriter(OutputStream out) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonWriterFactory createWriterFactory(Map<String, ?> config) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonReaderFactory createReaderFactory(Map<String, ?> config) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public JsonObjectBuilder createObjectBuilder() {
        return new IgniteJsonObjectBuilder();
    }

    /** {@inheritDoc} */
    @Override public JsonArrayBuilder createArrayBuilder() {
        return new IgniteJsonArrayBuilder();
    }

    /** {@inheritDoc} */
    @Override public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
        return null;
    }
}
