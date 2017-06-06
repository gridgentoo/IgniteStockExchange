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

package org.apache.ignite.internal.processors.cache.distributed.dht;

import java.util.Collection;
import org.apache.ignite.internal.processors.affinity.AffinityTopologyVersion;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.util.future.GridFinishedFuture;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class ClientCacheDhtTopologyFuture extends GridFinishedFuture<AffinityTopologyVersion>
    implements GridDhtTopologyFuture {
    /**
     * @param topVer Exchange topology version.
     */
    public ClientCacheDhtTopologyFuture(AffinityTopologyVersion topVer) {
        super(topVer);

        assert topVer != null;
    }

    /** {@inheritDoc} */
    @Override public AffinityTopologyVersion topologyVersion() {
        return result();
    }

    /** {@inheritDoc} */
    @Nullable @Override public Throwable validateCache(GridCacheContext cctx,
        boolean recovery,
        boolean read,
        @Nullable Object key,
        @Nullable Collection<?> keys) {
        return null;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return "ClientCacheDhtTopologyFuture [topVer=" + topologyVersion() + ']';
    }
}
