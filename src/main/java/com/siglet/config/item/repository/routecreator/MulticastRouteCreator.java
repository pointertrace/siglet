package com.siglet.config.item.repository.routecreator;

import com.siglet.SigletError;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.MulticastDefinition;

public class MulticastRouteCreator implements RouteCreator {

    private final MulticastDefinition multicastDefinition;

    public MulticastRouteCreator(MulticastDefinition multicastDefinition) {
        this.multicastDefinition = multicastDefinition;
    }

    @Override
    public RouteCreator addReceiver(String uri) {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public void addExporter(String uri) {
        multicastDefinition.to(uri);
    }

    public RouteCreator addProcessor(Processor processor) {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public RouteCreator addFilter(Predicate predicate) {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public RouteCreator startMulticast() {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public RouteCreator traceAggregator(String completionExpression, Long inactiveTimeoutMillis, Long timeoutMillis) {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public void endMulticast() {
        multicastDefinition.end();
    }

    @Override
    public RouteCreator startChoice() {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public RouteCreator addChoice(Predicate predicate) {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public RouteCreator endChoice() {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }
}
