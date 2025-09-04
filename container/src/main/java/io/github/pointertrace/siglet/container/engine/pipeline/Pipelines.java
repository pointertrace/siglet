package io.github.pointertrace.siglet.container.engine.pipeline;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.graph.PipelineNode;
import io.github.pointertrace.siglet.container.engine.SignalDestination;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Pipelines {

    private final Map<String, Pipeline> pipelineRegistry = new HashMap<>();

    public Pipeline create(PipelineNode node) {
        if (pipelineRegistry.containsKey(node.getName())) {
            throw new SigletError("Pipeline with name " + node.getName() + " already exists");
        }
        return pipelineRegistry.put(node.getConfig().getName(), new Pipeline(node));
    }

    public SignalDestination getDestination(String name) {
        return pipelineRegistry.values().stream()
                .map(pipeline -> pipeline.getDestination(name))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public void forEach(Consumer<Pipeline> pipelineConsumer) {
        pipelineRegistry.values().forEach(pipelineConsumer);
    }

    public void start() {
        pipelineRegistry.values().forEach(Pipeline::start);
    }


    public void stop() {
        pipelineRegistry.values().forEach(Pipeline::stop);
    }


    public Pipeline get(String name) {
        return pipelineRegistry.get(name);
    }
}
