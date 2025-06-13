package com.siglet.container.engine.pipeline;

import com.siglet.api.Signal;
import com.siglet.container.config.graph.PipelineNode;
import com.siglet.container.engine.EngineElement;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.pipeline.processor.Processors;

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

    public SignalDestination<Signal> getDestination(String name) {
        return processors.getDestination(name);
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
