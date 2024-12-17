package com.siglet.config.item.repository.routecreator;

import com.siglet.SigletError;
import com.siglet.camel.component.otelgrpc.aggregator.SignalAggregationStrategy;
import com.siglet.config.item.GrpcExporterUri;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.AggregateDefinition;
import org.apache.camel.model.MulticastDefinition;

import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.camel.language.constant.ConstantLanguage.constant;

public class MulticastRouteCreator implements RouteCreator {

    private final RouteBuilder routeBuilder;

    private final MulticastDefinition multicastDefinition;

    private final RouteLink multicastRouteLink;


    private final AtomicInteger seed;

    // TODO: juntar com SimpleRouteCreator
    public MulticastRouteCreator(AtomicInteger seed,RouteBuilder routeBuilder, MulticastDefinition multicastDefinition) {
        this.seed = seed;
        this.routeBuilder = routeBuilder;
        this.multicastDefinition = multicastDefinition;
        this.multicastRouteLink = new RouteLink(seed, "multicast");
    }

    @Override
    public RouteCreator addReceiver(String uri, String routeName) {
        throw new SigletError("cannot be called from a MulticastRouteCreator");
    }

    @Override
    public void addExporter(String uri) {
        if (uri.startsWith("otelgrpc")) {
            AggregateDefinition aggregateDefinition = multicastDefinition.aggregate(constant(true), new SignalAggregationStrategy());
            GrpcExporterUri grpcExporterUri = GrpcExporterUri.of(uri);
            if (grpcExporterUri.getBatchSizeInSignals() != null) {
                aggregateDefinition = aggregateDefinition.completionSize(grpcExporterUri.getBatchSizeInSignals());
            }
            if (grpcExporterUri.getBatchTimeoutInMillis() != null) {
                aggregateDefinition = aggregateDefinition.completionTimeout(grpcExporterUri.getBatchTimeoutInMillis());
            }
            aggregateDefinition.end().to(uri);
        } else {
            multicastDefinition.to(uri);
        }
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
