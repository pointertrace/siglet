package com.siglet.container.engine.pipeline.processor.siglet.spanlet.unmodifiable;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Signal;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpanlet;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.EventLoopConfig;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.pipeline.processor.Processor;
import com.siglet.container.eventloop.processor.ProcessorContextImpl;
import com.siglet.container.eventloop.processor.ProcessorEventloop;
import com.siglet.container.eventloop.processor.ProcessorFactory;

import java.util.Map;

public class UnmodifiableSpanletProcessor implements Processor {

    private ProcessorNode node;

    private Context context;

    private final ProcessorEventloop<Signal, Object> processorEventLoop;

    public UnmodifiableSpanletProcessor(Context context, ProcessorNode node, UnmodifiableSpanlet<Object> spanlet) {
        this.node = node;
        this.context = context;
        Object config = node.getConfig().getConfig();
        ProcessorContextImpl<Object> processorContext = new ProcessorContextImpl<>(config);

        processorEventLoop = new ProcessorEventloop<>(node.getName(), createProcessorFactory(context, spanlet),
                processorContext, Signal.class, node.getQueueSize(), node.getThreadPoolSize(),
                node.getDestinationMappings());
    }

    // TODO remover para depender apenas do node out de um adapter node->config
    public UnmodifiableSpanletProcessor(String name, UnmodifiableSpanlet spanlet, Object config,
                                        int queueCapacity, int threadPoolSize, Map<String, String> destinationMappings) {

        ProcessorContextImpl<Object> ctx = new ProcessorContextImpl<>(config);
        processorEventLoop = new ProcessorEventloop<>(name, createProcessorFactory(context, spanlet), ctx,
                Signal.class, queueCapacity, threadPoolSize, destinationMappings);
    }


    private static <T> ProcessorFactory<T> createProcessorFactory(Context context, UnmodifiableSpanlet<T> spanlet) {
        return ctx -> new UnmodifiedSpanBaseEventloopProcessor<>(context, ctx, spanlet);
    }

    @Override
    public ProcessorNode getNode() {
        return node;
    }

    public ProcessorContext<Object> getContext() {
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
}
