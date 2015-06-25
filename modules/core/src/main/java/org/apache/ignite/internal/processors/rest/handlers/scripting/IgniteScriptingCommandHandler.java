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
import org.apache.ignite.internal.util.typedef.*;
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

    /** Emit result. */
    private IgniteJsEmitResult emitRes;

    /**
     * @param ctx Context.
     */
    public IgniteScriptingCommandHandler(GridKernalContext ctx) {
        super(ctx);

        try {
            IgniteScriptProcessor script = ctx.scripting();

            String emitFunction = "function emit(f, args, nodeId) {" +
                "__emitResult.add(f.toString(), args, nodeId);}";

            script.addEngineFunction(emitFunction);

            emitRes = new IgniteJsEmitResult();

            script.addBinding("__emitResult", emitRes);
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

                return ctx.closure().callAsync(new IgniteClosure<RestRunScriptRequest, GridRestResponse>() {
                    @Override public GridRestResponse apply(RestRunScriptRequest req) {
                        try {
                            return new GridRestResponse(ctx.grid().compute().call(
                                new JsFunctionCallable(req.script(), req.argument())));
                        }
                        catch (Exception e) {
                            return new GridRestResponse(GridRestResponse.STATUS_FAILED, e.getMessage());
                        }
                    }
                }, (RestRunScriptRequest)req, Collections.singleton(ctx.grid().localNode()));
            }

            case EXECUTE_MAP_REDUCE_SCRIPT: {
                assert req instanceof RestMapReduceScriptRequest :
                    "Invalid type of execute map reduce script request.";

                assert SUPPORTED_COMMANDS.contains(req.command());

                return ctx.closure().callAsync(new IgniteClosure<RestMapReduceScriptRequest, GridRestResponse>() {
                    @Override public GridRestResponse apply(RestMapReduceScriptRequest req0) {
                        try {
                            return new GridRestResponse(ctx.grid().compute().execute(
                                new JsTask(req0.mapFunction(), req0.argument(), req0.reduceFunction(), ctx, emitRes),
                                null));
                        }
                        catch (Exception e) {
                            return new GridRestResponse(GridRestResponse.STATUS_FAILED, e.getMessage());
                        }
                }
                }, (RestMapReduceScriptRequest)req, Collections.singleton(ctx.grid().localNode()));
            }
        }

        return new GridFinishedFuture<>();
    }

    /**
     * JS Compute Task.
     */
    private static class JsTask extends ComputeTaskAdapter<String, Object> {
        /** */
        private static final long serialVersionUID = 0L;

        /** Mapping function. */
        private String mapFunc;

        /** Reduce function. */
        private String reduceFunc;

        /** Kernal context. */
        private GridKernalContext ctx;

        /** Map function argument. */
        private Object arg;

        /** Emit results. */
        private IgniteJsEmitResult emitRes;

        /**
         * @param mapFunc Map function.
         * @param arg Map function argument.
         * @param reduceFunc Reduce function.
         * @param ctx Kernal context.
         */
        public JsTask(String mapFunc, Object arg, String reduceFunc, GridKernalContext ctx, IgniteJsEmitResult emitRes) {
            this.mapFunc = mapFunc;
            this.reduceFunc = reduceFunc;
            this.arg = arg;
            this.ctx = ctx;
            this.emitRes = emitRes;
        }

        /** {@inheritDoc} */
        @Override public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> nodes, String arg) {
            try {
                Map<ComputeJob, ClusterNode> map = new HashMap<>();

                ctx.scripting().invokeFunction(mapFunc, nodes.toArray(new ClusterNode[nodes.size()]), this.arg);

                List<T3<Object, Object, Object>> jsMapRes = emitRes.getEmitResult();

                for (T3<Object, Object, Object> task : jsMapRes) {

                    map.put(new JsCallFunctionJob((String)task.get1(), task.get2()),
                        (ClusterNode)task.get3());
                }

                return map;
            }
            catch (IgniteCheckedException e) {
                throw U.convertException(e);
            }
        }

        /** {@inheritDoc} */
        @Nullable @Override public Object reduce(List<ComputeJobResult> results) {
            try {
                Object[] data = new Object[results.size()];

                for (int i = 0; i < results.size(); ++i) {
                    IgniteException err = results.get(i).getException();

                    if (err != null)
                        return new GridRestResponse(GridRestResponse.STATUS_FAILED, err.getMessage());

                    data[i] = results.get(i).getData();
                }

                return ctx.scripting().invokeFunction(reduceFunc, data);
            }
            catch (IgniteCheckedException e) {
                throw U.convertException(e);
            }
        }
    }

    /**
     * Js call function job.
     */
    private static class JsCallFunctionJob extends ComputeJobAdapter {
        /** */
        private static final long serialVersionUID = 0L;

        /** Function to call. */
        private String func;

        /** Function argument. */
        private Object argv;

        /** Ignite instance. */
        @IgniteInstanceResource
        private Ignite ignite;

        /**
         * @param func Function to call.
         * @param argv Function argument.
         */
        public JsCallFunctionJob(String func, Object argv) {
            this.func = func;
            this.argv = argv;
        }

        /** {@inheritDoc} */
        @Override public Object execute() throws IgniteException {
            try {
                return ((IgniteKernal)ignite).context().scripting().invokeFunction(func, argv);
            }
            catch (IgniteCheckedException e) {
                throw U.convertException(e);
            }
        }
    }

    /**
     * Call java script function.
     */
    private static class JsFunctionCallable implements IgniteCallable<Object> {
        /** */
        private static final long serialVersionUID = 0L;

        /** Function to call. */
        private String func;

        /** Function argument. */
        private Object arg;

        /** Ignite instance. */
        @IgniteInstanceResource
        private Ignite ignite;

        /**
         * @param func Function to call.
         * @param arg Function argument.
         */
        public JsFunctionCallable(String func, Object arg) {
            this.func = func;
            this.arg = arg;
        }

        /** {@inheritDoc} */
        @Override public Object call() {
            try {
                return ((IgniteKernal)ignite).context().scripting().invokeFunction(func, arg);
            }
            catch (IgniteCheckedException e) {
                throw U.convertException(e);
            }
        }
    }
}
