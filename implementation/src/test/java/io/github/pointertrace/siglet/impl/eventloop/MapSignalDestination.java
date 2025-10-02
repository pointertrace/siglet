package io.github.pointertrace.siglet.impl.eventloop;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MapSignalDestination implements SignalDestination {

    public final String name;

    public final Map<String, Signal> signals = new ConcurrentHashMap<>();

    public MapSignalDestination(String name) {
        this.name = name;
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
    public Set<SignalType> getSignalCapabilities() {
        return Set.of(SignalType.TRACE);
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
