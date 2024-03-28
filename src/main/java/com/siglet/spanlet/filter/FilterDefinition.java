package com.siglet.spanlet.filter;


import com.siglet.config.builder.SpanletBuilder;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.spanlet.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class FilterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(SpanletBuilder::setConfig, "config",
                strictObject(FilterConfigBuilder::new,
                        requiredProperty(FilterConfigBuilder::setExpression, "expression", text())));
    }
}
