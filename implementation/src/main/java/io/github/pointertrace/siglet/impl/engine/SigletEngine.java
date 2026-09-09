package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.graph.*;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestinationProvider;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSourceProvider;
import io.github.pointertrace.siglet.impl.engine.component.connection.drop.DropSignalDestinationProvider;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporters;
import io.github.pointertrace.siglet.impl.engine.metric.MetricInterceptor;
import io.github.pointertrace.siglet.impl.engine.metric.Metrics;
import io.github.pointertrace.siglet.impl.engine.metric.noop.NoopMetrics;
import io.github.pointertrace.siglet.impl.engine.metric.otelgrpc.OtelGrpcMetrics;
import io.github.pointertrace.siglet.impl.engine.pipeline.Pipeline;
import io.github.pointertrace.siglet.impl.engine.pipeline.Pipelines;
import io.github.pointertrace.siglet.impl.engine.receiver.Receivers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SigletEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigletEngine.class);

    private State state = State.CREATED;

    private Metrics metrics;

    private final Receivers receivers = new Receivers();

    private final Exporters exporters = new Exporters();

    private final Pipelines pipelines = new Pipelines();

    public SigletEngine(SigletContext sigletContext) {

        Graph graph = sigletContext.getGraph();

        String metricsGrpcExporter = sigletContext.getConfig().getYamlDescriptor().getGlobalConfig().getInternalMetricsGrpcExporter() != null ?
                sigletContext.getConfig().getYamlDescriptor().getGlobalConfig().getInternalMetricsGrpcExporter().getValue() : null;

        long exportInterval = sigletContext.getConfig().getYamlDescriptor().getGlobalConfig().getInternalMetricsExportIntervalMillis() != null ?
                sigletContext.getConfig().getYamlDescriptor().getGlobalConfig().getInternalMetricsExportIntervalMillis().getValue().longValue() : 0;

        // TODO move to a factory
        graph.getNodeRegistry().stream()
                .filter(ExporterNode.class::isInstance)
                .map(ExporterNode.class::cast)
                .forEach(exporterNode -> exporters.create(sigletContext, exporterNode));

        if (metricsGrpcExporter != null && exportInterval > 0) {
            metrics = new OtelGrpcMetrics(exportInterval, exporters.getExporter(metricsGrpcExporter));
            sigletContext.addInterceptor(new MetricInterceptor(metrics));
        } else {
            metrics = new NoopMetrics();
        }
        ((SigletContextImpl) sigletContext).setMetrics(metrics);

        graph.getNodeRegistry().stream()
                .filter(PipelineNode.class::isInstance)
                .map(PipelineNode.class::cast)
                .forEach(pipelineNode -> pipelines.create(sigletContext, pipelineNode));

        graph.getNodeRegistry().stream()
                .filter(ProcessorNode.class::isInstance)
                .map(ProcessorNode.class::cast)
                .forEach(sigletNode -> {
                    String pipelineName = sigletNode.getPipeline().getName();
                    Pipeline pipeline = pipelines.get(pipelineName);
                    if (pipeline == null) {
                        throw new SigletError(String.format("Could not find pipeline named %s", pipelineName));
                    }
                    pipeline.getProcessors().create(sigletContext, sigletNode);
                });

        graph.getNodeRegistry().stream()
                .filter(ReceiverNode.class::isInstance)
                .map(ReceiverNode.class::cast)
                .forEach(receiverNode -> receivers.create(sigletContext, receiverNode));
        connect(sigletContext);

    }


    private SignalDestinationProvider getSignalDestinationProvider(String name) {
        SignalDestinationProvider result = pipelines.getDestination(name);
        if (result == null) {
            result = exporters.getExporter(name);
        }
        if (result == null) {
            throw new SigletError(String.format("Could not find signal destination %s", name));
        }
        return result;
    }

    private void connect(SigletContext sigletContext) {
        receivers.forEach(receiver ->
                receiver.getNode().getTo().stream()
                        .flatMap(pipelineNode -> pipelineNode.getStart().stream())
                        .forEach(sigletNode -> connect(receiver, getSignalDestinationProvider(sigletNode.getName())))
        );

        pipelines.forEach(pipeline ->
                pipeline.getProcessors().forEach(processor ->
                        processor.getNode().getTo()
                                .forEach(node -> connect(processor, getSignalDestinationProvider(node.getName()))))
        );

        pipelines.forEach(pipeline ->
                pipeline.getDeadEndProcessors().forEach(processor ->
                        connect(processor, new DropSignalDestinationProvider(sigletContext)))
        );

    }

    private void connect(SignalSourceProvider signalSourceProvider, SignalDestinationProvider signalDestinationProvider) {
        signalSourceProvider.getSignalSource().connect(signalDestinationProvider.getSignalDestination());
    }


    public void start() {
        state = State.STARTING;
        exporters.start();
        pipelines.start();
        receivers.start();
        metrics.start();
        state = State.RUNNING;
    }

    public void stop() {
        state = State.STOPPING;
        receivers.stop();
        pipelines.stop();
        exporters.stop();
        state = State.STOPPED;
    }

    public State getState() {
        return state;
    }

}
