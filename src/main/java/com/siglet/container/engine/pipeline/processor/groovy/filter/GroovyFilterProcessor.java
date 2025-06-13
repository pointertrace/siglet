package com.siglet.container.engine.pipeline.processor.groovy.filter;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.ResultFactory;
import com.siglet.api.Signal;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.pipeline.processor.Processor;
import com.siglet.container.engine.pipeline.processor.groovy.BaseGroovyBaseEventloopProcessor;
import com.siglet.container.eventloop.processor.ProcessorContextImpl;
import com.siglet.container.eventloop.processor.ProcessorEventloop;
import com.siglet.container.eventloop.processor.ProcessorFactory;
import com.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import groovy.lang.Script;

public class GroovyFilterProcessor  implements Processor {

    private ProcessorNode node;

    private final ProcessorEventloop<Signal, Void> processorEventLoop;

    public GroovyFilterProcessor(ProcessorNode node) {
        this.node = node;
        GroovyFilterConfig conf = (GroovyFilterConfig) node.getConfig().getConfig();
        ProcessorContextImpl<Void> ctx = new ProcessorContextImpl<>(null);
        processorEventLoop = new ProcessorEventloop<>(node.getName(), createProcessorFactory(conf.getExpression()), ctx,
                Signal.class, 1, 1_000);
    }

    public GroovyFilterProcessor(String name, String expression, int threadPoolSize, int queueCapacity) {
        ProcessorContextImpl<Void> ctx = new ProcessorContextImpl<>(null);
        processorEventLoop = new ProcessorEventloop<>(name, createProcessorFactory(expression), ctx, Signal.class,
                threadPoolSize, queueCapacity);
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
    public Class<Signal> getType() {
        return processorEventLoop.getType();
    }

    @Override
    public void connect(SignalDestination<Signal> destination) {
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
            System.out.println("antes de verificar signal:" + signal.getId() + ", predicate:" + predicate);
            if (predicate != null && predicate instanceof Boolean boolPredicate && Boolean.TRUE.equals(boolPredicate)) {
                System.out.println("depois proceed");
                return resultFactory.proceed();
            } else {
                System.out.println("depois drop");
                return resultFactory.drop();
            }
        }
    }
}
