package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;

import java.util.ArrayList;
import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class SpanletGroovyRouterProcessorType implements ProcessorType<GroovyRouterConfig> {

    @Override
    public String getType() {
        return "spanlet-groovy-router";
    }

    @Override
    public ConfigurationFactory<GroovyRouterConfig> getConfigurationFactory() {
        return ConfigurationFactory.of(
                List.of(property("default", GroovyRouterConfig::setDefaultRoute, stringValueObject()),
                        property("routes", GroovyRouterConfig::setRoutes,
                                array(ArrayList::new, arrayItem(List::add, object(RouteConfig::new)
                                       .addProperty(property("to", RouteConfig::setTo, stringValueObject()))
                                        .addProperty(property("when", RouteConfig::setWhen, stringValueObject()))))
                        )
                ), GroovyRouterConfig.class);
    }

    @Override
    public ComponentCreator<ProcessorNode> getComponentCreator() {
        return (context, node) -> {
            if (node.getDescription().getConfig() instanceof GroovyRouterConfig) {
                return new GroovyRouterProcessor(context, node);
            } else {
                throw new SigletError(String.format("for groovy action type config must be a %s",
                        node.getDescription().getConfig().getClass().getName()));
            }
        };
    }

}
