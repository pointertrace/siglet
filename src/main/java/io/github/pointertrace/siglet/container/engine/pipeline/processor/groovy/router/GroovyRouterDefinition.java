package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.router;


import io.github.pointertrace.siglet.container.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siget.parser.NodeChecker;

import static io.github.pointertrace.siget.parser.schema.SchemaFactory.*;


public class GroovyRouterDefinition implements ConfigDefinition {


    @Override
    public NodeChecker getChecker() {
        return requiredProperty(ProcessorConfig::setConfig, ProcessorConfig::setConfigLocation, "config",
                strictObject(GroovyRouterConfig::new,
                        requiredProperty(GroovyRouterConfig::setDefaultRoute,
                                GroovyRouterConfig::setDefaultRouteLocation, "default", text()),
                        requiredProperty(GroovyRouterConfig::setRoutes, GroovyRouterConfig::setRoutesLocation,
                                "routes",
                                array(strictObject(RouteConfig::new,
                                        requiredProperty(RouteConfig::setTo, RouteConfig::setToLocation,
                                                "to", text()),
                                        requiredProperty(RouteConfig::setWhen,
                                                RouteConfig::setWhenLocation, "when",
                                                text()))))));
    }
}
