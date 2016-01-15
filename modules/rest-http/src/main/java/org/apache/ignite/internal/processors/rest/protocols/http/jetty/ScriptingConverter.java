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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.cache.Cache;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.processors.cache.CacheEntryImpl;
import org.apache.ignite.internal.processors.scripting.IgniteScriptingConverter;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.json.IgniteJson;

/**
 * Converter for glassfish objects.
 */
@SuppressWarnings("unused")
public class ScriptingConverter extends IgniteScriptingConverter {
    /** JSON provider */
    private final JsonProvider provider;

    /** */
    private Class<?> scriptObjCls;

    /**
     * @param ctx Grid context.
     */
    public ScriptingConverter(GridKernalContext ctx) {
        provider = IgniteJson.jsonProvider(ctx.grid());

        try {
            scriptObjCls = Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");
        }
        catch(ClassNotFoundException e1) {
            try {
                scriptObjCls = Class.forName("sun.org.mozilla.javascript.internal.NativeObject");
            }
            catch (ClassNotFoundException e2) {
                // Ignore.
            }
        }
    }

    /**
     * @param o Object.
     * @return {@code True} if object is script engine wrapper.
     */
    private boolean scriptObject(Object o) {
        return o.getClass() == scriptObjCls && (o instanceof Map);
    }

    /**
     * @param o Object.
     * @return Builder.
     */
    @SuppressWarnings("unchecked")
    private JsonObjectBuilder toBuilder(Object o) {
        assert scriptObject(o) : o;

        JsonObjectBuilder builder = provider.createObjectBuilder();

        Map<Object, Object> map = (Map<Object, Object>)o;

        for (Map.Entry<Object, Object> e : map.entrySet()) {
            String name = e.getKey().toString();

            Object val = e.getValue();

            if (scriptObject(val))
                builder.add(name, toBuilder(val));
            else
                builder.add(name, val.toString());
        }

        return builder;
    }

    /** {@inheritDoc} */
    @Override public Object toJavaObject(Object o) {
        if (o == null)
            return null;

        // TODO IGNITE-961.
        if (scriptObject(o))
            return toBuilder(o).build();
        else if (o instanceof Collection) {
            Collection col = (Collection)o;

            List<Object> res = new ArrayList<>(col.size());

            for (Object o1 : col)
                res.add(toJavaObject(o1));

            return res;
        }

        return o;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override public Object toScriptObject(Object o) {
        if (o == null)
            return null;

        if (o instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>)o;

            Map<Object, Object> res = U.newHashMap(map.size());

            for (Map.Entry<Object, Object> e : map.entrySet())
                res.put(toScriptObject(e.getKey()), toScriptObject(e.getValue()));

            return res;
        }
        else if (o instanceof List) {
            List list = (List) o;

            List<Object> res = new ArrayList<>(list.size());

            for (Object v : list)
                res.add(toScriptObject(v));

            return res;
        }
        else if (o instanceof Cache.Entry)
            return new CacheEntryImpl<>(toScriptObject(((Cache.Entry)o).getKey()),
                toScriptObject(((Cache.Entry)o).getValue()));
        else if (o instanceof JsonString)
            return ((JsonString)o).getString();
        else if (o instanceof JsonNumber)
            return ((JsonNumber)o).intValue();
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
