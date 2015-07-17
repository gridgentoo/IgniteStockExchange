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

import javax.json.stream.*;

/**
 * Json location implementation.
 */
public class IgniteJsonLocation implements JsonLocation {
    /** Column number. */
    private final long col;

    /** Line number. */
    private final long line;

    /** Stream offset. */
    private final long off;

    /**
     * @param line Line number.
     * @param col Column number.
     * @param streamOff Stream offset.
     */
    IgniteJsonLocation(long line, long col, long streamOff) {
        this.line = line;
        this.col = col;
        this.off = streamOff;
    }

    /** {@inheritDoc} */
    @Override public long getLineNumber() {
        return line;
    }

    /** {@inheritDoc} */
    @Override public long getColumnNumber() {
        return col;
    }

    /** {@inheritDoc} */
    @Override public long getStreamOffset() {
        return off;
    }
}
