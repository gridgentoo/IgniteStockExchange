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

import org.apache.ignite.internal.util.typedef.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.lang.*;
import org.jetbrains.annotations.*;
import org.jsr166.*;

import java.io.*;
import java.lang.management.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Utility class for debugging.
 */
public class GridDebug {
    /** */
    private static final AtomicReference<ConcurrentHashMap8<Long, Que>> que =
        new AtomicReference<>(new ConcurrentHashMap8<Long, Que>());

    /** */
    private static final SimpleDateFormat DEBUG_DATE_FMT = new SimpleDateFormat("HH:mm:ss,SSS");

    /** */
    private static final FileOutputStream out;

    /** */
    private static final Charset charset = Charset.forName("UTF-8");

    /** */
    private static volatile long start;

    /**
     * On Ubuntu:
     * sudo mkdir /ramdisk
     * sudo mount -t tmpfs -o size=2048M tmpfs /ramdisk
     */
    private static final String LOGS_PATH = null;// "/ramdisk/";

    /** */
    private static boolean allowLog;

    /** */
    static {
        if (LOGS_PATH != null) {
            File log = new File(LOGS_PATH + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-").format(new Date()) +
                    ManagementFactory.getRuntimeMXBean().getName() + ".log");

            assert !log.exists();

            try {
                out = new FileOutputStream(log, false);
            }
            catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        else
            out = null;
    }

    /**
     * @param allow Write log.
     */
    public static synchronized void allowWriteLog(boolean allow) {
        allowLog = allow;
    }

    /**
     * Writes to log file which should reside on ram disk.
     *
     * @param x Data to log.
     */
    public static synchronized void write(Object ... x) {
        if (!allowLog)
            return;

        Thread th = Thread.currentThread();

        try {
            out.write((formatEntry(System.currentTimeMillis(), th.getName(), th.getId(), x) + "\n").getBytes(charset));
            out.flush();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Add the data to debug queue.
     *
     * @param x Debugging data.
     */
    public static void debug(Object ... x) {
//        if (true)
//            return;

        ConcurrentHashMap8<Long,Que> m = que.get();

        if (m == null)
            return;

        Item i = new Item(x);

        Que q = m.get(i.threadId);

        if (q == null)
            m.put(i.threadId, q = new Que());

        q.add(i);
    }

    /**
     * Hangs for 5 minutes if stopped.
     */
    public static void hangIfStopped() {
        if (que.get() == null)
            try {
                Thread.sleep(300000);
            }
            catch (InterruptedException ignored) {
                // No-op.
            }
    }

    /**
     * Sets starting time after which {@link #timing(String)} measurements can be done.
     */
    public static void start() {
        start = U.currentTimeMillis();
    }

    /**
     * Print timing after the {@link #start()} call.
     *
     * @param label Label.
     */
    public static void timing(String label) {
        X.println(label + ' ' + (U.currentTimeMillis() - start) + " ms");
    }

    /**
     * @return Object which will dump thread stack on toString call.
     */
    public static Object dumpStack() {
        final Throwable t = new Throwable();

        return new Object() {
            @Override public String toString() {
                StringWriter errors = new StringWriter();

                t.printStackTrace(new PrintWriter(errors));

                return errors.toString();
            }
        };
    }

    /**
     * Dump given queue to stdout.
     *
     * @param que Queue.
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public static void dump(Collection<Item> que) {
        if (que == null)
            return;

        int start = -1;// que.size() - 5000;

        int x = 0;

        for (Item i : que) {
            if (x++ > start)
                System.out.println(i);
        }
    }

    /**
     * Dump existing queue to stdout.
     *
     * @param filter Filter for logged debug items.
     * @return Empty string.
     */
    public static String dumpWithStop(@Nullable IgnitePredicate<Item> filter) {
        ConcurrentHashMap8<Long,Que> m;

        do {
            m = que.get();

            if (m == null)
                return ""; // Stopped.
        }
        while (!que.compareAndSet(m, null));

        List<Item> col = new ArrayList<>();

        for (Que q : m.values()) // Merge all threads together.
            q.collect(col, filter);

        Collections.sort(col);

        dump(col);

        return "";
    }

    /**
     * Formats log entry string.
     *
     * @param ts Timestamp.
     * @param threadName Thread name.
     * @param threadId Thread ID.
     * @param data Data.
     * @return String.
     */
    private static String formatEntry(long ts, String threadName, long threadId, Object... data) {
        return "<" + DEBUG_DATE_FMT.format(new Date(ts)) + "><~DBG~><" + threadName + " id:" + threadId + "> " +
            Arrays.deepToString(data);
    }

    /**
     * Debug info queue item.
     */
    @SuppressWarnings({"PublicInnerClass", "PublicField"})
    public static class Item implements Comparable<Item> {
        /** */
        public final long ts = U.currentTimeMillis();

        /** */
        public final String threadName;

        /** */
        public final long threadId;

        /** */
        public final Object[] data;

        /** */
        public int order;

        /**
         * Constructor.
         *
         * @param data Debugging data.
         */
        public Item(Object[] data) {
            this.data = data;
            Thread th = Thread.currentThread();

            threadName = th.getName();
            threadId = th.getId();
        }

        /** {@inheritDoc} */
        @Override public int compareTo(Item o) {
            if (ts == o.ts)
                return order > o.order ? 1 : -1;

            return ts > o.ts ? 1 : -1;
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return formatEntry(ts, threadName, threadId, data);
        }
    }

    public static class Que {
        /** */
        private static int BLOCK_SIZE = 1024;

        /** */
        private static int MASK = BLOCK_SIZE - 1;

        /** */
        private Block last;

        /** */
        private int curIdx;

        public void add(Item item) {
            assert item != null;

            int idx = curIdx & MASK;

            if (idx == 0)
                last = new Block(last, BLOCK_SIZE);

            item.order = curIdx++;

            last.items[idx] = item;
        }

        public void collect(Collection<Item> to, IgnitePredicate<Item> filter) {
            Block b = last;

            while (b != null) {
                for (Item item : b.items) {
                    if (item == null)
                        break;

                    if (filter == null || filter.apply(item))
                        to.add(item);
                }

                b = b.prev;
            }
        }
    }

    /**
     *
     */
    private static class Block {
        /** */
        private final Item[] items;

        /** */
        private final Block prev;

        public Block(Block prev, int cap) {
            this.prev = prev;

            items = new Item[cap];
        }
    }
}
