package com.siglet.config.item.repository.routecreator;

import com.siglet.SigletError;
import com.siglet.camel.component.otelgrpc.aggregator.SignalAggregationStrategy;
import com.siglet.config.item.GrpcExporterUri;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.AggregateDefinition;
import org.apache.camel.model.ChoiceDefinition;

import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.camel.language.constant.ConstantLanguage.constant;

public class ChoiceRouteCreator implements RouteCreator {

    private final RouteBuilder routeBuilder;

    private final ChoiceDefinition choiceDefinition;

    private final AtomicInteger seed;

    private final RouteLink choiceRouterLink;

    public ChoiceRouteCreator(AtomicInteger seed, RouteBuilder routeBuilder, ChoiceDefinition choiceDefinition) {
        this.seed = seed;
        this.routeBuilder = routeBuilder;
        this.choiceDefinition = choiceDefinition;
        this.choiceRouterLink = new RouteLink(seed, "choice");
    }

    @Override
    public RouteCreator addReceiver(String uri, String routeName) {
        throw new SigletError("cannot be called from a ChoiceRouteCreator");
    }

    @Override
    public void addExporter(String uri) {
        if (uri.startsWith("otelgrpc")) {
            AggregateDefinition aggregateDefinition = choiceDefinition.aggregate(constant(true), new SignalAggregationStrategy());
            GrpcExporterUri grpcExporterUri = GrpcExporterUri.of(uri);
            if (grpcExporterUri.getBatchSizeInSignals() != null) {
                aggregateDefinition = aggregateDefinition.completionSize(grpcExporterUri.getBatchSizeInSignals());
            }
            if (grpcExporterUri.getBatchTimeoutInMillis() != null) {
                aggregateDefinition = aggregateDefinition.completionTimeout(grpcExporterUri.getBatchTimeoutInMillis());
            }
            aggregateDefinition.end().to(uri);
        } else {
            choiceDefinition.to(uri);
        }
    }

    @Override
    public RouteCreator addProcessor(Processor processor) {
        throw new SigletError("cannot be called from a ChoiceRouteCreator");
    }

    @Override
    public RouteCreator addFilter(Predicate predicate) {
        throw new SigletError("cannot be called from a ChoiceRouteCreator");
    }

    @Override
    public RouteCreator startMulticast() {
        throw new SigletError("cannot be called from a ChoiceRouteCreator");
    }

    @Override
    public RouteCreator traceAggregator(String completionExpression, Long inactiveTimeoutMillis, Long timeoutMillis) {
        throw new SigletError("cannot be called from a ChoiceRouteCreator");
    }

    @Override
    public void endMulticast() {
        throw new SigletError("cannot be called from a ChoiceRouteCreator");
    }

    @Override
    public RouteCreator startChoice() {
        throw new SigletError("cannot be called from a ChoiceRouteCreator");
    }

    @Override
    public RouteCreator addChoice(Predicate predicate) {
        choiceRouterLink.increment();
        choiceDefinition.when(predicate).to(choiceRouterLink.getLink());
        return new SimpleRouteCreator(seed, routeBuilder, routeBuilder.from(choiceRouterLink.getLink()));
    }

    @Override
    public RouteCreator endChoice() {
        choiceRouterLink.increment();
        choiceDefinition.otherwise().to(choiceRouterLink.getLink()).end();
        return new SimpleRouteCreator(seed, routeBuilder, routeBuilder.from(choiceRouterLink.getLink()));
    }

}
