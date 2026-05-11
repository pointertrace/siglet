package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.property;
import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;

public abstract class GroovyFilterProcessorType implements ProcessorType<GroovyFilterConfig> {

    @Override
    public ConfigurationFactory<GroovyFilterConfig> getConfigurationFactory() {
        return ConfigurationFactory.of(
                List.of(property("expression", GroovyFilterConfig::setExpression, string())), GroovyFilterConfig.class
        );
    }




}