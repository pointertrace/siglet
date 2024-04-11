package com.siglet.config.item.repository.routecreator;

import org.apache.camel.Processor;

public interface RouteCreator {

    RouteCreator addReceiver(String uri);

    void addExporter(String uri);

    RouteCreator addProcessor(Processor processor);

    RouteCreator addFilter(String groovyExpression);

    RouteCreator startMulticast();

    void endMulticast();

    RouteCreator startChoice();

    RouteCreator addChoice(String expression);

    RouteCreator endChoice(String expression);

}
