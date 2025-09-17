package io.github.pointertrace.siglet.container.engine;

import io.github.pointertrace.siglet.container.adapter.pool.MetricObjectPool;
import io.github.pointertrace.siglet.container.adapter.pool.SpanObjectPool;
import io.github.pointertrace.siglet.container.config.Config;
import io.github.pointertrace.siglet.container.config.graph.*;
import io.github.pointertrace.siglet.container.config.raw.GrpcExporterConfig;
import io.github.pointertrace.siglet.container.config.raw.ProcessorKind;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.container.engine.receiver.Receiver;

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

    public Receiver createReceiver(ReceiverNode receiverNode) {
        return config.getReceiverTypeRegistry().create(this, receiverNode);
    }


    // todo verificar quais são spanlets
    private int getSpanObjectPoolSize() {
        return getGraph().getNodeRegistry().stream()
                .filter(ProcessorNode.class::isInstance)
                .map(ProcessorNode.class::cast)
//                .filter(processorNode -> processorNode.getConfig().getProcessorKind() == ProcessorKind.SPANLET)
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

    // todo verificar quais são metriclets
    private int getMetricObjectPoolSize() {
        return getGraph().getNodeRegistry().stream()
                .filter(ProcessorNode.class::isInstance)
                .map(ProcessorNode.class::cast)
//                .filter(processorNode -> processorNode.getConfig().getProcessorKind() == ProcessorKind.METRICLET)
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
