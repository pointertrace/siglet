package com.siglet.config.item.repository.routecreator;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class RootRouteCreator implements RouteCreator {

    private final RouteBuilder routeBuilder;

    public RootRouteCreator() {
        this.routeBuilder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            }
        };
    }

    public RouteBuilder getRouteBuilder() {
        return routeBuilder;
    }

    @Override
    public RouteCreator addReceiver(String uri) {
        return new SimpleRouteCreator(routeBuilder, routeBuilder.from(uri));
    }

    @Override
    public void addExporter(String uri) {
        throw new IllegalStateException("can only be called in root");
    }

    @Override
    public RouteCreator addProcessor(Processor processor) {
        throw new IllegalStateException("can only be called in root");
    }

    @Override
    public RouteCreator addFilter(String groovyExpression) {
        throw new IllegalStateException("can only be called in root");
    }

    @Override
    public RouteCreator startMulticast() {
        throw new IllegalStateException("can only be called in root");
    }

    @Override
    public void endMulticast() {
        throw new IllegalStateException("can only be called in root");
    }

    @Override
    public RouteCreator startChoice() {
        throw new IllegalStateException("can only be called in root");
    }

    @Override
    public RouteCreator addChoice(String expression) {
        throw new IllegalStateException("can only be called in root");
    }

    @Override
    public RouteCreator endChoice(String expression) {
        throw new IllegalStateException("can only be called in root");
    }
}
