package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.eventloop.processor.ContextImpl;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorEventloop;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorFactory;

import java.util.Map;
import java.util.Set;

public class SpanletProcessor implements Processor {

    private ProcessorNode node;


    private final ProcessorEventloop<Object> processorEventloop;

    public SpanletProcessor(io.github.pointertrace.siglet.impl.engine.Context context, ProcessorNode node, Spanlet<Object> spanlet) {
        this.node = node;
        Object config = node.getConfig().getConfig();
        ContextImpl<Object> ctx = new ContextImpl<>(config);

        processorEventloop = new ProcessorEventloop<>(
                node.getName(),
                createProcessorFactory(spanlet),
                ctx,
                node.getConfig().getSignalType(),
                node.getConfig().getQueueSizeConfig().getQueueSize(),
                node.getConfig().getThreadPoolSizeConfig().getThreadPoolSize(),
                node.getDestinationMappings());
    }

    // TODO remover para depender apenas do node out de um adapter node->config
    public SpanletProcessor(String name, Spanlet spanlet, Object config, int queueCapacity, int threadPoolSize,
                            Map<String, String> destinationMappings) {

        ContextImpl<Object> ctx = new ContextImpl<>(config);
        processorEventloop = new ProcessorEventloop<>(name, createProcessorFactory(spanlet), ctx,
                SignalType.SPAN, queueCapacity, threadPoolSize, destinationMappings);
    }

    private static <T> ProcessorFactory<T> createProcessorFactory(Spanlet<T> spanlet) {
        return ctx -> new ModifiedSpanBaseEventloopProcessor<>(ctx, spanlet);
    }

    public Context<Object> getContext() {
        return processorEventloop.getContext();
    }

    @Override
    public ProcessorNode getNode() {
        return node;
    }

    @Override
    public SignalType getSignalType() {
        return SignalType.SPAN;
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
