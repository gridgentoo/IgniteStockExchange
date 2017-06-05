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

import org.apache.ignite.internal.processors.cache.ratemetrics.HitRateMetrics;
import org.apache.ignite.mxbean.PersistenceMetricsMXBean;

/**
 *
 */
public class PersistenceMetricsImpl implements PersistenceMetricsMXBean {
    /** */
    private volatile HitRateMetrics walLoggingRate;

    /** */
    private volatile HitRateMetrics walWritingRate;

    /** */
    private volatile int walArchiveSegments;

    /** */
    private volatile long walFsyncTimeAvg;

    /** */
    private volatile long lastCpDuration;

    /** */
    private volatile long lastCpFsyncDuration;

    /** */
    private volatile long lastCpTotalPages;

    /** */
    private volatile long lastCpDataPages;

    /** */
    private volatile long lastCpIdxPages;

    /** */
    private volatile long lastCpCowPages;

    /** */
    private volatile long rateTimeInterval;

    /** */
    private volatile int subInts;

    /** */
    private volatile boolean metricsEnabled;

    /**
     * @param metricsEnabled Metrics enabled flag.
     * @param rateTimeInterval Rate time interval.
     * @param subInts Number of sub-intervals.
     */
    public PersistenceMetricsImpl(boolean metricsEnabled, long rateTimeInterval, int subInts) {
        this.metricsEnabled = metricsEnabled;
        this.rateTimeInterval = rateTimeInterval;
        this.subInts = subInts;

        resetRates();
    }

    /** {@inheritDoc} */
    @Override public float getWalLoggingRate() {
        if (!metricsEnabled)
            return 0;

        return ((float)walLoggingRate.getRate()) / rateTimeInterval;
    }

    /** {@inheritDoc} */
    @Override public float getWalWritingRate() {
        if (!metricsEnabled)
            return 0;

        return ((float)walWritingRate.getRate()) / rateTimeInterval;
    }

    /** {@inheritDoc} */
    @Override public int getWalArchiveSegments() {
        return 0;
    }

    @Override public float getWalFsyncTimeAverage() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override public long getLastCheckpointingDuration() {
        if (!metricsEnabled)
            return 0;

        return lastCpDuration;
    }

    /** {@inheritDoc} */
    @Override public long getLastCheckpointFsyncDuration() {
        if (!metricsEnabled)
            return 0;

        return lastCpFsyncDuration;
    }

    /** {@inheritDoc} */
    @Override public long getLastCheckpointTotalPagesNumber() {
        if (!metricsEnabled)
            return 0;

        return lastCpTotalPages;
    }

    /** {@inheritDoc} */
    @Override public long getLastCheckpointDataPagesNumber() {
        if (!metricsEnabled)
            return 0;

        return lastCpDataPages;
    }

    /** {@inheritDoc} */
    @Override public long getLastCheckpointIndexPagesNumber() {
        if (!metricsEnabled)
            return 0;

        return lastCpIdxPages;
    }

    /** {@inheritDoc} */
    @Override public long getLastCheckpointCopiedOnWritePagesNumber() {
        if (!metricsEnabled)
            return 0;

        return lastCpCowPages;
    }

    /** {@inheritDoc} */
    @Override public void enableMetrics() {
        metricsEnabled = true;
    }

    /** {@inheritDoc} */
    @Override public void disableMetrics() {
        metricsEnabled = false;
    }

    /** {@inheritDoc} */
    @Override public void rateTimeInterval(long rateTimeInterval) {
        this.rateTimeInterval = rateTimeInterval;

        resetRates();
    }

    /** {@inheritDoc} */
    @Override public void subIntervals(int subInts) {
        this.subInts = subInts;

        resetRates();
    }

    public void onCheckpoint(
        long duration,
        long fsyncDuration,
        long totalPages,
        long dataPages,
        long idxPages,
        long cowPages
    ) {
        if (metricsEnabled) {
            lastCpDuration = duration;
            lastCpFsyncDuration = fsyncDuration;
            lastCpTotalPages = totalPages;
            lastCpDataPages = dataPages;
            lastCpIdxPages = idxPages;
            lastCpCowPages = cowPages;
        }
    }

    /**
     *
     */
    private void resetRates() {
        walLoggingRate = new HitRateMetrics((int)rateTimeInterval, subInts);
        walWritingRate = new HitRateMetrics((int)rateTimeInterval, subInts);
    }
}
