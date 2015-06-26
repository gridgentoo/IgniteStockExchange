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

package org.apache.ignite.internal.processors.rest.handlers.query;

import org.apache.ignite.cache.query.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.rest.*;
import org.apache.ignite.internal.processors.rest.handlers.*;
import org.apache.ignite.internal.processors.rest.request.*;
import org.apache.ignite.internal.util.future.*;
import org.apache.ignite.internal.util.typedef.internal.*;

import javax.cache.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.apache.ignite.internal.processors.rest.GridRestCommand.*;

/**
 * Query command handler.
 */
public class QueryCommandHandler extends GridRestCommandHandlerAdapter {
    /** Supported commands. */
    private static final Collection<GridRestCommand> SUPPORTED_COMMANDS = U.sealList(EXECUTE_SQL_QUERY,
        FETCH_SQL_QUERY);

    /** Query ID sequence. */
    private static final AtomicLong qryIdGen = new AtomicLong();

    /** Current queries. */
    private final ConcurrentHashMap<Long, Iterator<Cache.Entry<String, String>>> curs =
        new ConcurrentHashMap<>();

    /**
     * @param ctx Context.
     */
    public QueryCommandHandler(GridKernalContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    @Override public Collection<GridRestCommand> supportedCommands() {
        return SUPPORTED_COMMANDS;
    }

    /** {@inheritDoc} */
    @Override public IgniteInternalFuture<GridRestResponse> handleAsync(GridRestRequest req) {
        assert req != null;

        assert SUPPORTED_COMMANDS.contains(req.command());

        switch (req.command()) {
            case EXECUTE_SQL_QUERY: {
                assert req instanceof RestSqlQueryRequest : "Invalid type of query request.";

                return ctx.closure().callLocalSafe(
                    new ExecuteQueryCallable(ctx, (RestSqlQueryRequest)req, curs), false);
            }

            case FETCH_SQL_QUERY: {
                assert req instanceof RestSqlQueryRequest : "Invalid type of query request.";

                return ctx.closure().callLocalSafe(
                    new FetchQueryCallable(ctx, (RestSqlQueryRequest)req, curs), false);
            }
        }

        return new GridFinishedFuture<>();
    }

    /**
     * Execute query callable.
     */
    private static class ExecuteQueryCallable implements Callable<GridRestResponse> {
        /** */
        private static final long serialVersionUID = 0L;

        /** Kernal context. */
        private GridKernalContext ctx;

        /** Execute query request. */
        private RestSqlQueryRequest req;

        /** Queries cursors. */
        private ConcurrentHashMap<Long, Iterator<Cache.Entry<String, String>>> curs;

        /**
         * @param ctx Kernal context.
         * @param req Execute query request.
         */
        public ExecuteQueryCallable(GridKernalContext ctx, RestSqlQueryRequest req,
            ConcurrentHashMap<Long, Iterator<Cache.Entry<String, String>>> curs) {
            this.ctx = ctx;
            this.req = req;
            this.curs = curs;
        }

        /** {@inheritDoc} */
        @Override public GridRestResponse call() throws Exception {
            try {
                SqlQuery<String, String> qry = new SqlQuery(String.class, req.sqlQuery());

                Iterator<Cache.Entry<String, String>> cur =
                    ctx.grid().cache(req.cacheName()).query(qry).iterator();

                long qryId = qryIdGen.getAndIncrement();

                curs.put(qryId, cur);

                List<Cache.Entry<String, String>> res = new ArrayList<>();

                CacheQueryResult response = new CacheQueryResult();

                for (int i = 0; i < req.pageSize() && cur.hasNext(); ++i)
                    res.add(cur.next());

                response.setItems(res);

                response.setLast(!cur.hasNext());

                response.setQueryId(qryId);

                if (!cur.hasNext())
                    curs.remove(qryId);

                return new GridRestResponse(response);
            }
            catch (Exception e) {
                return new GridRestResponse(GridRestResponse.STATUS_FAILED, e.getMessage());
            }
        }
    }

    /**
     * Fetch query callable.
     */
    private static class FetchQueryCallable implements Callable<GridRestResponse> {
        /** */
        private static final long serialVersionUID = 0L;

        /** Kernal context. */
        private GridKernalContext ctx;

        /** Execute query request. */
        private RestSqlQueryRequest req;

        /** Queries cursors. */
        private ConcurrentHashMap<Long, Iterator<Cache.Entry<String, String>>> curs;

        /**
         * @param ctx Kernal context.
         * @param req Execute query request.
         */
        public FetchQueryCallable(GridKernalContext ctx, RestSqlQueryRequest req,
            ConcurrentHashMap<Long, Iterator<Cache.Entry<String, String>>> curs) {
            this.ctx = ctx;
            this.req = req;
            this.curs = curs;
        }

        /** {@inheritDoc} */
        @Override public GridRestResponse call() throws Exception {
            try {
                if (curs.contains(req.queryId()))
                    return new GridRestResponse(GridRestResponse.STATUS_FAILED,
                        "Cannot find query [qryId=" + req.queryId() + "]");

                Iterator<Cache.Entry<String, String>> cur = curs.get(req.queryId());

                List<Cache.Entry<String, String>> res = new ArrayList<>();

                CacheQueryResult response = new CacheQueryResult();

                for (int i = 0; i < req.pageSize() && cur.hasNext(); ++i)
                    res.add(cur.next());

                response.setItems(res);

                response.setLast(!cur.hasNext());

                response.setQueryId(req.queryId());

                if (!cur.hasNext())
                    curs.remove(req.queryId());

                return new GridRestResponse(response);
            }
            catch (Exception e) {
                return new GridRestResponse(GridRestResponse.STATUS_FAILED, e.getMessage());
            }
        }
    }
}
