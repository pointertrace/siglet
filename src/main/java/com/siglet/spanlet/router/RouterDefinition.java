package com.siglet.spanlet.router;


import com.siglet.config.item.ProcessorItem;
import com.siglet.config.item.SpanletItem;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.spanlet.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class RouterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(ProcessorItem::setConfig, "config",
                strictObject(RouterConfig::new,
                        requiredProperty(RouterConfig::setDefaultRoute, "default",text()),
                        requiredProperty(RouterConfig::setRoutes, "routes",
                                array(strictObject(Route::new,
                                        requiredProperty(Route::setTo, "to", text()),
                                        requiredProperty(Route::setExpression, "when", text())

                                )))));
    }
}
