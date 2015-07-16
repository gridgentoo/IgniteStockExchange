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

import javax.json.*;
import javax.json.stream.*;
import java.io.*;
import java.math.*;
import java.util.*;

/**
 * Json generator implementation.
 */
public class JsonGeneratorImpl implements JsonGenerator {
    /** Writer. */
    private final BufferedWriter writer;

    private LinkedList<Element> ctx = new LinkedList();

    /**
     * @param writer Writer.
     */
    public JsonGeneratorImpl(Writer writer) {
        this.writer = new BufferedWriter(writer);

        ctx.push(new Element(Context.NONE, true));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator writeStartObject() {
        try {
            if (ctx.getLast().context() == Context.OBJECT ||
                (ctx.getLast().context() == Context.NONE && !ctx.getLast().isFirst()))
                throw new JsonGenerationException("No name for object field.");

            writeComma();
            writer.write('{');

            ctx.addLast(new Element(Context.OBJECT, true));

            return this;
        }
        catch (IOException e) {
            throw new JsonException("Writer fails.", e);
        }
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator writeStartObject(String name) {
        try {
            if (ctx.getLast().context() != Context.OBJECT)
                throw new JsonGenerationException("Object with name in not object scope.");

            writeComma();
            writeString(name);
            writer.write(":");
            writer.write('{');

            ctx.addLast(new Element(Context.OBJECT, true));

            return this;
        }
        catch (IOException e) {
            throw new JsonException("Writer fails.", e);
        }
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator writeStartArray() {
        try {
            if (ctx.getLast().context() == Context.OBJECT ||
                (ctx.getLast().context() == Context.NONE && !ctx.getLast().isFirst()))
                throw new JsonGenerationException("Array in object scope.");

            writeComma();
            writer.write("[");

            ctx.addLast(new Element(Context.ARRAY, true));

            return this;
        }
        catch (IOException e) {
            throw new JsonException("Writer fails.", e);
        }
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator writeStartArray(String name) {
        try {
            if (ctx.getLast().context() != Context.OBJECT)
                throw new JsonGenerationException("Array with name in not object scope.");

            writeComma();
            writeString(name);
            writer.write(":");
            writer.write('[');

            ctx.addLast(new Element(Context.ARRAY, true));

            return this;
        }
        catch (IOException e) {
            throw new JsonException("Writer fails.", e);
        }
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(String name, JsonValue val) {
        if (ctx.getLast().context() != Context.OBJECT)
            throw new JsonGenerationException("Json value with name in not object scope.");

        try {
            switch (val.getValueType()) {
                case ARRAY: {
                    JsonArray arr = (JsonArray) val;

                    writeStartArray(name);

                    for (JsonValue el : arr)
                        write(el);

                    writeEnd();

                    break;
                }

                case OBJECT: {
                    JsonObject o = (JsonObject) val;

                    writeStartObject(name);

                    for (Map.Entry<String, JsonValue> member : o.entrySet())
                        write(member.getKey(), member.getValue());

                    writeEnd();

                    break;
                }

                case STRING: {
                    JsonString str = (JsonString) val;

                    write(name, str.getString());

                    break;
                }

                case NUMBER: {
                    JsonNumber n = (JsonNumber) val;

                    writeComma();
                    writeString(name);
                    writer.write(":");
                    writeString(n.toString());

                    break;
                }
                case TRUE: {
                    write(name, true);

                    break;
                }

                case FALSE: {
                    write(name, false);

                    break;
                }

                case NULL: {
                    writeNull(name);

                    break;
                }
            }
            return this;
        }
        catch (IOException e) {
            throw new JsonException("Writer fails.", e);
        }
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(String name, String val) {
        return writeSimpleField(name, val);
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(String name, BigInteger val) {
        return writeSimpleField(name, String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(String name, BigDecimal val) {
        return writeSimpleField(name, String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(String name, int val) {
        return writeSimpleField(name, String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(String name, long val) {
        return writeSimpleField(name, String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(String name, double val) {
        return writeSimpleField(name, String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(String name, boolean val) {
        return writeSimpleField(name, String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator writeNull(String name) {
        return writeSimpleField(name, "null");
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator writeEnd() {
        if (ctx.getLast().context() == Context.NONE)
            throw new JsonGenerationException("Cannot call writeEnd in none context.");

        try {
            if (ctx.getLast().context() == Context.ARRAY)
                writer.write("]");

            if (ctx.getLast().context() == Context.OBJECT)
                writer.write("}");

            ctx.removeLast();

            return this;
        }
        catch(IOException e) {
            throw new JsonException("Writer fails.", e);
        }
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(JsonValue val) {
        if (ctx.getLast().context() != Context.ARRAY)
            throw new JsonGenerationException("Json value without name in not array scope.");

        try {
            switch (val.getValueType()) {
                case ARRAY: {
                    JsonArray arr = (JsonArray) val;

                    writeStartArray();

                    for (JsonValue el : arr)
                        write(el);

                    writeEnd();

                    break;
                }

                case OBJECT: {
                    JsonObject o = (JsonObject) val;

                    writeStartObject();

                    for (Map.Entry<String, JsonValue> member : o.entrySet())
                        write(member.getKey(), member.getValue());

                    writeEnd();

                    break;
                }

                case STRING: {
                    JsonString str = (JsonString) val;

                    write(str.getString());

                    break;
                }

                case NUMBER: {
                    JsonNumber n = (JsonNumber) val;

                    writeComma();
                    writeString(n.toString());

                    break;
                }
                case TRUE: {
                    write(true);

                    break;
                }

                case FALSE: {
                    write(false);

                    break;
                }

                case NULL: {
                    writeNull();

                    break;
                }
            }
            return this;
        }
        catch (IOException e) {
            throw new JsonException("Writer fails.", e);
        }
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(String val) {
        return writeSimpleArrayElement(val);
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(BigDecimal val) {
        return writeSimpleArrayElement(String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(BigInteger val) {
        return writeSimpleArrayElement(String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(int val) {
        return writeSimpleArrayElement(String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(long val) {
        return writeSimpleArrayElement(String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(double val) {
        return writeSimpleArrayElement(String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator write(boolean val) {
        return writeSimpleArrayElement(String.valueOf(val));
    }

    /** {@inheritDoc} */
    @Override public JsonGenerator writeNull() {
        return writeSimpleArrayElement("null");
    }

    /** {@inheritDoc} */
    @Override public void close() {
        try {
            writer.close();
        }
        catch (IOException e) {
            throw new JsonException("Could not close writer.", e);
        }
    }

    /** {@inheritDoc} */
    @Override public void flush() {
        try {
            writer.flush();
        }
        catch (IOException e) {
            throw new JsonException("Could not flush buffer to writer.", e);
        }
    }

    /**
     * Write comma if object is not first.
     *
     * @throws IOException If failed.
     */
    private void writeComma() throws IOException{
        if (!ctx.getLast().isFirst())
            writer.write(",");

        ctx.getLast().isFirst = false;
    }

    /**
     * @param name Field name.
     * @param val Field value.
     */
    private JsonGenerator writeSimpleField(String name, String val) {
        if (ctx.getLast().context() != Context.OBJECT)
            throw new JsonGenerationException("String with name in not object scope.");

        try {
            writeComma();
            writeString(name);
            writer.write(":");
            writeString(val);

            return this;
        }
        catch (IOException e) {
            throw new JsonException("Writer fails.", e);
        }
    }


    /**
     * @param val Field value.
     */
    private JsonGenerator writeSimpleArrayElement(String val) {
        if (ctx.getLast().context() != Context.ARRAY)
            throw new JsonGenerationException("String without name in not array scope.");

        try {
            writeComma();
            writeString(val);

            return this;
        }
        catch (IOException e) {
            throw new JsonException("Writer fails.", e);
        }
    }

    /**
     * @param str String to write.
     * @throws IOException If failed.
     * //TODO: escape string.
     */
    private void writeString(String str) throws IOException {
        writer.write(str);
    }

    /**
     * Generator element.
     */
    private static class Element {
        /** Context. */
        private Context ctx;

        /** First element flag. */
        private boolean isFirst;

        /**
         * @param ctx Context.
         * @param isFirst First element flag.
         */
        public Element(Context ctx, boolean isFirst) {
            this.ctx = ctx;
            this.isFirst = isFirst;
        }

        /**
         * @return First element flag.
         */
        public boolean isFirst() {
            return isFirst;
        }

        /**
         * @return Context.
         */
        public Context context() {
            return ctx;
        }
    }
    /**
     * Context for writer.
     */
    private enum Context {
        /** Writing object. */
        OBJECT,

        /** Writing array. */
        ARRAY,

        /** Not in object or in array. */
        NONE
    }
}
