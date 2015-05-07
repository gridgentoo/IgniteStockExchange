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

import org.apache.ignite.internal.util.lang.*;
import org.apache.ignite.lang.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 *
 */
@SuppressWarnings("PublicInnerClass")
public class IgniteIterables {
    /**
     * Private constructor.
     */
    private IgniteIterables() {
        // No-op.
    }

    /**
     * @param iterable Input iterable.
     * @param filter Filter.
     */
    public static <T> Iterable<T> filter(@NotNull Iterable<T> iterable, @NotNull IgnitePredicate<T> filter) {
        return new FilteredIterable<>(iterable, filter);
    }
    /**
     * @param iterable Input iterable.
     * @param trans Transformation closure.
     */
    public static <T1, T2> Iterable<T2> transform(@NotNull Iterable<T1> iterable,
        @NotNull IgniteClosure<? super T1, T2> trans) {
        return new TransformedIterable<>(iterable, trans);
    }

    /**
     *
     */
    public static class TransformedIterable<T1, T2> implements Iterable<T2> {
        /** */
        private final Iterable<T1> src;

        /** */
        private final IgniteClosure<? super T1, T2> trans;

        /**
         * @param src Source.
         * @param trans Trans.
         */
        public TransformedIterable(Iterable<T1> src, IgniteClosure<? super T1, T2> trans) {
            this.src = src;
            this.trans = trans;
        }

        /** {@inheritDoc} */
        @Override public Iterator<T2> iterator() {
            return IgniteIterators.transform(src.iterator(), trans);
        }
    }

    /**
     *
     */
    public static class FilteredIterable<T> implements Iterable<T> {
        /** */
        private final Iterable<T> src;

        /** */
        private final IgnitePredicate<T> filter;

        /**
         * @param src Iterable.
         * @param filter Filter.
         */
        public FilteredIterable(Iterable<T> src, IgnitePredicate<T> filter) {
            this.src = src;
            this.filter = filter;
        }

        /** {@inheritDoc} */
        @Override public Iterator<T> iterator() {
            return new GridFilteredIterator<T>(src.iterator()) {
                @Override protected boolean accept(T t) {
                    return filter.apply(t);
                }
            };
        }
    }
}
