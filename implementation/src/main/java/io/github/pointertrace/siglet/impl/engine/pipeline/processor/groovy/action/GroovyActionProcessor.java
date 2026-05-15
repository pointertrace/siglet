package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action;

import groovy.lang.Script;
import io.github.pointertrace.siglet.api.*;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.BaseGroovyProcessor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.BindingUtils;
import io.github.pointertrace.siglet.impl.eventloop.processor.ContextImpl;
import io.github.pointertrace.siglet.impl.eventloop.processor.Eventloop;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorFactory;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultFactoryImpl;

import java.util.List;
import java.util.Map;

public class GroovyActionProcessor implements Processor {

    private ProcessorNode node;

    private final Eventloop<Void> eventLoop;

    GroovyActionProcessor(SigletContext sigletContext, ProcessorNode node) {
        this(node.getName(), getConfig(node).getAction(),
                SignalCapabilities.of(node.getDescription().getType().getValue()),
                sigletContext.getConfig().getQueueSize(node.getDescription()),
                sigletContext.getConfig().getThreadPoolSize(node.getDescription()));
        this.node = node;
    }

    GroovyActionProcessor(String name, String action, SignalCapabilities signalCapabilities,
                          int queueCapacity, int threadPoolSize) {
        ContextImpl<Void> ctx = new ContextImpl<>(null);
        eventLoop = new Eventloop<>(name, createProcessorFactory(action), ctx,
                signalCapabilities, signalCapabilities, queueCapacity, threadPoolSize);
    }

    private static GroovyActionConfig getConfig(ProcessorNode node) {
        return ((GroovyActionConfig) node.getDescription().getConfig());
    }

    private static <T> ProcessorFactory<T> createProcessorFactory(String action) {
        return ctx -> new GroovyActionBaseGroovyProcessor<T>(ctx, ResultFactoryImpl.INSTANCE, action);
    }

    @Override
    public ProcessorNode getNode() {
        return node;
    }

    public Context<Void> getContext() {
        return eventLoop.getContext();
    }

    @Override
    public void start() {
        eventLoop.start();
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

    public static class GroovyActionBaseGroovyProcessor<T> extends BaseGroovyProcessor<T> {

        private final Script actionScript;

        public GroovyActionBaseGroovyProcessor(Context<T> context, ResultFactory resultFactory, String action) {
            super(context, resultFactory);
            this.actionScript = getCompiler().compile(action);
        }

        @Override
        public Result process(Signal signal, Context<T> context, ResultFactory resultFactory) {
            getCompiler().prepareScript(actionScript, signal, context);
            actionScript.run();
            Result result = BindingUtils.getResult(actionScript.getBinding());
            Map<String, List<Signal>> routes = BindingUtils.getRoutes(actionScript.getBinding());
            for (Map.Entry<String, List<Signal>> entry : routes.entrySet()) {
                for (Signal s : entry.getValue()) {
                    result = result.andSend(s, entry.getKey());
                }
            }
            return result;
        }
    }
}