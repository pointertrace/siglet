package com.siglet.container.engine.pipeline.processor.groovy.filter;


import com.siglet.api.parser.NodeChecker;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.container.engine.pipeline.processor.ConfigDefinition;
import com.siglet.parser.schema.SchemaFactory;

import static com.siglet.parser.schema.SchemaFactory.requiredProperty;
import static com.siglet.parser.schema.SchemaFactory.text;

public class GroovyFilterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return SchemaFactory.requiredProperty(ProcessorConfig::setConfig, ProcessorConfig::setConfigLocation, "config",
                SchemaFactory.strictObject(GroovyFilterConfig::new,
                        requiredProperty(GroovyFilterConfig::setExpression,
                                GroovyFilterConfig::setExpressionLocation,
                                "expression", text())));
    }
}
