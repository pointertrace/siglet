package com.siglet.container.engine;

import com.siglet.container.adapter.pool.MetricObjectPool;
import com.siglet.container.adapter.pool.SpanObjectPool;
import com.siglet.container.config.Config;
import com.siglet.container.config.graph.ExporterNode;
import com.siglet.container.config.graph.Graph;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.GrpcExporterConfig;
import com.siglet.container.config.raw.ProcessorKind;
import com.siglet.container.engine.pipeline.processor.Processor;

public class Context {

    private final Config config;

    private SpanObjectPool spanObjectPool;

    private MetricObjectPool metricObjectPool;

    public Context(Config config, SpanObjectPool spanObjectPool, MetricObjectPool metricObjectPool) {
        this.config = config;
        this.spanObjectPool = spanObjectPool;
        this.metricObjectPool = metricObjectPool;
    }

    public Graph getGraph() {
        return config.getGraph(this);
    }

    public Processor createProcessor(ProcessorNode processorNode) {
        return config.getProcessorTypes().create(this, processorNode);
    }


//    private int getSpanObjectPoolSize() {
//        return getGraph().getNodeRegistry().stream()
//                .filter(ProcessorNode.class::isInstance)
//                .map(ProcessorNode.class::cast)
//                .filter(processorNode -> processorNode.getConfig().getProcessorKind() == ProcessorKind.SPANLET)
//                .mapToInt(processorNode -> processorNode.getQueueSize() + processorNode.getThreadPoolSize())
//                .sum() +
//                getGraph().getNodeRegistry().stream()
//                        .filter(ExporterNode.class::isInstance)
//                        .map(ExporterNode.class::cast)
//                        .filter(exporterNode -> exporterNode.getConfig() instanceof GrpcExporterConfig)
//                        .mapToInt(exporterNode > exporterNode.getQueueSize() + exporterNode.getThreadPoolSize())
//                        .sum();
//    }
//
//    private int getMetricObjectPoolSize() {
//        return getGraph().getNodeRegistry().stream()
//                .filter(ProcessorNode.class::isInstance)
//                .map(ProcessorNode.class::cast)
//                .filter(processorNode -> processorNode.getConfig().getProcessorKind() == ProcessorKind.METRICLET)
//                .mapToInt(processorNode -> processorNode.getQueueSize() + processorNode.getThreadPoolSize())
//                .sum() +
//                getGraph().getNodeRegistry().stream()
//                        .filter(ExporterNode.class::isInstance)
//                        .map(ExporterNode.class::cast)
//                        .filter(exporterNode -> exporterNode.getConfig() instanceof GrpcExporterConfig)
//                        .mapToInt(exporterNode -> exporterNode.getQueueSize() + exporterNode.getThreadPoolSize())
//                        .sum();
//    }
//

    public SpanObjectPool getSpanObjectPool() {
        if (spanObjectPool == null) {
//            spanObjectPool = new SpanObjectPool(getSpanObjectPoolSize());
        }
        return spanObjectPool;
    }

    public MetricObjectPool getMetricObjectPool() {
        if (metricObjectPool == null) {
//            metricObjectPool = new MetricObjectPool(getMetricObjectPoolSize());
        }
        return metricObjectPool;
    }
}
