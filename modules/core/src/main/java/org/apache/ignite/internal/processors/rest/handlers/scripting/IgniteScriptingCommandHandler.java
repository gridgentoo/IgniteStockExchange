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

package org.apache.ignite.internal.processors.rest.handlers.scripting;

import org.apache.ignite.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.compute.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.rest.*;
import org.apache.ignite.internal.processors.rest.handlers.*;
import org.apache.ignite.internal.processors.rest.request.*;
import org.apache.ignite.internal.processors.scripting.*;
import org.apache.ignite.internal.util.future.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.lang.*;
import org.apache.ignite.resources.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static org.apache.ignite.internal.processors.rest.GridRestCommand.*;

/**
 * Compute task command handler.
 */
public class IgniteScriptingCommandHandler extends GridRestCommandHandlerAdapter {
    /** Supported commands. */
    private static final Collection<GridRestCommand> SUPPORTED_COMMANDS = U.sealList(
        EXECUTE_MAP_REDUCE_SCRIPT,
        RUN_SCRIPT);

    /**
     * @param ctx Context.
     */
    public IgniteScriptingCommandHandler(GridKernalContext ctx) {
        super(ctx);

        try {
            IgniteScriptProcessor script = ctx.scripting();

            String emitFunction = "function emit(result, f, args, nodeId) {result.push([f.toString(), args, nodeId])}";

            String computeFunction = "function __compute(mapFuncSource, ids, args) {"  +
                "       var res = [];" +
                "       var f = __createJSFunction(mapFuncSource);" +
                "       f(ids, args, emit.bind(null, res)); "  +
                "       return res;" +
                "   }";

            script.addEngineFunction(emitFunction);
            script.addEngineFunction(computeFunction);
        }
        catch (IgniteCheckedException e) {
            ctx.log().error(e.getMessage());
        }
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
            case RUN_SCRIPT: {
                assert req instanceof RestRunScriptRequest : "Invalid type of run script request.";

                final RestRunScriptRequest req0 = (RestRunScriptRequest) req;

                GridRestResponse res = ctx.grid().compute().call(new IgniteCallable<GridRestResponse>() {
                    @IgniteInstanceResource
                    private Ignite ignite;

                    @Override public GridRestResponse call() {
                        try {
                            return new GridRestResponse(((IgniteKernal)ignite).
                                context().scripting().invokeFunction(req0.script()));
                        }
                        catch (IgniteCheckedException e) {
                            return new GridRestResponse(GridRestResponse.STATUS_FAILED, e.getMessage());
                        }
                    }
                });

                return new GridFinishedFuture<>(res);
            }

            case EXECUTE_MAP_REDUCE_SCRIPT: {
                assert req instanceof RestMapReduceScriptRequest :
                    "Invalid type of execute map reduce script request.";

                assert SUPPORTED_COMMANDS.contains(req.command());

                final RestMapReduceScriptRequest req0 = (RestMapReduceScriptRequest) req;

                GridRestResponse execRes = ctx.grid().compute().execute(
                    new JsTask(req0.mapFunction(), req0.argument(), req0.reduceFunction(), ctx), null);

                return new GridFinishedFuture<>(execRes);
            }
        }

        return new GridFinishedFuture<>();
    }

    /**
     * JS Compute Task.
     */
    private static class JsTask extends ComputeTaskAdapter<String, GridRestResponse> {
        /** */
        private static final long serialVersionUID = 0L;

        /** Mapping function. */
        private String mapFunc;

        /** Reduce function. */
        private String reduceFunc;

        /** Kernal context. */
        private GridKernalContext ctx;

        /** Map function argument. */
        private String arg;

        /**
         * @param mapFunc Map function.
         * @param arg Map function argument.
         * @param reduceFunc Reduce function.
         * @param ctx Kernal context.
         */
        public JsTask(String mapFunc, String arg, String reduceFunc, GridKernalContext ctx) {
            this.mapFunc = mapFunc;
            this.reduceFunc = reduceFunc;
            this.arg = arg;
            this.ctx = ctx;
        }

        /** {@inheritDoc} */
        @Override public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> nodes, String arg) {
            try {
                Map<ComputeJob, ClusterNode> map = new HashMap<>();

                List jsMapRes = (List)ctx.scripting().invokeFunctionByName("__compute",
                    mapFunc, nodes.toArray(new ClusterNode[nodes.size()]), this.arg);

                for (Object jobMapping : jsMapRes) {
                    List task = (List)jobMapping;

                    final String func = (String)task.get(0);
                    final Object argv = task.get(1);
                    ClusterNode node = (ClusterNode)task.get(2);

                    map.put(new ComputeJobAdapter() {
                        @IgniteInstanceResource
                        private Ignite ignite;

                        @Override public Object execute() throws IgniteException {
                            try {
                                return ((IgniteKernal)ignite).context().scripting().invokeFunction(func, argv);
                            }
                            catch (IgniteCheckedException e) {
                               throw U.convertException(e);
                            }
                        }
                    }, node);
                }

                return map;
            }
            catch (IgniteCheckedException e) {
                throw U.convertException(e);
            }
        }

        /** {@inheritDoc} */
        @Nullable @Override public GridRestResponse reduce(List<ComputeJobResult> results) {
            try {
                String[] data = new String[results.size()];

                for (int i = 0; i < results.size(); ++i) {
                    IgniteException err = results.get(i).getException();

                    if (err != null)
                        return new GridRestResponse(GridRestResponse.STATUS_FAILED, err.getMessage());

                    data[i] = results.get(i).getData().toString();
                }

                return new GridRestResponse(ctx.scripting().invokeFunction(reduceFunc, (Object)data));
            }
            catch (IgniteCheckedException e) {
                return new GridRestResponse(GridRestResponse.STATUS_FAILED, e.getMessage());
            }
        }
    }
}
