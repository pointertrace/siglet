package com.siglet.spanlet.router;


import com.siglet.config.builder.SpanletBuilder;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.spanlet.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class RouterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(SpanletBuilder::setConfig, "config",
                strictObject(RouterConfigBuilder::new,
                        requiredProperty(RouterConfigBuilder::setRouters, "routes",
                                array(strictObject(RouterItem::new,
                                        requiredProperty(RouterItem::setTo, "to", text()),
                                        requiredProperty(RouterItem::setExpression, "when", text())

                                )))));
    }
}
