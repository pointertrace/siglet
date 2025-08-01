package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorCreator;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorType;

public class GroovyFilterProcessorType implements ProcessorType {

    private final GroovyFilterDefinition filterDefinition = new GroovyFilterDefinition();

    @Override
    public String getName() {
        return "groovy-filter";
    }

    @Override
    public ConfigDefinition getConfigDefinition() {
        return filterDefinition;
    }

    @Override
    public ProcessorCreator getProcessorCreator() {
        return  (context, node) -> {
            if (node.getConfig().getConfig() instanceof GroovyFilterConfig) {
                return new GroovyFilterProcessor(context, node);
            } else {
                throw new SigletError(String.format("for groovy action type config must be a %s",
                        node.getConfig().getConfig().getClass().getName()));
            }
        };
    }
}
