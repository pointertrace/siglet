package com.siglet.container.engine;

import com.siglet.container.adapter.pool.MetricObjectPool;
import com.siglet.container.adapter.pool.SpanObjectPool;
import com.siglet.container.config.Config;
import com.siglet.container.config.graph.ExporterNode;
import com.siglet.container.config.graph.Graph;
import com.siglet.container.config.graph.GraphFactory;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.GrpcExporterConfig;
import com.siglet.container.config.raw.ProcessorKind;
import com.siglet.container.engine.pipeline.processor.Processor;

public class Context {

    private final Config config;

    private Graph graph;

    private GraphFactory graphFactory = new GraphFactory();

    private SpanObjectPool spanObjectPool;

    private MetricObjectPool metricObjectPool;

    public Context(Config config, SpanObjectPool spanObjectPool, MetricObjectPool metricObjectPool) {
        this.config = config;
        this.spanObjectPool = spanObjectPool;
        this.metricObjectPool = metricObjectPool;
    }

    public Graph getGraph() {
        if (graph == null) {
            graph = graphFactory.create(config);
        }
        return graph;
    }

    public Processor createProcessor(ProcessorNode processorNode) {
        return config.getProcessorTypes().create(this, processorNode);
    }


    private int getSpanObjectPoolSize() {
        return getGraph().getNodeRegistry().stream()
                .filter(ProcessorNode.class::isInstance)
                .map(ProcessorNode.class::cast)
                .filter(processorNode -> processorNode.getConfig().getProcessorKind() == ProcessorKind.SPANLET)
                .map(ProcessorNode::getConfig)
                .mapToInt(processorConfig -> processorConfig.getThreadPoolSizeConfig().getThreadPoolSize() +
                        processorConfig.getQueueSizeConfig().getQueueSize())
                .sum() +
                getGraph().getNodeRegistry().stream()
                        .filter(ExporterNode.class::isInstance)
                        .map(ExporterNode.class::cast)
                        .filter(exporterNode -> exporterNode.getConfig() instanceof GrpcExporterConfig)
                        .map(ExporterNode::getConfig)
                        .map(GrpcExporterConfig.class::cast)
                        .mapToInt(grpcExporterConfig -> grpcExporterConfig.getQueueSizeConfig().getQueueSize())
                        .sum();
    }

    private int getMetricObjectPoolSize() {
        return getGraph().getNodeRegistry().stream()
                .filter(ProcessorNode.class::isInstance)
                .map(ProcessorNode.class::cast)
                .filter(processorNode -> processorNode.getConfig().getProcessorKind() == ProcessorKind.METRICLET)
                .map(ProcessorNode::getConfig)
                .mapToInt(processorConfig -> processorConfig.getThreadPoolSizeConfig().getThreadPoolSize() +
                        processorConfig.getQueueSizeConfig().getQueueSize())
                .sum() +
                getGraph().getNodeRegistry().stream()
                        .filter(ExporterNode.class::isInstance)
                        .map(ExporterNode.class::cast)
                        .filter(exporterNode -> exporterNode.getConfig() instanceof GrpcExporterConfig)
                        .map(ExporterNode::getConfig)
                        .map(GrpcExporterConfig.class::cast)
                        .mapToInt(grpcExporterConfig -> grpcExporterConfig.getQueueSizeConfig().getQueueSize())
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
