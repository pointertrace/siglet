package com.siglet.container.engine.pipeline;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.config.graph.PipelineNode;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.SignalDestination;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Pipelines {

    private final Map<String, Pipeline> pipelines = new HashMap<>();

    public Pipeline create(PipelineNode node) {
        if (pipelines.containsKey(node.getName())) {
            throw new SigletError("Pipeline with name " + node.getName() + " already exists");
        }
        return pipelines.put(node.getConfig().getName(), new Pipeline(node));
    }

    public SignalDestination<Signal> getDestination(String name) {
        return pipelines.values().stream()
                .map(pipeline -> pipeline.getDestination(name))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public void forEach(Consumer<Pipeline> pipelineConsumer) {
        pipelines.values().forEach(pipelineConsumer);
    }

    public void start() {
        pipelines.values().forEach(Pipeline::start);
    }


    public void stop() {
        pipelines.values().forEach(Pipeline::stop);
    }


    public Pipeline get(String name) {
        return pipelines.get(name);
    }
}
