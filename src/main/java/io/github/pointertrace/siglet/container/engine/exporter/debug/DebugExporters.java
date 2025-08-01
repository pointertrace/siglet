package io.github.pointertrace.siglet.container.engine.exporter.debug;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.api.Signal;

import java.util.*;

public class DebugExporters {

    public static final DebugExporters INSTANCE = new DebugExporters();
    public static final String EXPORTER_NOT_FOUND = "Cannot find debug exporter named %s";

    private final Map<String, List<Signal>> exportedSignals = new HashMap<>();

    private DebugExporters() {
    }

    protected synchronized void addExporter(String exporter) {
        exportedSignals.put(exporter, Collections.synchronizedList(new ArrayList<Signal>()));
    }

    public synchronized void addSignal(String exporter, Signal signal) {
        List<Signal> signals = exportedSignals.get(exporter);
        if (signals == null) {
            throw new SigletError(String.format(EXPORTER_NOT_FOUND, exporter));
        }
        signals.add(signal);
    }

    public synchronized <T extends Signal> List<T> get(String exporter, Class<T> signalType) {
        List<Signal> signals = exportedSignals.get(exporter);
        if (signals == null) {
            throw new SigletError(String.format(EXPORTER_NOT_FOUND, exporter));
        }
        return signals.stream()
                .filter(signalType::isInstance)
                .map(signalType::cast).toList();
    }

    public synchronized List<Signal> get(String exporter) {
        List<Signal> signals = exportedSignals.get(exporter);
        if (signals == null) {
            throw new SigletError(String.format(EXPORTER_NOT_FOUND, exporter));
        }
        return signals;
    }
}
