package com.siglet.container.engine.pipeline.processor.groovy.action;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.Signal;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.graph.SignalType;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.pipeline.processor.Processor;
import com.siglet.container.engine.pipeline.processor.groovy.BaseGroovyBaseEventloopProcessor;
import com.siglet.container.eventloop.processor.ProcessorContextImpl;
import com.siglet.container.eventloop.processor.ProcessorEventloop;
import com.siglet.container.eventloop.processor.ProcessorFactory;
import com.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import com.siglet.container.eventloop.processor.result.ResultImpl;
import groovy.lang.Script;

import java.util.Set;

public class GroovyActionProcessor implements Processor {

    private ProcessorNode node;

    private final ProcessorEventloop<Void> processorEventloop;

    public GroovyActionProcessor(Context context, ProcessorNode node) {
        this.node = node;
        GroovyActionConfig groovyActionConfig = (GroovyActionConfig) node.getConfig().getConfig();
        ProcessorContextImpl<Void> ctx = new ProcessorContextImpl<>(null);
        processorEventloop = new ProcessorEventloop<>(
                node.getName(),
                createProcessorFactory(groovyActionConfig.getAction()),
                ctx,
                node.getSignal(),
                node.getConfig().getQueueSizeConfig().getQueueSize(),
                node.getConfig().getThreadPoolSizeConfig().getThreadPoolSize());
    }

    public GroovyActionProcessor(String name, String action, SignalType signalType,
                                 int queueCapacity, int threadPoolSize) {
        ProcessorContextImpl<Void> ctx = new ProcessorContextImpl<>(null);
        processorEventloop = new ProcessorEventloop<>(name, createProcessorFactory(action), ctx,
                signalType, queueCapacity, threadPoolSize);
    }

    private static <T> ProcessorFactory<T> createProcessorFactory(String action) {
        return ctx -> new GroovyActionBaseEventloopProcessor<T>(ctx, ResultFactoryImpl.INSTANCE, action);
    }

    @Override
    public ProcessorNode getNode() {
        return node;
    }

    public ProcessorContext<Void> getContext() {
        return processorEventloop.getContext();
    }

    @Override
    public void start() {
        processorEventloop.start();
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

    private static class GroovyActionBaseEventloopProcessor<T> extends BaseGroovyBaseEventloopProcessor<T> {

        private final Script actionScript;

        public GroovyActionBaseEventloopProcessor(ProcessorContext<T> context, ResultFactory resultFactory, String action) {
            super(context, resultFactory);
            this.actionScript = getCompiler().compile(action);
        }

        @Override
        protected Result process(Signal signal, ProcessorContext<T> context, ResultFactory resultFactory) {
            actionScript.getBinding().setVariable("result", ResultImpl.proceed());
            actionScript.getBinding().setVariable("signal", signal);
            actionScript.getBinding().setVariable("context", context);
            actionScript.run();
            Object resultObj = actionScript.getBinding().getVariable("result");
            if (resultObj instanceof Result result) {
                return result;
            } else {
                throw new IllegalStateException("Result object wrong type or not found as a binding variable in " +
                        "groovy script");
            }
        }
    }
}