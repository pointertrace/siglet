package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.graph.*;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporters;
import io.github.pointertrace.siglet.impl.engine.pipeline.Pipeline;
import io.github.pointertrace.siglet.impl.engine.pipeline.Pipelines;
import io.github.pointertrace.siglet.impl.engine.receiver.Receivers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SigletEngine implements EngineElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigletEngine.class);

    private State state = State.CREATED;

    private final Receivers receivers = new Receivers();

    private final Exporters exporters = new Exporters();

    private final Pipelines pipelines = new Pipelines();

    public SigletEngine(Context context) {

        Graph graph = context.getGraph();

        // TODO move to a factory
        graph.getNodeRegistry().stream()
                .filter(ExporterNode.class::isInstance)
                .map(ExporterNode.class::cast)
                .forEach(exporterNode -> exporters.create(context, exporterNode));

        graph.getNodeRegistry().stream()
                .filter(PipelineNode.class::isInstance)
                .map(PipelineNode.class::cast)
                .forEach(pipelines::create);

        graph.getNodeRegistry().stream()
                .filter(ProcessorNode.class::isInstance)
                .map(ProcessorNode.class::cast)
                .forEach(sigletNode -> {
                    String pipelineName = sigletNode.getPipeline().getName();
                    Pipeline pipeline = pipelines.get(pipelineName);
                    if (pipeline == null) {
                        throw new SigletError(String.format("Could not find pipeline named %s", pipelineName));
                    }
                    pipeline.getProcessors().create(context, sigletNode);
                });

        graph.getNodeRegistry().stream()
                .filter(ReceiverNode.class::isInstance)
                .map(ReceiverNode.class::cast)
                .forEach(receiverNode -> receivers.create(context, receiverNode));
        connect();

    }


    private SignalDestination getDestination(String name) {
        SignalDestination result = pipelines.getDestination(name);
        if (result == null) {
            result = exporters.getExporter(name);
        }
        if (result == null) {
            throw new SigletError(String.format("Could not find signal destination %s", name));
        }
        return result;
    }

    private void connect() {
        receivers.forEach(receiver ->
                receiver.getNode().getTo().stream()
                        .flatMap(pipelineNode -> pipelineNode.getStart().stream())
                        .forEach(sigletNode -> receiver.connect(getDestination(sigletNode.getName())))
        );

        pipelines.forEach(pipeline ->
                pipeline.getProcessors().forEach(processor ->
                        processor.getNode().getTo()
                                .forEach(node -> processor.connect(getDestination(node.getName()))))
        );

    }


    @Override
    public void start() {
        state = State.STARTING;
        exporters.start();
        pipelines.start();
        receivers.start();
        state = State.RUNNING;
    }

    @Override
    public void stop() {
        state = State.STOPPING;
        receivers.stop();
        pipelines.stop();
        exporters.stop();
        state = State.STOPPED;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public String getName() {
        return "engine";
    }
}
