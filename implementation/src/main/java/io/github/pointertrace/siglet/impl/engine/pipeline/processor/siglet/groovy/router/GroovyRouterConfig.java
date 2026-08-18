package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.router;


import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ValidatableConfig;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroovyRouterConfig implements ValidatableConfig<ProcessorDescriptor> {

    private List<RouteConfig> routes;

    private StringValue defaultRoute;

    public List<RouteConfig> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteConfig> routeConfigs) {
        this.routes = routeConfigs;
    }

    public void setDefaultRoute(StringValue defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    public StringValue getDefaultRoute() {
        return defaultRoute;
    }


    @Override
    public void validate(ProcessorDescriptor descriptor) {
        Set<String> destinations = descriptor.getTo().stream()
                .map(StringValue::getValue)
                .collect(Collectors.toSet());

        Set<String> routes = this.routes.stream()
                .map(RouteConfig::getTo)
                .map(StringValue::getValue)
                .collect(Collectors.toSet());
        routes.add(defaultRoute.getValue());

        Set<String> inDestinationsNotInRoutes = new HashSet<>(destinations);
        inDestinationsNotInRoutes.removeAll(routes);

        Set<String> inRoutesNotInDestinations = new HashSet<>(routes);
        inRoutesNotInDestinations.removeAll(destinations);

        Set<String> errors = new HashSet<>();
        if (!inDestinationsNotInRoutes.isEmpty()) {
            errors.add(String.format("some destinations defined in 'to' (%s) that are not defined as a route",
                    String.join(",", inDestinationsNotInRoutes)));
        }
        if (!inRoutesNotInDestinations.isEmpty()) {
            errors.add(String.format("some routes (%s) that are not defined as processor destination defined in 'to'",
                    String.join(",", inRoutesNotInDestinations)));
        }
        if (!errors.isEmpty()) {
            throw new SigletError(String.format("The processor '%s' at (%s:%s) has %s",
                    descriptor.getName().getValue(), descriptor.getLocation().getLine(),
                    descriptor.getLocation().getColumn(), String.join(" and ", errors)));
        }
    }
}
