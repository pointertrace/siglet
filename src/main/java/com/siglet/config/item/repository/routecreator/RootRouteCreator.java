package com.siglet.config.item.repository.routecreator;

import com.siglet.SigletError;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import java.util.concurrent.atomic.AtomicInteger;

public class RootRouteCreator implements RouteCreator {

    private static final String CANNOT_BE_CALLED_ERROR_MESSAGE = "cannot be called from a RootRouteCreator";
    private final RouteBuilder routeBuilder;

    private final AtomicInteger seed = new AtomicInteger(1);


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
    public RouteCreator addReceiver(String uri, String routeName) {
        return new SimpleRouteCreator(seed, routeBuilder, routeBuilder.from(uri).routeId(routeName));
    }

    @Override
    public void addExporter(String uri) {
        throw new SigletError(CANNOT_BE_CALLED_ERROR_MESSAGE);
    }

    @Override
    public RouteCreator addProcessor(Processor processor) {
        throw new SigletError(CANNOT_BE_CALLED_ERROR_MESSAGE);
    }

    @Override
    public RouteCreator addFilter(Predicate predicate) {
        throw new SigletError(CANNOT_BE_CALLED_ERROR_MESSAGE);
    }

    @Override
    public RouteCreator startMulticast() {
        throw new SigletError(CANNOT_BE_CALLED_ERROR_MESSAGE);
    }

    @Override
    public RouteCreator traceAggregator(String completionExpression, Long inactiveTimeoutMillis, Long timeoutMillis) {
        throw new SigletError(CANNOT_BE_CALLED_ERROR_MESSAGE);
    }

    @Override
    public void endMulticast() {
        throw new SigletError(CANNOT_BE_CALLED_ERROR_MESSAGE);
    }

    @Override
    public RouteCreator startChoice() {
        throw new SigletError(CANNOT_BE_CALLED_ERROR_MESSAGE);
    }

    @Override
    public RouteCreator addChoice(Predicate predicate) {
        throw new SigletError(CANNOT_BE_CALLED_ERROR_MESSAGE);
    }

    @Override
    public RouteCreator endChoice() {
        throw new SigletError(CANNOT_BE_CALLED_ERROR_MESSAGE);
    }



}
