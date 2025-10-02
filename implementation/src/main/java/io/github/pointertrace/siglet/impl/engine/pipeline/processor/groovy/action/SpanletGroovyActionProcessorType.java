package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorCreator;

public class SpanletGroovyActionProcessorType extends BaseGroovyActionProcessorType {

    @Override
    public String getName() {
        return "spanlet-groovy-action";
    }

    @Override
    public ProcessorCreator getProcessorCreator() {
        return (context, node) -> {
            if (node.getConfig().getConfig() instanceof GroovyActionConfig) {
                return new SpanletGroovyActionProcessor(context, node);
            } else {
                throw new SigletError(String.format("for groovy action type config must be a %s",
                        node.getConfig().getClass().getName()));
            }
        };
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.SPAN;
    }
}
