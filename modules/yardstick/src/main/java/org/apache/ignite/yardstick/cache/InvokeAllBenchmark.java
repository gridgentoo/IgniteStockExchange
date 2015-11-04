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

package org.apache.ignite.yardstick.cache;

import java.util.HashMap;
import java.util.Map;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheEntryProcessor;

/**
 * Ignite benchmark that performs put operations.
 */
public class InvokeAllBenchmark extends IgniteCacheAbstractBenchmark<String, Object> {
    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        Map<String, EntryProcessor<String, Object, Object>> map = new HashMap<>(args.batch());

        for (int i = 0; i < args.batch(); i++) {
            String key = "key" + nextRandom(args.range());

            map.put(key, new EP(new byte[500]));
        }

        cache.invokeAll(map);

        return true;
    }

    /** {@inheritDoc} */
    @Override protected IgniteCache<String, Object> cache() {
        return ignite().cache("atomic");
    }

    private static class EP implements CacheEntryProcessor<String, Object, Object> {
        private final byte[] payload;

        public EP(byte[] payload) {
            this.payload = payload;
        }

        @Override public Object process(MutableEntry<String, Object> e, Object... args) throws EntryProcessorException {
            for (long i = 0; i < 1000000; i++);

            return null;
        }
    }
}
