package com.siglet.spanlet.router;

import com.siglet.config.item.repository.Node;

import java.util.List;

public class RouterConfig {

    private List<Route> routes;

    private String defaultRoute;


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

}
