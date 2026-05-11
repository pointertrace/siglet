package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;

public abstract class GroovyActionProcessorType implements ProcessorType<GroovyActionConfig> {

    @Override
    public ConfigurationFactory<GroovyActionConfig> getConfigurationFactory() {
        return ConfigurationFactory.of(
                List.of(property("action", GroovyActionConfig::setAction, string())), GroovyActionConfig.class
        );
    }

}
