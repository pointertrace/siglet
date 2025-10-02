package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import groovy.lang.Script;
import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.BaseGroovyBaseEventloopProcessor;
import io.github.pointertrace.siglet.impl.eventloop.processor.ContextImpl;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorEventloop;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorFactory;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultFactoryImpl;

import java.util.List;
import java.util.Set;

public abstract class BaseGroovyRouterProcessor implements Processor {

    private ProcessorNode node;

    private final ProcessorEventloop<Void> processorEventloop;

    BaseGroovyRouterProcessor(io.github.pointertrace.siglet.impl.engine.Context context, ProcessorNode node) {
        this.node = node;
        GroovyRouterConfig config = (GroovyRouterConfig) node.getConfig().getConfig();
        ContextImpl<Void> ctx = new ContextImpl<>(null);
        List<Route> routes = config.getRoutes().stream()
                .map(routeConfig -> new Route(routeConfig.getWhen(), routeConfig.getTo()))
                .toList();
        this.processorEventloop = new ProcessorEventloop<>(
                node.getName(),
                createProcessorFactory(config.getDefaultRoute(), routes),
                ctx,
                node.getConfig().getSignalType(),
                node.getConfig().getQueueSizeConfig().getQueueSize(),
                node.getConfig().getThreadPoolSizeConfig().getThreadPoolSize());
    }

    BaseGroovyRouterProcessor(String name, String defaultRoute, List<Route> routes, SignalType signalType,
                                     int queueCapacity, int threadPoolSize) {
        ContextImpl<Void> ctx = new ContextImpl<>(null);
        this.processorEventloop = new ProcessorEventloop<>(name, createProcessorFactory(defaultRoute, routes),
                ctx, signalType, queueCapacity, threadPoolSize);
    }

    private static <T> ProcessorFactory<T> createProcessorFactory(String defaultDestination, List<Route> routes) {

        return ctx -> new GroovyRouterBaseEventloopProcessor<>(ctx, ResultFactoryImpl.INSTANCE, defaultDestination, routes);
    }

    @Override
    public ProcessorNode getNode() {
        return node;
    }

    @Override
    public void start() {
        processorEventloop.start();
    }

    public Context<Void> getContext() {
        return processorEventloop.getContext();
    }

    @Override
    public void stop() {
        processorEventloop.stop();
    }

    @Override
    public State getState() {
        return processorEventloop.getState();
    }

    @Override
    public String getName() {
        return processorEventloop.getName();
    }

    @Override
    public boolean send(Signal signal) {
        return processorEventloop.send(signal);
    }

    @Override
    public Set<SignalType> getSignalCapabilities() {
        return processorEventloop.getSignalCapabilities();
    }

    @Override
    public void connect(SignalDestination destination) {
        processorEventloop.connect(destination);
    }

    private static class GroovyRouterBaseEventloopProcessor<T> extends BaseGroovyBaseEventloopProcessor<T> {

        private final String defaultDestination;

        private final List<CompiledRoute> compiledRoutes;

        public GroovyRouterBaseEventloopProcessor(Context<T> context, ResultFactory resultFactory,
                                                  String defaultDestination, List<Route> routes) {
            super(context, resultFactory);
            this.defaultDestination = defaultDestination;
            this.compiledRoutes = routes.stream()
                    .map(r -> new CompiledRoute(getCompiler().compile(r.predicate()), r.destination()))
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
