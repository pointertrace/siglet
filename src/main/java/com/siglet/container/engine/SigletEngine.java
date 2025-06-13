package com.siglet.container.engine;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.config.graph.*;
import com.siglet.container.engine.exporter.Exporters;
import com.siglet.container.engine.pipeline.Pipeline;
import com.siglet.container.engine.pipeline.Pipelines;
import com.siglet.container.engine.receiver.Receivers;

public class SigletEngine implements EngineElement {

    private State state = State.CREATED;

    private final Receivers receivers = new Receivers();

    private final Exporters exporters = new Exporters();

    private final Pipelines pipelines = new Pipelines();

    public SigletEngine(Graph graph) {

        // TODO move to a factory
        graph.getNodes().stream()
                .filter(ExporterNode.class::isInstance)
                .map(ExporterNode.class::cast)
                .forEach(exporterNode -> exporters.create(exporterNode));

        graph.getNodes().stream()
                .filter(PipelineNode.class::isInstance)
                .map(PipelineNode.class::cast)
                .forEach(pipelineNode -> pipelines.create(pipelineNode));

        graph.getNodes().stream()
                .filter(ProcessorNode.class::isInstance)
                .map(ProcessorNode.class::cast)
                .forEach(sigletNode -> {
                    String pipelineName = sigletNode.getPipeline().getName();
                    Pipeline pipeline = pipelines.get(pipelineName);
                    if (pipeline == null) {
                        throw new SigletError(String.format("Could not find pipeline named %s", pipelineName));
                    }
                    pipeline.getProcessors().create(sigletNode);
                });

        graph.getNodes().stream()
                .filter(ReceiverNode.class::isInstance)
                .map(ReceiverNode.class::cast)
                .forEach(receivers::create);

        connect();

    }


    private SignalDestination<Signal> getDestination(String name) {
        SignalDestination<Signal> result = pipelines.getDestination(name);
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
