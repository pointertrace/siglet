package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siglet.api.ProcessorContext;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.SignalDestination;
import io.github.pointertrace.siglet.container.engine.State;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.BaseGroovyBaseEventloopProcessor;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.BindingUtils;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorContextImpl;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorEventloop;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorFactory;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultImpl;
import groovy.lang.Script;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseGroovyActionProcessor implements Processor {

    private ProcessorNode node;

    private final ProcessorEventloop<Void> processorEventloop;

    BaseGroovyActionProcessor(Context context, ProcessorNode node) {
        this.node = node;
        GroovyActionConfig groovyActionConfig = (GroovyActionConfig) node.getConfig().getConfig();
        ProcessorContextImpl<Void> ctx = new ProcessorContextImpl<>(null);
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

    public static class GroovyActionBaseEventloopProcessor<T> extends BaseGroovyBaseEventloopProcessor<T> {

        private final Script actionScript;

        public GroovyActionBaseEventloopProcessor(ProcessorContext<T> context, ResultFactory resultFactory, String action) {
            super(context, resultFactory);
            this.actionScript = getCompiler().compile(action);
        }

        @Override
        public Result process(Signal signal, ProcessorContext<T> context, ResultFactory resultFactory) {
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