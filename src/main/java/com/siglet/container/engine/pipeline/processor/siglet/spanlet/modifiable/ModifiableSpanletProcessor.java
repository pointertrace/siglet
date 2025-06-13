package com.siglet.container.engine.pipeline.processor.siglet.spanlet.modifiable;

import com.siglet.api.ProcessorContext;
import com.siglet.api.Signal;
import com.siglet.api.modifiable.trace.ModifiableSpanlet;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.pipeline.processor.Processor;
import com.siglet.container.eventloop.processor.ProcessorContextImpl;
import com.siglet.container.eventloop.processor.ProcessorEventloop;
import com.siglet.container.eventloop.processor.ProcessorFactory;

public class ModifiableSpanletProcessor implements Processor {

    private ProcessorNode node;


    private final ProcessorEventloop<Signal, Object> processorEventloop;

    public ModifiableSpanletProcessor(ProcessorNode node, ModifiableSpanlet<Object> spanlet) {
        this.node = node;
        Object config = node.getConfig().getConfig();
        ProcessorContextImpl<Object> ctx = new ProcessorContextImpl<>(config);
        processorEventloop = new ProcessorEventloop<>(node.getName(), createProcessorFactory(spanlet), ctx, Signal.class, 10, 1_000);
    }

    // TODO remover para depender apenas do node out de um adapter node->config
    public ModifiableSpanletProcessor(String name, ModifiableSpanlet spanlet, Object config,
                                      int threadPoolSize,
                                      int queueCapacity) {

        ProcessorContextImpl<Object> ctx = new ProcessorContextImpl<>(config);
        processorEventloop = new ProcessorEventloop<>(name, createProcessorFactory(spanlet), ctx,
                Signal.class, threadPoolSize, queueCapacity);
    }

    private static <T> ProcessorFactory<T> createProcessorFactory(com.siglet.api.modifiable.trace.ModifiableSpanlet<T> spanlet) {
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
    public Class<Signal> getType() {
        return processorEventloop.getType();
    }

    @Override
    public void connect(SignalDestination<Signal> destination) {
        processorEventloop.connect(destination);
    }
}
