package com.siglet.container.engine.exporter.debug;

import com.siglet.api.Signal;
import com.siglet.container.config.graph.ExporterNode;
import com.siglet.container.config.raw.SignalType;
import com.siglet.container.engine.State;
import com.siglet.container.engine.exporter.Exporter;

import java.util.Set;

public class DebugExporter implements Exporter {

    private volatile State state = State.CREATED;

    private final ExporterNode node;

    public DebugExporter(ExporterNode node) {
        this.node = node;
        DebugExporters.INSTANCE.addExporter(node.getName());
    }

    @Override
    public boolean send(Signal signal) {
        DebugExporters.INSTANCE.addSignal(getName(), signal);
        return true;
    }

    @Override
    public Set<SignalType> getSignalCapabilities() {
        return Set.of(SignalType.TRACE, SignalType.METRIC);
    }

    @Override
    public void start() {
        state = State.RUNNING;
    }

    @Override
    public void stop() {
        state = State.STOPPED;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public String getName() {
        return node.getName();
    }

    @Override
    public ExporterNode getNode() {
        return node;
    }
}
