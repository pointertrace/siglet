package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.Context;

public class SpanletGroovyActionProcessor extends BaseGroovyActionProcessor {

    private static final SignalType signalType = SignalType.SPAN;

    public SpanletGroovyActionProcessor(String name, String action, int queueCapacity, int threadPoolSize) {
        super(name, action, signalType, queueCapacity, threadPoolSize);
    }

    public SpanletGroovyActionProcessor(Context context, ProcessorNode node) {
        super(context, node);
    }

    @Override
    public SignalType getSignalType() {
        return signalType;
    }
}
