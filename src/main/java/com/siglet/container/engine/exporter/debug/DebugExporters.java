package com.siglet.container.engine.exporter.debug;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.config.raw.SignalType;

import java.util.*;

public class DebugExporters {

    public final static DebugExporters INSTANCE = new DebugExporters();

    private final Map<String, List<Signal>> exportedSignals = new HashMap<>();

    private DebugExporters() {
    }

    protected synchronized void addExporter(String exporter) {
        exportedSignals.put(exporter, Collections.synchronizedList(new ArrayList<Signal>()));
    }

    public synchronized void addSignal(String exporter, Signal signal) {
        List<Signal> signals = exportedSignals.get(exporter);
        if (signals == null) {
            throw new SigletError(String.format("Cannot find debug exporter named %s", exporter));
        }
        signals.add(signal);
    }

    public synchronized <T extends Signal> List<T> get(String exporter, Class<T> signalType) {
        List<Signal> signals = exportedSignals.get(exporter);
        if (signals == null) {
            throw new SigletError(String.format("Cannot find debug exporter named %s", exporter));
        }
        return signals.stream()
                .filter(signalType::isInstance)
                .map(signalType::cast).toList();
    }

    public synchronized List<Signal> get(String exporter) {
        List<Signal> signals = exportedSignals.get(exporter);
        if (signals == null) {
            throw new SigletError(String.format("Cannot find debug exporter named %s", exporter));
        }
        return signals;
    }
}
