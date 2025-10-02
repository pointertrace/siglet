package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.Context;

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
