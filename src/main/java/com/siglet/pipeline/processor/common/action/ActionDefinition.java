package com.siglet.pipeline.processor.common.action;


import com.siglet.config.item.ProcessorItem;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.pipeline.processor.common.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class ActionDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(ProcessorItem::setConfig, ProcessorItem::setConfigLocation, "config",
                strictObject(ActionConfig::new,
                        requiredProperty(ActionConfig::setAction,
                                ActionConfig::setActionLocation,
                                "action", text())));
    }
}
