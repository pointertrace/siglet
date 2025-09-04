package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.action;


import io.github.pointertrace.siglet.container.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.parser.NodeChecker;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.*;

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
