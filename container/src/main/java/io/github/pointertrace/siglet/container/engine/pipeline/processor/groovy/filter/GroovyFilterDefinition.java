package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.filter;


import io.github.pointertrace.siglet.container.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.schema.SchemaFactory;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.requiredProperty;
import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.text;

public class GroovyFilterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return SchemaFactory.requiredProperty(ProcessorConfig::setConfig, ProcessorConfig::setConfigLocation, "config",
                SchemaFactory.strictObject(GroovyFilterConfig::new,
                        requiredProperty(GroovyFilterConfig::setExpression,
                                GroovyFilterConfig::setExpressionLocation, "expression", text())));
    }
}
