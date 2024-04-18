package com.siglet.config.item.repository.routecreator;

import com.siglet.SigletError;
import com.siglet.data.Clonable;
import com.siglet.spanlet.GroovyPropertySetter;
import com.siglet.spanlet.filter.GroovyPredicate;
import com.siglet.spanlet.traceaggregator.TraceAggregationStrategy;
import com.siglet.spanlet.traceaggregator.TraceAggregatorCorrelationExpression;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

public class SimpleRouteCreator implements RouteCreator {

    private final RouteBuilder routeBuilder;

    private final RouteDefinition routeDefinition;

    private static int num = 10000;

    public SimpleRouteCreator(RouteBuilder routeBuilder, RouteDefinition routeDefinition) {
        this.routeBuilder = routeBuilder;
        this.routeDefinition = routeDefinition;
    }

    @Override
    public RouteCreator addReceiver(String uri) {
        throw new IllegalStateException("can only be called in root");
    }

    @Override
    public void addExporter(String uri) {
        routeDefinition.to(uri);
    }

    public RouteCreator addProcessor(Processor processor) {
        return new SimpleRouteCreator(routeBuilder, routeDefinition.process(processor));
    }

    @Override
    public RouteCreator addFilter(Predicate predicate) {
        num++;
        routeDefinition.filter(predicate).to("direct:filter" + num).end();
        return new SimpleRouteCreator(routeBuilder, routeBuilder.from("direct:filter" + num));
    }

    public RouteCreator startMulticast() {
        return new MulticastRouteCreator(routeBuilder, routeDefinition.multicast().onPrepare(new CloneProcessor()));
    }

    @Override
    public RouteCreator traceAggregator(String completionExpression, Long inactiveTimeoutMillis, Long timeoutMillis) {

        num ++;
        var aggregate = routeDefinition
                .aggregate(new TraceAggregationStrategy())
                .expression(new TraceAggregatorCorrelationExpression());

        if (completionExpression != null) {
            aggregate = aggregate.completion(new GroovyPredicate(completionExpression, GroovyPropertySetter.trace));
        }
        if (inactiveTimeoutMillis != null) {
            aggregate = aggregate.completionTimeout(inactiveTimeoutMillis);
        }
        if (timeoutMillis != null) {
            aggregate = aggregate.completionTimeout(timeoutMillis);
        }
        aggregate.to("direct:aggregate"+ num);
        return new SimpleRouteCreator(routeBuilder, routeBuilder.from("direct:aggregate"+ num));
    }

    public void endMulticast() {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public RouteCreator startChoice() {
        return new ChoiceRouteCreator(routeBuilder, routeDefinition.choice());
    }

    @Override
    public RouteCreator addChoice(Predicate predicate) {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public RouteCreator endChoice() {
        throw new IllegalStateException("can only be called in multicast");
    }

    public static class CloneProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            Object body = exchange.getIn().getBody();
            if (body instanceof Clonable clonable) {
                exchange.getIn().setBody(clonable.clone());
            } else {
                throw new SigletError("type " + body.getClass().getName() + " is not clonable!");
            }
        }
    }
}
