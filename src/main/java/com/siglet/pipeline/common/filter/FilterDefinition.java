package com.siglet.pipeline.common.filter;


import com.siglet.config.item.ProcessorItem;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.SchemaFactory;
import com.siglet.pipeline.common.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.requiredProperty;
import static com.siglet.config.parser.schema.SchemaFactory.text;

public class FilterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return SchemaFactory.requiredProperty(ProcessorItem::setConfig, "config",
                SchemaFactory.strictObject(FilterConfig::new,
                        requiredProperty(FilterConfig::setExpression, "expression", text())));
    }
}
