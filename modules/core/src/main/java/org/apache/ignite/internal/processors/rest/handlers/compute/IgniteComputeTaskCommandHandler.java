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
import org.apache.ignite.internal.util.typedef.T3;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.resources.*;
import org.jetbrains.annotations.*;

import javax.script.ScriptException;
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

        List<T3<String, String, String>> mapping =  req0.mapping();

        Object res = ctx.grid().compute().execute(new JsTask(mapping, ctx), null);

        return new GridFinishedFuture<>(new GridRestResponse(res));
    }

    private static class JsTask extends ComputeTaskAdapter<String, Object> {
        /** Mapping. */
        private List<T3<String, String, String>> mapping;

        /** Grid kernal context. */
        private GridKernalContext ctx;

        /**
         * @param mapping Task mapping.
         */
        public JsTask(List<T3<String, String, String>> mapping, GridKernalContext ctx) {
            this.mapping = mapping;
            this.ctx = ctx;
        }

        /** {@inheritDoc} */
        @Override public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> nodes, String arg) {
            Map<ComputeJob, ClusterNode> map = new HashMap<>();

            for (final T3<String, String, String> job : mapping) {
                UUID nodeId = UUID.fromString(job.get2());

                ClusterNode node = ctx.grid().cluster().node(nodeId);

                map.put(new ComputeJobAdapter() {
                    /** Ignite. */
                    @IgniteInstanceResource
                    private Ignite ignite;

                    @Override public Object execute() throws IgniteException {
                        System.out.println("Compute job on node " + ignite.cluster().localNode().id());

                        try {
                            return ((IgniteKernal)ignite).context().scripting().runJS(job.get1(), job.get3());
                        }
                        catch (ScriptException e) {
                            throw new IgniteException(e);
                        }
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

            return data;
        }
    }
}
