package com.siglet.config.item.repository.routecreator;

import com.siglet.SigletError;
import com.siglet.data.CloneableAdapter;
import com.siglet.pipeline.common.filter.GroovyPredicate;
import com.siglet.pipeline.spanlet.traceaggregator.TraceAggregationStrategy;
import com.siglet.pipeline.spanlet.traceaggregator.TraceAggregatorCorrelationExpression;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleRouteCreator implements RouteCreator {

    private final RouteBuilder routeBuilder;

    private final RouteDefinition routeDefinition;

    private final AtomicInteger seed;

    private final RouteLink filterRouterLink;

    private final RouteLink aggregatorRouteLink;

    public SimpleRouteCreator(AtomicInteger seed, RouteBuilder routeBuilder, RouteDefinition routeDefinition) {
        this.seed = seed;
        this.routeBuilder = routeBuilder;
        this.routeDefinition = routeDefinition;
        this.filterRouterLink = new RouteLink(seed, "filter");
        this.aggregatorRouteLink = new RouteLink(seed, "aggregator");
    }


    @Override
    public RouteCreator addReceiver(String uri, String routeName) {
        throw new SigletError("cannot be called from a SimpleRouteCreator");
    }

    @Override
    public void addExporter(String uri) {
        routeDefinition.to(uri);
    }

    public RouteCreator addProcessor(Processor processor) {
        return new SimpleRouteCreator(seed, routeBuilder, routeDefinition.process(processor));
    }

    @Override
    public RouteCreator addFilter(Predicate predicate) {
        filterRouterLink.increment();
        routeDefinition.filter(predicate).to(filterRouterLink.getLink()).end();
        return new SimpleRouteCreator(seed, routeBuilder, routeBuilder.from(filterRouterLink.getLink()));
    }

    public RouteCreator startMulticast() {
        return new MulticastRouteCreator(seed, routeBuilder, routeDefinition, routeDefinition.multicast()
                .onPrepare(new CloneProcessor()));
    }

    @Override
    public RouteCreator traceAggregator(String completionExpression, Long inactiveTimeoutMillis, Long timeoutMillis) {
        aggregatorRouteLink.increment();
        var aggregate = routeDefinition
                .aggregate(new TraceAggregationStrategy())
                .expression(new TraceAggregatorCorrelationExpression());

        if (completionExpression != null) {
            aggregate = aggregate.completion(new GroovyPredicate(completionExpression));
        }
        if (inactiveTimeoutMillis != null) {
            aggregate = aggregate.completionTimeout(inactiveTimeoutMillis);
        }
        if (timeoutMillis != null) {
            aggregate = aggregate.completionTimeout(timeoutMillis);
        }
        aggregate.to(aggregatorRouteLink.getLink());
        return new SimpleRouteCreator(seed, routeBuilder, routeBuilder.from(aggregatorRouteLink.getLink()));
    }

    public void endMulticast() {
        throw new SigletError("cannot be called from a SimpleRouteCreator");
    }

    @Override
    public RouteCreator startChoice() {
        return new ChoiceRouteCreator(seed, routeBuilder, routeDefinition.choice());
    }

    @Override
    public RouteCreator addChoice(Predicate predicate) {
        throw new SigletError("cannot be called from a SimpleRouteCreator");
    }

    @Override
    public RouteCreator endChoice() {
        throw new SigletError("cannot be called from a SimpleRouteCreator");
    }

    public static class CloneProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            Object body = exchange.getIn().getBody();
            if (body instanceof CloneableAdapter<?> cloneableAdapter) {
                exchange.getIn().setBody(cloneableAdapter.cloneAdapter());
            } else {
                throw new SigletError("type " + body.getClass().getName() + " is not clonable!");
            }
        }
    }
}
