package io.github.pointertrace.siglet.impl.engine.exporter.debug;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;

public class DebugExporter implements Exporter {

    private volatile State state = State.CREATED;

    private final ExporterNode node;

    private final SignalCapabilities signalCapabilities = SignalCapabilities.of(Signal.class);

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
    public SignalCapabilities getIncomingCapabilities() {
        return signalCapabilities;
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
