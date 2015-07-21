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

package org.apache.ignite.internal.processors.rest.protocols.http.jetty;

import net.sf.json.*;
import org.apache.ignite.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.cache.*;
import org.apache.ignite.internal.processors.json.*;
import org.apache.ignite.internal.processors.scripting.*;
import org.apache.ignite.json.*;

import javax.cache.*;
import javax.json.*;
import javax.json.spi.*;
import java.math.*;
import java.util.*;

/**
 * Converter for glassfish objects.
 */
public class RestGlassFishScriptingConverter extends IgniteScriptingConverter {
    /** Grid kernal context. */
    GridKernalContext ctx;

    /**
     * @param ctx Grid context.
     */
    public RestGlassFishScriptingConverter(GridKernalContext ctx) {
        this.ctx = ctx;
    }

    /** {@inheritDoc} */
    @Override public JsonValue toJavaObject(Object o) {
        if (o == null)
            return null;

        if (o instanceof Map) {
            Map o1 = (Map)o;

            JsonProvider provider = IgniteJson.jsonProvider(ctx.grid());

            JsonObjectBuilder bld = provider.createObjectBuilder();

            for (Object key : o1.keySet()) {
                assert (key instanceof String) || (key instanceof JSONString);

                if (key instanceof JSONString)
                    bld.add(((JsonString) key).getString(), toJavaObject(o1.get(key)));
                else
                    bld.add((String)key, toJavaObject(o1.get(key)));
            }

            return bld.build();
        }
        else if (o instanceof List) {
            List o1 = (List) o;

            JsonProvider provider = IgniteJson.jsonProvider(ctx.grid());

            JsonArrayBuilder bld = provider.createArrayBuilder();

            for (Object v : o1)
                bld.add(toJavaObject(v));

            return bld.build();
        }
        else if (o instanceof JsonString)
            return new IgniteJsonString(((JsonString) o).getString());
        else if (o instanceof JsonNumber)
            return new IgniteJsonNumber(((JsonNumber) o).bigDecimalValue());
        else if (o.equals(JsonValue.FALSE))
            return JsonValue.FALSE;
        else if (o.equals(JsonValue.TRUE))
            return JsonValue.TRUE;
        else if (o.equals(JsonValue.NULL))
            return JsonValue.NULL;
        else if (o instanceof String)
            return new IgniteJsonString((String)o);
        else if (o instanceof Integer)
            return new IgniteJsonNumber(new BigDecimal((Integer)o));
        else if (o instanceof Long)
            return new IgniteJsonNumber(new BigDecimal((Long)o));
        else if (o instanceof Double)
            return new IgniteJsonNumber(new BigDecimal((Double)o));

        throw new IgniteException("Do not support type: " + o.getClass());
    }

    /** {@inheritDoc} */
    @Override public Object toScriptObject(Object o) {
        if (o == null)
            return null;

        if (o instanceof Map) {
            Map o1 = (Map)o;

            Map<Object, Object> res = new HashMap<>();

            for (Object key : o1.keySet())
                res.put(toScriptObject(key), toScriptObject(o1.get(key)));

            return res;
        }
        else if (o instanceof List) {
            List o1 = (List) o;

            List<Object> res = new ArrayList<>();

            for (Object v : o1)
                res.add(toScriptObject(v));

            return res;
        }
        else if (o instanceof Cache.Entry)
            return new CacheEntryImpl<>(toScriptObject(((Cache.Entry) o).getKey()),
                toScriptObject(((Cache.Entry) o).getValue()));
        else if (o instanceof JsonString)
            return ((JsonString) o).getString();
        else if (o instanceof JsonNumber)
            return ((JsonNumber) o).intValue();
        else if (o.equals(JsonValue.FALSE))
            return false;
        else if (o.equals(JsonValue.TRUE))
            return true;
        else if (o.equals(JsonValue.NULL))
            return null;

        return o;
    }


    /** {@inheritDoc} */
    @Override public Object getField(String key, Object o) {
        if (o instanceof JsonObject)
            return ((JsonObject)o).get(key);

        return null;
    }
}
