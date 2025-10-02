package io.github.pointertrace.siglet.impl.engine.pipeline;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.graph.PipelineNode;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Pipelines {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pipelines.class);

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
        LOGGER.info("Stopping pipelines");
        pipelineRegistry.values().forEach(Pipeline::stop);
        LOGGER.info("All Pipelines stoped");
    }


    public Pipeline get(String name) {
        return pipelineRegistry.get(name);
    }
}
