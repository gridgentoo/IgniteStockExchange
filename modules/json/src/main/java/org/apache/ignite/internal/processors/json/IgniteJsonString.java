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
import java.io.*;

/**
 * Json string implementation.
 */
public class IgniteJsonString implements JsonString, Serializable {
    /** Value. */
    private final String val;

    /**
     * @param val Value.
     */
    public IgniteJsonString(String val) {
        this.val = val;
    }

    /** {@inheritDoc} */
    @Override public String getString() {
        return val;
    }

    /** {@inheritDoc} */
    @Override public CharSequence getChars() {
        return val;
    }

    /** {@inheritDoc} */
    @Override public ValueType getValueType() {
        return ValueType.STRING;
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        if (val == null)
            return 0;

        return val.hashCode();
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JsonString))
            return false;

        JsonString other = (JsonString)obj;

        if (val == null)
            return other.getString() == null;

        return val.equals(other.getString());
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return val;
    }
}
