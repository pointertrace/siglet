package com.siglet.container.engine.pipeline.processor.groovy.action;


import com.siglet.parser.NodeChecker;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.container.engine.pipeline.processor.ConfigDefinition;

import static com.siglet.parser.schema.SchemaFactory.*;

public class GroovyActionDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(ProcessorConfig::setConfig, ProcessorConfig::setConfigLocation, "config",
                strictObject(GroovyActionConfig::new,
                        requiredProperty(GroovyActionConfig::setAction,
                                GroovyActionConfig::setActionLocation,
                                "action", text())));
    }
}
