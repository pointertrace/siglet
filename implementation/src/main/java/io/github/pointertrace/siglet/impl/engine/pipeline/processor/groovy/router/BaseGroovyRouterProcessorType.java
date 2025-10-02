package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;

public abstract class BaseGroovyRouterProcessorType implements ProcessorType {

    private final GroovyRouterDefinition routerDefinition = new GroovyRouterDefinition();

    @Override
    public ConfigDefinition getConfigDefinition() {
        return routerDefinition;
    }


}
