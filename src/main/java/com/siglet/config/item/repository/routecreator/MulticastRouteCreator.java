package com.siglet.config.item.repository.routecreator;

import com.siglet.spanlet.filter.GroovyPredicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.MulticastDefinition;

public class MulticastRouteCreator implements RouteCreator {

    private final RouteBuilder routeBuilder;

    private static int i;

    private final MulticastDefinition multicastDefinition;

    public MulticastRouteCreator(RouteBuilder routeBuilder, MulticastDefinition multicastDefinition) {
        this.routeBuilder = routeBuilder;
        this.multicastDefinition = multicastDefinition;
    }

    @Override
    public RouteCreator addReceiver(String uri) {
        throw new IllegalStateException("can only be called in multicast");
    }

    @Override
    public void addExporter(String uri) {
        multicastDefinition.to(uri);
    }

    public RouteCreator addProcessor(Processor processor) {
        i++;
        multicastDefinition.to("direct:x" + i);
        return new SimpleRouteCreator(routeBuilder, routeBuilder.from("direct:x" + i).process(processor));
    }

    @Override
    public RouteCreator addFilter(String groovyExpression) {
        i++;
        multicastDefinition.filter(new GroovyPredicate(groovyExpression)).to("direct:x" + i).end();
        return new SimpleRouteCreator(routeBuilder,routeBuilder.from("direct:x"+i));
    }

    @Override
    public RouteCreator startMulticast() {
        throw new IllegalStateException("can only be called in simpleroute");
    }

    @Override
    public void endMulticast() {
        multicastDefinition.end();
    }

    @Override
    public RouteCreator startChoice() {
        throw new IllegalStateException("can only be called in simpleroute");
    }

    @Override
    public RouteCreator addChoice(String expression) {
        throw new IllegalStateException("can only be called in simpleroute");
    }

    @Override
    public RouteCreator endChoice(String expression) {
        throw new IllegalStateException("can only be called in simpleroute");
    }
}
