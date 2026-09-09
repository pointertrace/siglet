package io.github.pointertrace.siglet.impl.engine.exporter.debug;

import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestinationImpl;
import io.github.pointertrace.siglet.impl.engine.exporter.BaseExporter;

import java.util.ArrayList;
import java.util.List;

public class DebugExporter extends BaseExporter {


    List<Object> signals = new ArrayList<>();

    private final SignalDestination signalDestination;


    public DebugExporter(SigletContext sigletContext, ExporterNode node) {
        super(sigletContext, node);

        DebugExporters.INSTANCE.addExporter(node.getName());
        signalDestination = new SignalDestinationImpl(this, this::receive, getSigletContext().getInterceptor());
    }

    @Override
    protected void doStart() {
    }

    @Override
    protected void doStop() {
    }

    @Override
    public SignalDestination getSignalDestination() {
        return signalDestination;
    }

    private boolean receive(Object signal) {
        DebugExporters.INSTANCE.addSignal(getName(), signal);
        return true;
    }

    private <T> List<T> getSignals(Class<T> type) {
        return signals.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }
}
