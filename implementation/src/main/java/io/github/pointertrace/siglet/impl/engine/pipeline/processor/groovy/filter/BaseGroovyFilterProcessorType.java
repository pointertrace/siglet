package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;

public abstract class BaseGroovyFilterProcessorType implements ProcessorType {

    private final GroovyFilterDefinition filterDefinition = new GroovyFilterDefinition();

    @Override
    public ConfigDefinition getConfigDefinition() {
        return filterDefinition;
    }


}
