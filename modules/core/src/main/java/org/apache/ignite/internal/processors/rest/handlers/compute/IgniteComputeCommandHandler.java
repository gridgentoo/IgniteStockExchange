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
import org.apache.ignite.internal.*;
import org.apache.ignite.internal.processors.rest.*;
import org.apache.ignite.internal.processors.rest.handlers.*;
import org.apache.ignite.internal.processors.rest.request.*;
import org.apache.ignite.internal.util.future.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.lang.*;
import org.apache.ignite.resources.*;

import java.util.*;

import static org.apache.ignite.internal.processors.rest.GridRestCommand.*;

/**
 * Compute command handler.
 */
public class IgniteComputeCommandHandler extends GridRestCommandHandlerAdapter {
    /** Supported commands. */
    private static final Collection<GridRestCommand> SUPPORTED_COMMANDS = U.sealList(
        AFFINITY_RUN,
        AFFINITY_CALL);

    /**
     * @param ctx Context.
     */
    public IgniteComputeCommandHandler(GridKernalContext ctx) {
        super(ctx);
    }

    /** {@inheritDoc} */
    @Override public Collection<GridRestCommand> supportedCommands() {
        return SUPPORTED_COMMANDS;
    }

    /** {@inheritDoc} */
    @Override public IgniteInternalFuture<GridRestResponse> handleAsync(GridRestRequest req) {
        assert req != null;

        assert req instanceof RestComputeRequest : "Invalid type of compute request.";

        assert SUPPORTED_COMMANDS.contains(req.command());

        final RestComputeRequest req0 = (RestComputeRequest) req;

        switch (req.command()) {
            case AFFINITY_RUN:
                ctx.grid().compute().affinityRun(req0.cacheName(), req0.key(), new IgniteRunnable() {
                    @IgniteInstanceResource
                    private Ignite ignite;

                    @Override public void run() {
                        ((IgniteKernal) ignite).context().scripting().invokeFunction(req0.function());
                    }
                });

                return new GridFinishedFuture<>(new GridRestResponse());

            case AFFINITY_CALL:
                Object res = ctx.grid().compute().affinityCall(req0.cacheName(), req0.key(), new IgniteCallable<Object>() {
                    @IgniteInstanceResource
                    private Ignite ignite;

                    @Override public Object call() {
                        return ((IgniteKernal) ignite).context().scripting().invokeFunction(req0.function());
                    }
                });

                return new GridFinishedFuture<>(new GridRestResponse(res));
        }

        return new GridFinishedFuture<>();
    }
}
