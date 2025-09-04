package io.github.pointertrace.siglet.container.engine.exporter.debug;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.config.graph.ExporterNode;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.engine.State;
import io.github.pointertrace.siglet.container.engine.exporter.Exporter;

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
        return Set.of(SignalType.SPAN, SignalType.METRIC);
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
