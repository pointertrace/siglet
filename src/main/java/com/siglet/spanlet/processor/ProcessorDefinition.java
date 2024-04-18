package com.siglet.spanlet.processor;


import com.siglet.config.item.ProcessorItem;
import com.siglet.config.item.SpanletItem;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.spanlet.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class ProcessorDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(ProcessorItem::setConfig, "config",
                strictObject(ProcessorConfig::new,
                        requiredProperty(ProcessorConfig::setAction, "action", text())));
    }
}
