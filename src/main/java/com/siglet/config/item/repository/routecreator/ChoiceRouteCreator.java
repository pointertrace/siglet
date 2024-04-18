package com.siglet.config.item.repository.routecreator;

import com.siglet.spanlet.filter.GroovyPredicate;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ChoiceDefinition;

public class ChoiceRouteCreator implements RouteCreator {


    private final RouteBuilder routeBuilder;

    private static int i;

    private final ChoiceDefinition choiceDefinition;

    public ChoiceRouteCreator(RouteBuilder routeBuilder, ChoiceDefinition choiceDefinition) {
        this.routeBuilder = routeBuilder;
        this.choiceDefinition = choiceDefinition;
    }

    @Override
    public RouteCreator addReceiver(String uri) {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public void addExporter(String uri) {
        choiceDefinition.to(uri);
    }

    @Override
    public RouteCreator addProcessor(Processor processor) {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public RouteCreator addFilter(Predicate predicate) {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public RouteCreator startMulticast() {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public RouteCreator traceAggregator(String completionExpression, Long inactiveTimeoutMillis, Long timeoutMillis) {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public void endMulticast() {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public RouteCreator startChoice() {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public RouteCreator addChoice(Predicate predicate) {
        i++;
        choiceDefinition.when(predicate).to("direct:c" + i);
        return new SimpleRouteCreator(routeBuilder, routeBuilder.from("direct:c"+i));
    }

    @Override
    public RouteCreator endChoice() {
        i++;
        choiceDefinition.otherwise().to("direct:c"+i).end();
        return new SimpleRouteCreator(routeBuilder, routeBuilder.from("direct:c"+i));
    }
}
