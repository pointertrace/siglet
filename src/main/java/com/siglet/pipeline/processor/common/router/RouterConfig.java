package com.siglet.pipeline.processor.common.router;

import com.siglet.config.item.Item;
import com.siglet.config.located.Location;

import java.util.List;

// todo testar
public class RouterConfig extends Item {

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
}
