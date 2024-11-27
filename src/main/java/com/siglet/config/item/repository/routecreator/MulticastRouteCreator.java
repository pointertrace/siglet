package com.siglet.config.item.repository.routecreator;

import com.siglet.SigletError;
import com.siglet.pipeline.common.filter.GroovyPredicate;
import com.siglet.pipeline.spanlet.traceaggregator.TraceAggregationStrategy;
import com.siglet.pipeline.spanlet.traceaggregator.TraceAggregatorCorrelationExpression;
import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.RouteDefinition;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class MulticastRouteCreator implements RouteCreator {

    private final RouteDefinition routeDefinition;

    private final RouteBuilder routeBuilder;

    private final MulticastDefinition multicastDefinition;

    private final RouteLink filterRouterLink;

    private final RouteLink aggregatorRouteLink;

    private final RouteLink multicastRouteLink;


    private final AtomicInteger seed;

    // TODO: juntar com SimpleRouteCreator
    public MulticastRouteCreator(AtomicInteger seed,RouteBuilder routeBuilder, RouteDefinition routeDefinition, MulticastDefinition multicastDefinition) {
        this.seed = seed;
        this.routeDefinition = routeDefinition;
        this.routeBuilder = routeBuilder;
        this.multicastDefinition = multicastDefinition;
        this.filterRouterLink = new RouteLink(seed, "filter");
        this.aggregatorRouteLink = new RouteLink(seed, "aggregator");
        this.multicastRouteLink = new RouteLink(seed, "multicast");
    }

    @Override
    public RouteCreator addReceiver(String uri, String routeName) {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public void addExporter(String uri) {
        multicastDefinition.to(uri);
    }

    public RouteCreator addProcessor(Processor processor) {
        multicastRouteLink.increment();
        multicastDefinition.to(multicastRouteLink.getLink());
        return new SimpleRouteCreator(seed, routeBuilder,routeBuilder.from(multicastRouteLink.getLink()))
                .addProcessor(processor);
    }

    @Override
    public RouteCreator addFilter(Predicate predicate) {
        multicastRouteLink.increment();
        multicastDefinition.to(multicastRouteLink.getLink());
        return new SimpleRouteCreator(seed, routeBuilder,routeBuilder.from(multicastRouteLink.getLink()))
                .addFilter(predicate);
    }

    @Override
    public RouteCreator startMulticast() {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public RouteCreator traceAggregator(String completionExpression, Long inactiveTimeoutMillis, Long timeoutMillis) {
        multicastRouteLink.increment();
        multicastDefinition.to(multicastRouteLink.getLink());
        return new SimpleRouteCreator(seed, routeBuilder,routeBuilder.from(multicastRouteLink.getLink()))
                .traceAggregator(completionExpression, inactiveTimeoutMillis, timeoutMillis);
    }

    @Override
    public void endMulticast() {
        multicastDefinition.end();
    }

    @Override
    public RouteCreator startChoice() {
        multicastRouteLink.increment();
        multicastDefinition.to(multicastRouteLink.getLink());
        return new SimpleRouteCreator(seed, routeBuilder,routeBuilder.from(multicastRouteLink.getLink()))
                .startChoice();
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
