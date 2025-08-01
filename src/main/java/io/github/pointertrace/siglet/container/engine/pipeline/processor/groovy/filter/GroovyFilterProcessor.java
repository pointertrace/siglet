package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.SignalDestination;
import io.github.pointertrace.siglet.container.engine.State;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.BaseGroovyBaseEventloopProcessor;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorContextImpl;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorEventloop;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorFactory;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import groovy.lang.Script;

import java.util.Set;

public class GroovyFilterProcessor implements Processor {

    private ProcessorNode node;

    private final ProcessorEventloop<Void> processorEventLoop;

    public GroovyFilterProcessor(Context context, ProcessorNode node) {
        this.node = node;
        GroovyFilterConfig conf = (GroovyFilterConfig) node.getConfig().getConfig();
        ProcessorContextImpl<Void> ctx = new ProcessorContextImpl<>(null);
        processorEventLoop = new ProcessorEventloop<>(
                node.getName(),
                createProcessorFactory(conf.getExpression()),
                ctx,
                node.getSignal(),
                node.getConfig().getQueueSizeConfig().getQueueSize(),
                node.getConfig().getThreadPoolSizeConfig().getThreadPoolSize());
    }

    public GroovyFilterProcessor(String name, String expression, SignalType signalType,
                                 int queueCapacity, int threadPoolSize) {
        ProcessorContextImpl<Void> ctx = new ProcessorContextImpl<>(null);
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

    public ProcessorContext<Void> getContext() {
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

        public GroovyFilterBaseEventloopProcessor(ProcessorContext<T> context,
                                                  ResultFactory resultFactory, String predicate) {
            super(context, resultFactory);
            this.predicateScript = getCompiler().compile(predicate);
        }

        @Override
        protected Result process(Signal signal, ProcessorContext<T> context, ResultFactory resultFactory) {
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
