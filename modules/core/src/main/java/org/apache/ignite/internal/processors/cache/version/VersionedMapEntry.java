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

package org.apache.ignite.internal.processors.cache.version;

import org.apache.ignite.lang.IgniteBiTuple;
import org.jetbrains.annotations.Nullable;

/**
 * {@link IgniteBiTuple} implementation that implements {@link GridCacheVersionAware} interface.
 *
 * @param <K> Entry key type.
 * @param <V> Entry value type.
 */
public class VersionedMapEntry<K, V> extends IgniteBiTuple<K, V> implements GridCacheVersionAware {
    /** Entry version. */
    private final GridCacheVersion ver;

    /**
     * Constructor.
     *
     * @param val1 Entry key.
     * @param val2 Entry value.
     * @param ver Entry version.
     */
    public VersionedMapEntry(@Nullable K val1, @Nullable V val2, GridCacheVersion ver) {
        super(val1, val2);
        this.ver = ver;
    }

    /** {@inheritDoc} */
    @Override public GridCacheVersion version() {
        return ver;
    }
}
