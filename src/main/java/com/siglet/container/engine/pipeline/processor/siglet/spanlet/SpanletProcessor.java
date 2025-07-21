package com.siglet.container.engine.pipeline.processor.siglet.spanlet;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Signal;
import com.siglet.api.signal.trace.Spanlet;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.SignalType;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.pipeline.processor.Processor;
import com.siglet.container.eventloop.processor.ProcessorContextImpl;
import com.siglet.container.eventloop.processor.ProcessorEventloop;
import com.siglet.container.eventloop.processor.ProcessorFactory;

import java.util.Map;
import java.util.Set;

public class SpanletProcessor implements Processor {

    private ProcessorNode node;


    private final ProcessorEventloop<Object> processorEventloop;

    public SpanletProcessor(Context context, ProcessorNode node, Spanlet<Object> spanlet) {
        this.node = node;
        Object config = node.getConfig().getConfig();
        ProcessorContextImpl<Object> ctx = new ProcessorContextImpl<>(config);

        processorEventloop = new ProcessorEventloop<>(
                node.getName(),
                createProcessorFactory(spanlet),
                ctx,
                node.getSignal(),
                node.getConfig().getQueueSizeConfig().getQueueSize(),
                node.getConfig().getThreadPoolSizeConfig().getThreadPoolSize(),
                node.getDestinationMappings());
    }

    // TODO remover para depender apenas do node out de um adapter node->config
    public SpanletProcessor(String name, Spanlet spanlet, Object config, int queueCapacity, int threadPoolSize,
                            Map<String, String> destinationMappings) {

        ProcessorContextImpl<Object> ctx = new ProcessorContextImpl<>(config);
        processorEventloop = new ProcessorEventloop<>(name, createProcessorFactory(spanlet), ctx,
                SignalType.TRACE, queueCapacity, threadPoolSize, destinationMappings);
    }

    private static <T> ProcessorFactory<T> createProcessorFactory(Spanlet<T> spanlet) {
        return ctx -> new ModifiedSpanBaseEventloopProcessor<>(ctx, spanlet);
    }

    public ProcessorContext<Object> getContext() {
        return processorEventloop.getContext();
    }

    @Override
    public ProcessorNode getNode() {
        return node;
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
}
