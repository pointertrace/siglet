package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

import groovy.lang.Script;
import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.BaseGroovyBaseEventloopProcessor;
import io.github.pointertrace.siglet.impl.eventloop.processor.ContextImpl;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorEventloop;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorFactory;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultFactoryImpl;

import java.util.Set;

public abstract class BaseGroovyFilterProcessor implements Processor {

    private ProcessorNode node;

    private final ProcessorEventloop<Void> processorEventLoop;

    BaseGroovyFilterProcessor(io.github.pointertrace.siglet.impl.engine.Context context, ProcessorNode node) {
        this.node = node;
        GroovyFilterConfig conf = (GroovyFilterConfig) node.getConfig().getConfig();
        ContextImpl<Void> ctx = new ContextImpl<>(null);
        processorEventLoop = new ProcessorEventloop<>(
                node.getName(),
                createProcessorFactory(conf.getExpression()),
                ctx,
                node.getConfig().getSignalType(),
                node.getConfig().getQueueSizeConfig().getQueueSize(),
                node.getConfig().getThreadPoolSizeConfig().getThreadPoolSize());
    }

    BaseGroovyFilterProcessor(String name, String expression, SignalType signalType,
                                     int queueCapacity, int threadPoolSize) {
        ContextImpl<Void> ctx = new ContextImpl<>(null);
        processorEventLoop = new ProcessorEventloop<>(name, createProcessorFactory(expression), ctx, signalType,
                queueCapacity, threadPoolSize);
    }


    private static <T> ProcessorFactory<T> createProcessorFactory(String predicate) {
        return ctx -> new GroovyFilterBaseEventloopProcessor<>(ctx, ResultFactoryImpl.INSTANCE, predicate);
    }

    @Override
    public ProcessorNode getNode() {
        return node;
    }

    public ProcessorConfig getConfig() {
        return node.getConfig();

    }

    public Context<Void> getContext() {
        return processorEventLoop.getContext();
    }

    @Override
    public void start() {
        processorEventLoop.start();
    }

    @Override
    public void stop() {
        processorEventLoop.stop();
    }

    @Override
    public State getState() {
        return processorEventLoop.getState();
    }

    @Override
    public String getName() {
        return processorEventLoop.getName();
    }

    @Override
    public boolean send(Signal signal) {
        return processorEventLoop.send(signal);
    }

    @Override
    public Set<SignalType> getSignalCapabilities() {
        return processorEventLoop.getSignalCapabilities();
    }

    @Override
    public void connect(SignalDestination destination) {
        processorEventLoop.connect(destination);
    }

    private static class GroovyFilterBaseEventloopProcessor<T> extends BaseGroovyBaseEventloopProcessor<T> {

        private final Script predicateScript;

        public GroovyFilterBaseEventloopProcessor(Context<T> context,
                                                  ResultFactory resultFactory, String predicate) {
            super(context, resultFactory);
            this.predicateScript = getCompiler().compile(predicate);
        }

        @Override
        protected Result process(Signal signal, Context<T> context, ResultFactory resultFactory) {
            getCompiler().prepareScript(predicateScript, signal, context);
            Object predicate = predicateScript.run();
            if (predicate != null && predicate instanceof Boolean boolPredicate && Boolean.TRUE.equals(boolPredicate)) {
                return resultFactory.proceed();
            } else {
                return resultFactory.drop();
            }
        }
    }


}
