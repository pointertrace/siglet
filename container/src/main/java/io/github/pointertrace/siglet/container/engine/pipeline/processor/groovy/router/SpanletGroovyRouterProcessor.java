package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.container.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.engine.Context;

import java.util.List;

public class SpanletGroovyRouterProcessor extends BaseGroovyRouterProcessor {

    private final static SignalType signalType = SignalType.SPAN;

    public SpanletGroovyRouterProcessor(String name, String defaultRoute, List<Route> routes, int queueCapacity, int threadPoolSize) {
        super(name, defaultRoute, routes, signalType, queueCapacity, threadPoolSize);
    }

    public SpanletGroovyRouterProcessor(Context context, ProcessorNode node) {
        super(context, node);
    }

    @Override
    public SignalType getSignalType() {
        return signalType;
    }
}
