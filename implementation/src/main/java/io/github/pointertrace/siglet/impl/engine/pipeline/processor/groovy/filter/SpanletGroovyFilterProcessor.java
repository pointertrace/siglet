package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.Context;

public class SpanletGroovyFilterProcessor extends BaseGroovyFilterProcessor {

    private final static SignalType signalType = SignalType.SPAN;

    public SpanletGroovyFilterProcessor(String name, String expression, int queueCapacity, int threadPoolSize) {
        super(name, expression, signalType, queueCapacity, threadPoolSize);
    }

    public SpanletGroovyFilterProcessor(Context context, ProcessorNode node) {
        super(context, node);
    }

    @Override
    public SignalType getSignalType() {
        return signalType;
    }
}
