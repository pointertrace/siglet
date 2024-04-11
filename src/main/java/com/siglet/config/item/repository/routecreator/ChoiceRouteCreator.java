package com.siglet.config.item.repository.routecreator;

import com.siglet.spanlet.filter.GroovyPredicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.MulticastDefinition;

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
//        return new ChoiceRouteCreator(routeBuilder, choiceDefinition.process(processor));
    }

    @Override
    public RouteCreator addFilter(String groovyExpression) {
        throw new IllegalStateException("can only be called in multicast");
//        i++;
//        choiceDefinition.filter(new GroovyPredicate(groovyExpression)).to("direct:z" + i).end();
//        return new SimpleRouteCreator(routeBuilder,routeBuilder.from("direct:z"+i));
    }

    @Override
    public RouteCreator startMulticast() {
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
    public RouteCreator addChoice(String expression) {
        i++;
        choiceDefinition.when(new GroovyPredicate(expression)).to("direct:c" + i);
        return new SimpleRouteCreator(routeBuilder, routeBuilder.from("direct:c"+i));
    }

    @Override
    public RouteCreator endChoice(String expression) {
        i++;
        choiceDefinition.otherwise().to("direct:c"+i).end();
        return new SimpleRouteCreator(routeBuilder, routeBuilder.from("direct:c"+i));
    }
}
