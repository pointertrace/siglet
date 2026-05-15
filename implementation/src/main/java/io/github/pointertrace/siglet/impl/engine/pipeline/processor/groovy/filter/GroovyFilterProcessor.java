package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

import groovy.lang.Script;
import io.github.pointertrace.siglet.api.*;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
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
import io.github.pointertrace.siglet.parser.StringValue;

public class GroovyFilterProcessor implements Processor {

    private ProcessorNode node;

    private final Eventloop<Void> eventLoop;

    GroovyFilterProcessor(SigletContext sigletContext, ProcessorNode node) {
        this(node.getName(), getConfig(node).getExpression(),
                SignalCapabilities.of(node.getDescription().getType().getValue()),
                sigletContext.getConfig().getQueueSize(node.getDescription()),
                sigletContext.getConfig().getThreadPoolSize(node.getDescription()));
        this.node = node;
    }

    GroovyFilterProcessor(String name, String expression, SignalCapabilities signalCapabilities,
                          int queueCapacity, int threadPoolSize) {
        ContextImpl<Void> ctx = new ContextImpl<>(null);
        eventLoop = new Eventloop<>(name, createProcessorFactory(expression), ctx,
                signalCapabilities, signalCapabilities,
                queueCapacity, threadPoolSize);
    }

    private static GroovyFilterConfig getConfig(ProcessorNode node) {
        return ((GroovyFilterConfig) node.getDescription().getConfig());
    }


    private static <T> ProcessorFactory<T> createProcessorFactory(String predicate) {
        return ctx -> new GroovyFilterBaseGroovyProcessor<>(ctx, ResultFactoryImpl.INSTANCE, predicate);
    }

    private SignalCapabilities getFromType(StringValue signalType) {
        if (signalType.getValue().toLowerCase().contains("spanlet")) {
            return SignalCapabilities.of(Span.class);
        } else if (signalType.getValue().toLowerCase().contains("metriclet")) {
            return SignalCapabilities.of(Span.class);
        } else {
            throw new SigletError(String.format("Cannot infer signal type for siglet named %s", signalType.getValue()));
        }

    }

    @Override
    public ProcessorNode getNode() {
        return node;
    }

    public ProcessorDescriptor getConfig() {
        return node.getDescription();
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

    private static class GroovyFilterBaseGroovyProcessor<T> extends BaseGroovyProcessor<T> {

        private final Script predicateScript;

        public GroovyFilterBaseGroovyProcessor(Context<T> context,
                                               ResultFactory resultFactory, String predicate) {
            super(context, resultFactory);
            this.predicateScript = getCompiler().compile(predicate);
        }

        @Override
        protected Result process(Signal signal, Context<T> context, ResultFactory resultFactory) {
            getCompiler().prepareScript(predicateScript, signal, context);
            Object predicate = predicateScript.run();
            if (predicate instanceof Boolean boolPredicate && boolPredicate) {
                return resultFactory.proceed();
            } else {
                return resultFactory.drop();
            }
        }
    }


}
