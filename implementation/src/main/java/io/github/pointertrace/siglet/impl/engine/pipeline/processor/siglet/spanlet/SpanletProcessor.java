package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.*;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.eventloop.processor.ContextImpl;
import io.github.pointertrace.siglet.impl.eventloop.processor.Eventloop;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorFactory;

import java.util.Map;

public class SpanletProcessor implements Processor {

    private ProcessorNode node;


    private final Eventloop<Object> eventloop;

    public SpanletProcessor(SigletContext sigletContext, ProcessorNode node, Spanlet<?> spanlet) {
        this(node.getName(), spanlet, node.getDescription().getConfig(),
                sigletContext.getConfig().getQueueSize(node.getDescription()) ,
                sigletContext.getConfig().getThreadPoolSize(node.getDescription()),sigletContext.getSigletMetrics(),
                node.getDestinationMappings());
        this.node = node;
    }

    public SpanletProcessor(String name, Spanlet<?> spanlet, Object config, int queueCapacity, int threadPoolSize,
                            SigletMetrics sigletMetrics, Map<String, String> destinationMappings) {

        ContextImpl<Object> ctx = new ContextImpl<>(config);
        eventloop = new Eventloop<Object>(name, createProcessorFactory(spanlet), ctx,
                SignalCapabilities.of(Span.class), SignalCapabilities.of(Span.class), queueCapacity, threadPoolSize,
                destinationMappings, sigletMetrics);
    }

    @SuppressWarnings("unchecked")
    private static ProcessorFactory<Object> createProcessorFactory(Spanlet<?> spanlet) {
        Spanlet<Object> typedSpanlet = (Spanlet<Object>) spanlet;
        return ctx -> new BaseSpanletProcessor<>(ctx, typedSpanlet);
    }

    public Context<Object> getContext() {
        return eventloop.getContext();
    }

    @Override
    public ProcessorNode getNode() {
        return node;
    }

    @Override
    public void start() {
        eventloop.start();
    }

    @Override
    public void stop() {
        eventloop.stop();
    }

    @Override
    public State getState() {
        return eventloop.getState();
    }

    @Override
    public String getName() {
        return eventloop.getName();
    }

    @Override
    public boolean send(Signal signal) {
        return eventloop.send(signal);
    }

    @Override
    public SignalCapabilities getIncomingCapabilities() {
        return SignalCapabilities.of(Span.class);
    }

    @Override
    public void connect(SignalDestination destination) {
        eventloop.connect(destination);
    }

    @Override
    public SignalCapabilities getOutgoingCapabilities() {
        return SignalCapabilities.of(Span.class);
    }
}
