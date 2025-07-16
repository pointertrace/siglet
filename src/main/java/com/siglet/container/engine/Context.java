package com.siglet.container.engine;

import com.siglet.container.adapter.pool.MetricObjectPool;
import com.siglet.container.adapter.pool.SpanObjectPool;
import com.siglet.container.config.Config;
import com.siglet.container.config.ConfigFactory;
import com.siglet.container.config.graph.Graph;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.EventLoopConfig;
import com.siglet.container.config.raw.ProcessorKind;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.engine.pipeline.processor.Processor;

import java.util.List;

public class Context {

    private final Config config;

    private final EventLoopConfig globalEventLoopConfig;

    private SpanObjectPool spanObjectPool;

    private MetricObjectPool metricObjectPool;

    public Context(Config config, SpanObjectPool spanObjectPool, MetricObjectPool metricObjectPool,
                   EventLoopConfig globalEventLoopConfig) {
        this.config = config;
        this.spanObjectPool = spanObjectPool;
        this.metricObjectPool = metricObjectPool;
        this.globalEventLoopConfig = globalEventLoopConfig;
    }

    public Graph getGraph() {
        return config.getGraph(this);
    }

    public EventLoopConfig getGlobalEventLoopConfig() {
        return globalEventLoopConfig;
    }

    public Processor createProcessor(ProcessorNode processorNode) {
        return config.getProcessorTypes().create(this, processorNode);
    }


    // todo incluir exporter
    private int getSpanObjectPoolSize() {
        return getGraph().getNodeRegistry().stream()
                .filter(ProcessorNode.class::isInstance)
                .map(ProcessorNode.class::cast)
                .filter(processorNode -> processorNode.getConfig().getKind() == ProcessorKind.SPANLET)
                .mapToInt(processorNode -> processorNode.getQueueSize() + processorNode.getThreadPoolSize())
                .sum();
    }

    // todo incluir exporter
    private int getMetricObjectPoolSize() {
        return getGraph().getNodeRegistry().stream()
                .filter(ProcessorNode.class::isInstance)
                .map(ProcessorNode.class::cast)
                .filter(processorNode -> processorNode.getConfig().getKind() == ProcessorKind.METRICLET)
                .mapToInt(processorNode -> processorNode.getQueueSize() + processorNode.getThreadPoolSize())
                .sum();
    }


    public SpanObjectPool getSpanObjectPool() {
        if (spanObjectPool == null) {
            spanObjectPool = new SpanObjectPool(getSpanObjectPoolSize());
        }
        return spanObjectPool;
    }

    public MetricObjectPool getMetricObjectPool() {
        if (metricObjectPool == null) {
            metricObjectPool = new MetricObjectPool(getMetricObjectPoolSize());
        }
        return metricObjectPool;
    }
}
