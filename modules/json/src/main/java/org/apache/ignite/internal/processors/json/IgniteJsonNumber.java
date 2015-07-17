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
import java.math.*;

/**
 * Json number implementation.
 *
 * TODO IGNITE-962: optimize for int, long, double.
 */
public class IgniteJsonNumber implements JsonNumber, Serializable {
    /** Value. */
    private final BigDecimal val;

    /**
     * @param val Value.
     */
    public IgniteJsonNumber(BigDecimal val){
        this.val = val;
    }

    /** {@inheritDoc} */
    @Override public boolean isIntegral() {
        return val != null && val.scale() == 0;

    }

    /** {@inheritDoc} */
    @Override public int intValue() {
        return val.intValue();
    }

    /** {@inheritDoc} */
    @Override public int intValueExact() {
        return val.intValueExact();
    }

    /** {@inheritDoc} */
    @Override public long longValue() {
        return val.longValue();
    }

    /** {@inheritDoc} */
    @Override public long longValueExact() {
        return val.longValueExact();
    }

    /** {@inheritDoc} */
    @Override public BigInteger bigIntegerValue() {
        return val.toBigInteger();
    }

    /** {@inheritDoc} */
    @Override public BigInteger bigIntegerValueExact() {
        return val.toBigIntegerExact();
    }

    /** {@inheritDoc} */
    @Override public double doubleValue() {
        return val.doubleValue();
    }

    /** {@inheritDoc} */
    @Override public BigDecimal bigDecimalValue() {
        return val;
    }

    /** {@inheritDoc} */
    @Override public ValueType getValueType() {
        return ValueType.NUMBER;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return val.toString();
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        if (val == null)
            return 0;

        return val.hashCode();
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof IgniteJsonNumber))
            return false;

        BigDecimal val0 = ((IgniteJsonNumber)obj).val;

        if (val == null)
            return val0 == null;

        return val.equals(val0);
    }
}
