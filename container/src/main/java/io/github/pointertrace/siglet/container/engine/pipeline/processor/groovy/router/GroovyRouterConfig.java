package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.router;


import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;

import java.util.List;

public class GroovyRouterConfig implements Located, Describable {

    private Location location;

    private List<RouteConfig> routeConfigs;

    private Location routesLocation;

    private String defaultRoute;

    private Location defaultRouteLocation;

    public List<RouteConfig> getRoutes() {
        return routeConfigs;
    }

    public void setRoutes(List<RouteConfig> routeConfigs) {
        this.routeConfigs = routeConfigs;
    }

    public void setDefaultRoute(String defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    public String getDefaultRoute() {
        return defaultRoute;
    }

    public Location getDefaultRouteLocation() {
        return defaultRouteLocation;
    }

    public void setDefaultRouteLocation(Location defaultRouteLocation) {
        this.defaultRouteLocation = defaultRouteLocation;
    }

    public Location getRoutesLocation() {
        return routesLocation;
    }

    public void setRoutesLocation(Location routesLocation) {
        this.routesLocation = routesLocation;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(getLocation().describe());
        sb.append("  groovyRouterConfig:");
        sb.append("\n");

        sb.append(Describable.prefix(level + 1));
        sb.append(getRoutesLocation().describe());
        sb.append("  routes:\n");
        for (RouteConfig routeConfig : getRoutes()) {
            sb.append(routeConfig.describe(level + 2));
            sb.append("\n");
        }

        sb.append(Describable.prefix(level + 1));
        sb.append(getDefaultRouteLocation().describe());
        sb.append("  defaultRoute: ");
        sb.append(getDefaultRoute());

        return sb.toString();

    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }
}
