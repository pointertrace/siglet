package com.siglet.config.item.repository.routecreator;

import org.apache.camel.Predicate;
import org.apache.camel.Processor;

public interface RouteCreator {

    RouteCreator addReceiver(String uri);

    void addExporter(String uri);

    RouteCreator addProcessor(Processor processor);

    RouteCreator addFilter(Predicate predicate);

    RouteCreator startMulticast();

    RouteCreator traceAggregator(String completionExpression, Long inactiveTimeoutMillis, Long timeoutMillis);

    void endMulticast();

    RouteCreator startChoice();

    RouteCreator addChoice(Predicate predicate);

    RouteCreator endChoice();

}
