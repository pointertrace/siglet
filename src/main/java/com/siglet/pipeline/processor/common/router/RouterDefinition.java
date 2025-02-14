package com.siglet.pipeline.processor.common.router;


import com.siglet.config.item.SigletItem;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.pipeline.processor.common.ConfigDefinition;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class RouterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(SigletItem::setConfig, SigletItem::setConfigLocation, "config",
                strictObject(RouterConfig::new,
                        requiredProperty(RouterConfig::setDefaultRoute,
                                RouterConfig::setDefaultRouteLocation, "default", text()),
                        requiredProperty(RouterConfig::setRoutes, RouterConfig::setRoutesLocation,
                                "routes",
                                array(strictObject(Route::new,
                                        requiredProperty(Route::setTo, Route::setToLocation,
                                                "to", text()),
                                        requiredProperty(Route::setWhen,
                                                Route::setWhenLocation, "when",
                                                text()))))));
    }
}
