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

import org.apache.ignite.lang.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 *
 */
@SuppressWarnings("PublicInnerClass")
public class IgniteIterators {
    /**
     * Private constructor.
     */
    private IgniteIterators() {
        // No-op.
    }

    /**
     * @param itr Itr.
     * @param trans Trans.
     */
    public static <T1, T2> Iterator<T2> transform(@NotNull Iterator<T1> itr,
        @NotNull IgniteClosure<? super T1, T2> trans) {
        return new TransformIterator<>(itr, trans);
    }

    /**
     *
     */
    public static class TransformIterator<T1, T2> implements Iterator<T2> {
        /** */
        private final Iterator<T1> src;

        /** */
        private final IgniteClosure<? super T1, T2> trans;

        /**
         * @param src Source.
         * @param trans Trans.
         */
        public TransformIterator(Iterator<T1> src, IgniteClosure<? super T1, T2> trans) {
            this.src = src;
            this.trans = trans;
        }

        /** {@inheritDoc} */
        @Override public boolean hasNext() {
            return src.hasNext();
        }

        /** {@inheritDoc} */
        @Override public T2 next() {
            T1 res = src.next();

            return trans.apply(res);
        }

        /** {@inheritDoc} */
        @Override public void remove() {
            src.remove();
        }
    }
}
