package com.siglet.pipeline.processor.common.router;

import com.siglet.config.item.Describable;
import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

import java.util.List;

public class RouterConfig implements Located, Describable {

    private Location location;

    private List<Route> routes;

    private Location routesLocation;

    private String defaultRoute;

    private Location defaultRouteLocation;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
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
        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getLocation().describe());
        sb.append("  routerConfig:");
        sb.append("\n");

        sb.append(prefix(level + 1));
        sb.append(getRoutesLocation().describe());
        sb.append("  routes:\n");
        for (Route route : getRoutes()) {
            sb.append(route.describe(level + 2));
            sb.append("\n");
        }

        sb.append(prefix(level + 1));
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
