package com.siglet.container.engine.pipeline.processor.groovy.action;

import com.siglet.SigletError;
import com.siglet.container.engine.pipeline.processor.ConfigDefinition;
import com.siglet.container.engine.pipeline.processor.ProcessorCreator;
import com.siglet.container.engine.pipeline.processor.ProcessorType;

public class GroovyActionProcessorType implements ProcessorType {

    private final GroovyActionDefinition actionDefinition = new GroovyActionDefinition();

    @Override
    public String getName() {
        return "groovy-action";
    }

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
