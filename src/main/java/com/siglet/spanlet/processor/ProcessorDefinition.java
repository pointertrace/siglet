package com.siglet.spanlet.processor;


import com.siglet.config.builder.SpanletBuilder;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.spanlet.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class ProcessorDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(SpanletBuilder::setConfig, "config",
                strictObject(ProcessorConfigBuilder::new,
                        requiredProperty(ProcessorConfigBuilder::setAction, "action", text())));
    }
}
