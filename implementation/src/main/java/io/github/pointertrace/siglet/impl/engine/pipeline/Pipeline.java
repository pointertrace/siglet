package io.github.pointertrace.siglet.impl.engine.pipeline;

import io.github.pointertrace.siglet.impl.config.graph.PipelineNode;
import io.github.pointertrace.siglet.impl.engine.EngineElement;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pipeline implements EngineElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);

    private final Processors processors = new Processors();

    private State state = State.CREATED;

    private final PipelineNode node;

    public Pipeline(PipelineNode node) {
        this.node = node;
    }

    public Processors getProcessors() {
        return processors;
    }

    public SignalDestination getDestination(String name) {
        return processors.getProcessor(name);
    }

    public PipelineNode getNode(){
        return node;
    }

    @Override
    public void start() {
        processors.start();
        state = State.RUNNING;
    }

    @Override
    public void stop() {
        state = State.STOPPED;
        LOGGER.info("Stopping pipeline {}", node.getName());
        processors.stop();
        LOGGER.info("Pipeline {} stoped", node.getName());
        state = State.RUNNING;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public String getName() {
        return node.getName();
    }
}
