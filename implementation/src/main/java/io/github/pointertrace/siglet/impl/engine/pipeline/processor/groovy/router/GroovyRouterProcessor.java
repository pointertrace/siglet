package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import groovy.lang.Script;
import io.github.pointertrace.siglet.api.*;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.BaseGroovyProcessor;
import io.github.pointertrace.siglet.impl.eventloop.processor.ContextImpl;
import io.github.pointertrace.siglet.impl.eventloop.processor.Eventloop;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorFactory;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultFactoryImpl;

import java.util.List;

public class GroovyRouterProcessor implements Processor {

    private ProcessorNode node;

    private final Eventloop<Void> eventLoop;

    GroovyRouterProcessor(SigletContext sigletContext, ProcessorNode node) {
        this(node.getName(), getConfig(node).getDefaultRoute().getValue(), getConfig(node).getRoutes(),
                SignalCapabilities.of(node.getDescription().getType().getValue()),
                sigletContext.getConfig().getQueueSize(node.getDescription()),
                sigletContext.getConfig().getThreadPoolSize(node.getDescription()));
        this.node = node;
    }

    GroovyRouterProcessor(String name, String defaultRoute, List<RouteConfig> routes, SignalCapabilities signalCapabilities,
                          int queueCapacity, int threadPoolSize) {
        ContextImpl<Void> ctx = new ContextImpl<>(null);
        this.eventLoop = new Eventloop<>(name, createProcessorFactory(defaultRoute, routes),
                ctx, signalCapabilities, signalCapabilities, queueCapacity, threadPoolSize);
    }

    private static GroovyRouterConfig getConfig(ProcessorNode node) {
        return ((GroovyRouterConfig) node.getDescription().getConfig());
    }

    private static <T> ProcessorFactory<T> createProcessorFactory(String defaultDestination, List<RouteConfig> routes) {
        return ctx -> new GroovyRouterBaseGroovyProcessor<>(ctx, ResultFactoryImpl.INSTANCE, defaultDestination, routes);
    }

    @Override
    public ProcessorNode getNode() {
        return node;
    }

    @Override
    public void start() {
        eventLoop.start();
    }

    public Context<Void> getContext() {
        return eventLoop.getContext();
    }

    @Override
    public void stop() {
        eventLoop.stop();
    }

    @Override
    public State getState() {
        return eventLoop.getState();
    }

    @Override
    public String getName() {
        return eventLoop.getName();
    }

    @Override
    public boolean send(Signal signal) {
        return eventLoop.send(signal);
    }

    @Override
    public SignalCapabilities getIncomingCapabilities() {
        return eventLoop.getIncomingCapabilities();
    }

    @Override
    public void connect(SignalDestination destination) {
        if (! eventLoop.getOutgoingCapabilities().isAbleToSend(destination.getIncomingCapabilities())) {
            throw new SigletError(String.format("Cannot connect processor [%s] to [%s] because they have incompatible " +
                            "signal capabilities. Processor generates [%s] and destination expects [%s]",
                    eventLoop.getName(), destination.getName(), eventLoop.getOutgoingCapabilities().print(),
                    destination.getIncomingCapabilities().print()));
        }

        eventLoop.connect(destination);
    }

    @Override
    public SignalCapabilities getOutgoingCapabilities() {
        return eventLoop.getOutgoingCapabilities();
    }

    private static class GroovyRouterBaseGroovyProcessor<T> extends BaseGroovyProcessor<T> {

        private final String defaultDestination;

        private final List<CompiledRoute> compiledRoutes;

        public GroovyRouterBaseGroovyProcessor(Context<T> context, ResultFactory resultFactory,
                                               String defaultDestination, List<RouteConfig> routes) {
            super(context, resultFactory);
            this.defaultDestination = defaultDestination;
            this.compiledRoutes = routes.stream()
                    .map(r -> new CompiledRoute(getCompiler().compile(r.getWhen().getValue()), r.getTo().getValue()))
                    .toList();
        }

        @Override
        protected Result process(Signal signal, Context<T> context, ResultFactory resultFactory) {
            for (CompiledRoute compiledRoute : compiledRoutes) {
                getCompiler().prepareScript(compiledRoute.predicate(), signal, context);
                Boolean predicate = (Boolean) compiledRoute.predicate().run();
                if (predicate) {
                    return resultFactory.proceed(compiledRoute.destination());
                }
            }
            return resultFactory.proceed(defaultDestination);
        }
    }

    private record CompiledRoute(Script predicate, String destination) {
    }

}
