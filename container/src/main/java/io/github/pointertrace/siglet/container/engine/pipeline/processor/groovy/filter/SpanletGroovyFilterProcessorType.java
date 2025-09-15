package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorCreator;

public class SpanletGroovyFilterProcessorType extends BaseGroovyFilterProcessorType {

    @Override
    public String getName() {
        return "spanlet-groovy-filter";
    }

    @Override
    public ProcessorCreator getProcessorCreator() {
        return  (context, node) -> {
            if (node.getConfig().getConfig() instanceof GroovyFilterConfig) {
                return new SpanletGroovyFilterProcessor(context, node);
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
