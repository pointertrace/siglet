package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorCreator;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorType;

public class GroovyRouterProcessorType implements ProcessorType {

    private final GroovyRouterDefinition routerDefinition = new GroovyRouterDefinition();

    @Override
    public String getName() {
        return "groovy-router";
    }

    @Override
    public ConfigDefinition getConfigDefinition() {
        return routerDefinition;
    }

    @Override
    public ProcessorCreator getProcessorCreator() {
        return  (context, node) -> {
            if (node.getConfig().getConfig() instanceof GroovyRouterConfig) {
                return new GroovyRouterProcessor(context, node);
            } else {
                throw new SigletError(String.format("for groovy action type config must be a %s",
                        node.getConfig().getConfig().getClass().getName()));
            }
        };
    }
}
