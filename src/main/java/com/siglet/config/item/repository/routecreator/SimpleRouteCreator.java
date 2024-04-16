package com.siglet.config.item.repository.routecreator;

import com.siglet.SigletError;
import com.siglet.data.Clonable;
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
        routeDefinition.filter(predicate).to("direct:y" + num).end();
        return new SimpleRouteCreator(routeBuilder, routeBuilder.from("direct:y" + num));
    }

    public RouteCreator startMulticast() {
        return new MulticastRouteCreator(routeBuilder, routeDefinition.multicast().onPrepare(new CloneProcessor()));
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
