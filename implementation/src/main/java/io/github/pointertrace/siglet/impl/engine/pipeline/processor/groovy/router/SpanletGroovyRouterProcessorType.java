package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorCreator;

public class SpanletGroovyRouterProcessorType extends BaseGroovyRouterProcessorType {

    @Override
    public String getName() {
        return "spanlet-groovy-router";
    }

    @Override
    public ProcessorCreator getProcessorCreator() {
        return  (context, node) -> {
            if (node.getConfig().getConfig() instanceof GroovyRouterConfig) {
                return new SpanletGroovyRouterProcessor(context, node);
            } else {
                throw new SigletError(String.format("for groovy action type config must be a %s",
                        node.getConfig().getConfig().getClass().getName()));
            }
        };
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.SPAN;
    }
}
