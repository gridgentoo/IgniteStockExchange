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

/**
 *
 */
public class PersistentStoreMetricsSnapshot implements PersistentStoreMetrics {
    /** */
    private float walLoggingRate;

    /** */
    private int walArchiveSegments;

    /** */
    private float walFsyncTime;

    /** */
    private float checkpointingTime;

    /** */
    private float checkpointingFsyncTime;

    /** */
    private long checkpoiningTotalPagesNum;

    /** */
    private long[] checkpointingPagesByTypeNum;

    /** */
    private long checkpointingCopiedOnWritePagesNum;

    /**
     * @param metrics Metrics.
     */
    public PersistentStoreMetricsSnapshot(PersistentStoreMetrics metrics) {
        walLoggingRate = metrics.getWalLoggingRate();
        walArchiveSegments = metrics.getWalArchiveSegments();
        walFsyncTime = metrics.getWalFsyncTime();
        checkpointingTime = metrics.getCheckpointingTime();
        checkpointingFsyncTime = metrics.getCheckpointingFsyncTime();
        checkpoiningTotalPagesNum = metrics.getCheckpointingTotalPagesNumber();
        checkpointingPagesByTypeNum = metrics.getCheckpointingPagesByTypeNumber();
        checkpointingCopiedOnWritePagesNum = metrics.getCheckpointingCopiedOnWritePagesNumber();
    }

    /**
     *
     */
    public float getWalLoggingRate() {
        return walLoggingRate;
    }

    /**
     *
     */
    public int getWalArchiveSegments() {
        return walArchiveSegments;
    }

    /**
     *
     */
    public float getWalFsyncTime() {
        return walFsyncTime;
    }

    /**
     *
     */
    public float getCheckpointingTime() {
        return checkpointingTime;
    }

    /**
     *
     */
    public float getCheckpointingFsyncTime() {
        return checkpointingFsyncTime;
    }

    /**
     *
     */
    public long getCheckpointingTotalPagesNumber() {
        return checkpoiningTotalPagesNum;
    }

    /**
     *
     */
    public long[] getCheckpointingPagesByTypeNumber() {
        return checkpointingPagesByTypeNum;
    }

    /**
     *
     */
    public long getCheckpointingCopiedOnWritePagesNumber() {
        return checkpointingCopiedOnWritePagesNum;
    }
}
