package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorType;

public abstract class BaseGroovyFilterProcessorType implements ProcessorType {

    private final GroovyFilterDefinition filterDefinition = new GroovyFilterDefinition();

    @Override
    public ConfigDefinition getConfigDefinition() {
        return filterDefinition;
    }


}
