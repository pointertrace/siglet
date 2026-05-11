package io.github.pointertrace.siglet.impl.eventloop;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockSignalDestination implements SignalDestination {

    public final String name;

    private final SignalCapabilities signalCapabilities;

    public final Map<String, Signal> signals = new ConcurrentHashMap<>();

    public MockSignalDestination(String name, SignalCapabilities signalCapabilities) {
        this.name = name;
        this.signalCapabilities = signalCapabilities;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean send(Signal signal) {
        signals.put(signal.getId(), signal);
        return true;
    }

    @Override
    public SignalCapabilities getIncomingCapabilities() {
        return signalCapabilities;
    }

    public <T extends Signal> T get(String id,Class<T> signalType) {
        Signal signal = signals.get(id);
        if (signal == null) {
            return null;
        }
        return signalType.cast(signal);
    }

    public boolean has(String id) {
        return signals.containsKey(id);
    }

    public int getSize() {
        return signals.size();
    }
}
