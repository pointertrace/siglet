package io.github.pointertrace.siglet.container.engine.pipeline;

import io.github.pointertrace.siglet.container.config.graph.PipelineNode;
import io.github.pointertrace.siglet.container.engine.EngineElement;
import io.github.pointertrace.siglet.container.engine.SignalDestination;
import io.github.pointertrace.siglet.container.engine.State;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.Processors;

public class Pipeline implements EngineElement {

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
        processors.stop();
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
