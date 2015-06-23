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

import javax.script.*;
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

        assert req instanceof RestComputeTaskRequest : "Invalid type of compute request.";

        assert SUPPORTED_COMMANDS.contains(req.command());

        final RestComputeTaskRequest req0 = (RestComputeTaskRequest) req;

        Object res = ctx.grid().compute().execute(new JsTask(req0.mapFunc(), req0.argument(), req0.reduceFunc(), ctx), null);

        return new GridFinishedFuture<>(new GridRestResponse(res));
    }

    private static class JsTask extends ComputeTaskAdapter<String, Object> {
        /** Mapping function. */
        private String mapFunc;

        private String reduceFunc;

        /** Grid kernal context. */
        private GridKernalContext ctx;

        private String arg;

        /**
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

            String nodesIds = "[";

            for (ClusterNode node : nodes)
                nodesIds += "\""  + node.id().toString() + "\"" + ",";

            nodesIds = nodesIds.substring(0, nodesIds.length() - 1) + "]";

            try {
                String newMap = new String("function () {\n" +
                    "   var res = [];\n" +
                    "   var resCont = function(f, args, nodeId) {\n" +
                    "       res.push([f.toString(), args, nodeId])\n" +
                    "   }\n" +
                    "   var locF = " + mapFunc + "; \n locF(" +
                        nodesIds + ", " +
                    "\"" + this.arg + "\"" +
                    ", resCont.bind(null)" + ");\n" +
                    "   return res;\n" +
                    "}");

                List mapRes = (List)ctx.scripting().runJS(newMap);

                for (Object arr : mapRes) {
                    Object[] nodeTask = ((List)arr).toArray();

                    final String func = (String)nodeTask[0];

                    final List argv = (List) nodeTask[1];

                    String nodeIdStr = (String) nodeTask[2];

                    UUID nodeId = UUID.fromString(nodeIdStr);

                    ClusterNode node = ctx.grid().cluster().node(nodeId);

                    map.put(new ComputeJobAdapter() {
                        /** Ignite. */
                        @IgniteInstanceResource
                        private Ignite ignite;

                        @Override public Object execute() throws IgniteException {
                            System.out.println("Compute job on node " + ignite.cluster().localNode().id());
                            try {
                                String[] argv1 = new String[argv.size()];

                                for (int i = 0; i < argv1.length; ++i)
                                    argv1[i] = "\"" + argv.get(i).toString() + "\"";

                                return ctx.scripting().runJS(func, argv1);
                            }
                            catch (Exception e) {
                                throw new IgniteException(e);
                            }
                        }
                    }, node);

                }
            }
            catch (ScriptException e) {
                throw new IgniteException(e);
            }
            finally {
            }

            return map;
        }

        /** {@inheritDoc} */
        @Nullable @Override public Object reduce(List<ComputeJobResult> results) {
            List<Object> data = new ArrayList<>();

            for (ComputeJobResult res : results)
                data.add(res.getData());

            try {
                return ctx.scripting().runJS(reduceFunc, new String[] {data.toString()});
            }
            catch (ScriptException e) {
                throw new IgniteException(e);
            }
        }
    }
}
