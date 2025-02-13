package com.siglet.pipeline.processor.common.filter;


import com.siglet.config.item.SigletItem;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.SchemaFactory;
import com.siglet.pipeline.processor.common.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.requiredProperty;
import static com.siglet.config.parser.schema.SchemaFactory.text;

public class FilterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return SchemaFactory.requiredProperty(SigletItem::setConfig, SigletItem::setConfigLocation, "config",
                SchemaFactory.strictObject(FilterConfig::new,
                        requiredProperty(FilterConfig::setExpression,
                                FilterConfig::setExpressionLocation,
                                "expression", text())));
    }
}
