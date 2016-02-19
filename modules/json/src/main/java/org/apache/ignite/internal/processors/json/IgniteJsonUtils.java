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
import java.util.List;
import javax.json.JsonValue;
import org.apache.ignite.binary.BinaryObject;

/**
 * JSON related utils.
 */
public class IgniteJsonUtils {
    /**
     * @param val Value.
     */
    public static JsonValue toJsonValue(Object val) {
        if (val == null)
            return JsonValue.NULL;
        else if (val instanceof Integer)
            return new IgniteJsonNumber(new BigDecimal((Integer)val));
        else if (val instanceof Long)
            return new IgniteJsonNumber(new BigDecimal((Long)val));
        else if (val instanceof Double)
            return new IgniteJsonNumber(new BigDecimal((Double)val));
        else if (val instanceof BigInteger)
            return new IgniteJsonNumber(new BigDecimal((BigInteger)val));
        else if (val instanceof BigDecimal)
            return new IgniteJsonNumber((BigDecimal)val);
        else if (val instanceof String)
            return new IgniteJsonString((String)val);
        else if (val instanceof Boolean)
            return (Boolean)val ? JsonValue.TRUE : JsonValue.FALSE;
        else if (val instanceof BinaryObject)
            return new IgniteJsonObject((BinaryObject)val);
        else if (val instanceof List)
            return new IgniteJsonArray((List<Object>)val);
        else
            throw new IllegalArgumentException("Unknown value type: " + val.getClass().getName());
    }

}
