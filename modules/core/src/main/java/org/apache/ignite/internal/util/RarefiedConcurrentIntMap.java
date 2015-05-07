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

package org.apache.ignite.internal.util;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 *
 */
public class RarefiedConcurrentIntMap<T> implements Iterable<T> {
    /** */
    private final AtomicReferenceArray<T> arr;

    /** */
    private final int maxIdx;

    /**
     * @param maxIdx Max element index.
     */
    public RarefiedConcurrentIntMap(int maxIdx) {
        arr = new AtomicReferenceArray<T>(maxIdx);

        this.maxIdx = maxIdx;
    }

    /** {@inheritDoc} */
    @Override public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int idx;

            private T next;

            private T lastReturned;

            private int lastReturnedIdx;

            private void advance() {
                while (next == null && idx < maxIdx)
                    next = arr.get(idx++);
            }

            @Override public boolean hasNext() {
                advance();

                return next != null;
            }

            @Override public T next() {
                advance();

                if (next == null)
                    throw new NoSuchElementException();

                lastReturned = next;
                lastReturnedIdx = idx - 1;

                next = null;

                return lastReturned;
            }

            @Override public void remove() {
                if (lastReturned == null)
                    throw new IllegalStateException();

                arr.compareAndSet(lastReturnedIdx, lastReturned, null);

                lastReturned = null;
            }
        };
    }

    /**
     * @param idx Index.
     */
    public T get(int idx) {
        return arr.get(idx);
    }

    /**
     *
     */
    public int maxIndex() {
        return maxIdx;
    }

    /**
     * @param idx Index.
     * @param expVal Expected value.
     */
    public boolean remove(int idx, T expVal) {
        return arr.compareAndSet(idx, expVal, null);
    }

    /**
     * @param idx Index.
     * @param val Value.
     */
    public T putIfAbsent(int idx, T val) {
        while (true) {
            if (arr.compareAndSet(idx, null, val))
                return null;

            T res = arr.get(idx);

            if (res != null)
                return res;
        }
    }

    /**
     * @param c Closure.
     */
    public void addAllTo(Collection<? super T> c) {
        for (int i = 0; i < maxIdx; i++) {
            T e = arr.get(i);

            if (e != null)
                c.add(e);
        }
    }
}
