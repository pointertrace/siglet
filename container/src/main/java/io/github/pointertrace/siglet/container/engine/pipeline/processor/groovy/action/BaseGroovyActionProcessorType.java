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


}
