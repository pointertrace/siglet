package com.siglet.config;

import org.apache.camel.builder.RouteBuilder;

public class Config {

    private final RouteBuilder routeBuilder;

    public Config(RouteBuilder routeBuilder) {
        this.routeBuilder = routeBuilder;
    }

    public RouteBuilder getRouteBuilder() {
        return routeBuilder;
    }
}
