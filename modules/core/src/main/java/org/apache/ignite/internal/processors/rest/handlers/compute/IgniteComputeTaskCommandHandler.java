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

package org.apache.ignite.internal.processors.rest.handlers.compute;

import org.apache.ignite.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.compute.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.rest.*;
import org.apache.ignite.internal.processors.rest.handlers.*;
import org.apache.ignite.internal.processors.rest.request.*;
import org.apache.ignite.internal.util.future.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.resources.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static org.apache.ignite.internal.processors.rest.GridRestCommand.*;

/**
 * Compute task command handler.
 */
public class IgniteComputeTaskCommandHandler extends GridRestCommandHandlerAdapter {
    /** Supported commands. */
    private static final Collection<GridRestCommand> SUPPORTED_COMMANDS = U.sealList(EXECUTE_TASK);

    /**
     * @param ctx Context.
     */
    public IgniteComputeTaskCommandHandler(GridKernalContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    @Override public Collection<GridRestCommand> supportedCommands() {
        return SUPPORTED_COMMANDS;
    }

    /** {@inheritDoc} */
    @Override public IgniteInternalFuture<GridRestResponse> handleAsync(GridRestRequest req) {
        assert req != null;

        assert req instanceof RestComputeTaskRequest : "Invalid type of compute task request.";

        assert SUPPORTED_COMMANDS.contains(req.command());

        final RestComputeTaskRequest req0 = (RestComputeTaskRequest) req;

        Object execRes = ctx.grid().compute().execute(
            new JsTask(req0.mapFunc(), req0.argument(), req0.reduceFunc(), ctx), null);

        return new GridFinishedFuture<>(new GridRestResponse(execRes));
    }

    /**
     * JS Compute Task.
     */
    private static class JsTask extends ComputeTaskAdapter<String, Object> {
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
            Map<ComputeJob, ClusterNode> map = new HashMap<>();

            List jsMapRes = (List)ctx.scripting().runJSFunction(wrapMapperFunction(nodes));

            for (Object jobMapping : jsMapRes) {
                List task = (List)jobMapping;

                final String func = (String)task.get(0);
                final List argv = (List)task.get(1);
                String nodeId = (String)task.get(2);

                ClusterNode node = ctx.grid().cluster().node(UUID.fromString(nodeId));

                map.put(new ComputeJobAdapter() {
                    @IgniteInstanceResource
                    private Ignite ignite;

                    @Override public Object execute() throws IgniteException {
                        String[] argv1 = new String[argv.size()];

                        for (int i = 0; i < argv1.length; ++i)
                            argv1[i] = "\"" + argv.get(i).toString() + "\"";

                        return ctx.scripting().runJSFunction(func, argv1);
                    }
                }, node);
            }

            return map;
        }

        /** {@inheritDoc} */
        @Nullable @Override public Object reduce(List<ComputeJobResult> results) {
            List<Object> data = new ArrayList<>();

            for (ComputeJobResult res : results)
                data.add(res.getData());

            return ctx.scripting().runJSFunction(reduceFunc, data.toString());
        }

        /**
         * @param nodes Cluster nodes.
         * @return Script running map function.
         */
        private String wrapMapperFunction(List<ClusterNode> nodes) {
            List<String> ids = new ArrayList<>();

            for (ClusterNode node : nodes)
                ids.add("\"" + node.id().toString() + "\"");

            String sep = System.getProperty("line.separator");

            return "function () {" + sep +
                "       var res = [];" + sep +
                "       var emitFunc = function(f, args, nodeId) {" + sep +
                "           res.push([f.toString(), args, nodeId])" + sep +
                "       }" + sep +
                "       var f = " + mapFunc + ";" + sep +
                "       f(" + ids + ", " + "\"" + this.arg + "\"" + ", emitFunc.bind(null)" + ");" + sep +
                "       return res;" + sep +
                "   }";
        }
    }
}
