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
package org.apache.ignite.internal.processors.cache.database;

import org.apache.ignite.PersistentStoreMetrics;
import org.apache.ignite.internal.processors.cache.ratemetrics.HitRateMetrics;

/**
 *
 */
public class PersistentStoreMetricsImpl implements PersistentStoreMetrics {
    /** */
    private volatile long lastWalFsyncTime;

    /** */
    private volatile HitRateMetrics walLoggingRate = new HitRateMetrics(60_000, 5);

    private volatile long rateTimeInterval;

    /** */
    private volatile boolean metricsEnabled;

    /** {@inheritDoc} */
    @Override public float getWalLoggingRate() {
        if (!metricsEnabled)
            return 0;

        return ((float) walLoggingRate.getRate()) / rateTimeInterval;
    }

    /** {@inheritDoc} */
    @Override public int getWalArchiveSegments() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override public float getWalFsyncTime() {
        if (!metricsEnabled)
            return 0;

        return lastWalFsyncTime;
    }

    public void setLastWalFsyncTime(long lastWalFsyncTime) {
        if (metricsEnabled)
            this.lastWalFsyncTime = lastWalFsyncTime;
    }

    /** {@inheritDoc} */
    @Override public float getCheckpointingTime() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override public float getCheckpointingFsyncTime() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override public long getCheckpointingTotalPagesNumber() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override public long[] getCheckpointingPagesByTypeNumber() {
        return new long[0];
    }

    /** {@inheritDoc} */
    @Override public long getCheckpointingCopiedOnWritePagesNumber() {
        return 0;
    }

    /**
     * @return {@code true} if collecting metrics is enabled.
     */
    public boolean metricsEnabled() {
        return metricsEnabled;
    }
}
