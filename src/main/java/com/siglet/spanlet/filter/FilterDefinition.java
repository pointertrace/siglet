package com.siglet.spanlet.filter;


import com.siglet.config.item.SpanletItem;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.spanlet.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class FilterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(SpanletItem::setConfig, "config",
                strictObject(FilterConfig::new,
                        requiredProperty(FilterConfig::setExpression, "expression", text())));
    }
}
