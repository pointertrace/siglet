package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action;

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
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.BindingUtils;
import io.github.pointertrace.siglet.impl.eventloop.processor.ContextImpl;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorEventloop;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorFactory;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultFactoryImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseGroovyActionProcessor implements Processor {

    private ProcessorNode node;

    private final ProcessorEventloop<Void> processorEventloop;

    BaseGroovyActionProcessor(io.github.pointertrace.siglet.impl.engine.Context context, ProcessorNode node) {
        this.node = node;
        GroovyActionConfig groovyActionConfig = (GroovyActionConfig) node.getConfig().getConfig();
        ContextImpl<Void> ctx = new ContextImpl<>(null);
        processorEventloop = new ProcessorEventloop<>(
                node.getName(),
                createProcessorFactory(groovyActionConfig.getAction()),
                ctx,
                node.getConfig().getSignalType(),
                node.getConfig().getQueueSizeConfig().getQueueSize(),
                node.getConfig().getThreadPoolSizeConfig().getThreadPoolSize());
    }

    BaseGroovyActionProcessor(String name, String action, SignalType signalType,
                              int queueCapacity, int threadPoolSize) {
        ContextImpl<Void> ctx = new ContextImpl<>(null);
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

    public Context<Void> getContext() {
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

    public static class GroovyActionBaseEventloopProcessor<T> extends BaseGroovyBaseEventloopProcessor<T> {

        private final Script actionScript;

        public GroovyActionBaseEventloopProcessor(Context<T> context, ResultFactory resultFactory, String action) {
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