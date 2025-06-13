package com.siglet.container.eventloop;

import com.siglet.api.Signal;
import com.siglet.container.engine.SignalDestination;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapSignalDestination<IN extends Signal> implements SignalDestination<IN> {

    public final String name;

    private final Class<IN> type;

    public final Map<String, IN> signals = new ConcurrentHashMap<>();

    public MapSignalDestination(String name, Class<IN> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean send(IN signal) {
        signals.put(signal.getId(), signal);
        return true;
    }

    @Override
    public Class<IN> getType() {
        return type;
    }

    public IN get(String id) {
        IN signal = signals.get(id);
        if (signal == null) {
            return null;
        }
        return signal;
    }

    public boolean has(String id) {
        return signals.containsKey(id);
    }

    public int getSize() {
        return signals.size();
    }
}
