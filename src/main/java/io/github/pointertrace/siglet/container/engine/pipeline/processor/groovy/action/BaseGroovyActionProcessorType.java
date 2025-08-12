package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorCreator;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorType;

public abstract class BaseGroovyActionProcessorType implements ProcessorType {

    private final GroovyActionDefinition actionDefinition = new GroovyActionDefinition();

    @Override
    public ConfigDefinition getConfigDefinition() {
        return actionDefinition;
    }

    @Override
    public ProcessorCreator getProcessorCreator() {
        return (context, node) -> {
            if (node.getConfig().getConfig() instanceof GroovyActionConfig) {
                return new GroovyActionProcessor(context, node);
            } else {
                throw new SigletError(String.format("for groovy action type config must be a %s",
                        node.getConfig().getClass().getName()));
            }
        };
    }
}
