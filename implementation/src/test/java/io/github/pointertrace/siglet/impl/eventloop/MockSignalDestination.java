package io.github.pointertrace.siglet.impl.eventloop;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;

import java.util.*;

public class MockSignalDestination implements SignalDestination {

    public final String name;

    private final SignalCapabilities signalCapabilities;

    private final List<Map.Entry<String,Signal>> signals = Collections.synchronizedList(new ArrayList<>());

    public MockSignalDestination(String name, SignalCapabilities signalCapabilities) {
        this.name = name;
        this.signalCapabilities = signalCapabilities;
    }

    @Override
    public GraphComponent<?> getGraphComponent() {
        return null;
    }

    @Override
    public SignalReceiverFunction getSignalReceiverFunction() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

//    @Override
//    public boolean send(Signal signal) {
//        signals.add(new AbstractMap.SimpleImmutableEntry<>(signal.getId(), signal));
//        return true;
//    }

//    @Override
//    public SignalCapabilities getIncomingCapabilities() {
//        return signalCapabilities;
//    }
//
    public <T extends Signal> T get(String id,Class<T> signalType) {
        return  signals.stream()
                .filter(e -> e.getKey().equals(id) && signalType.isAssignableFrom(e.getValue().getClass()))
                .map(Map.Entry::getValue)
                .map(signalType::cast)
                .findFirst()
                .orElse(null);
    }

    public <T extends Signal> T get(int index,Class<T> signalType) {
        Map.Entry<String,Signal> entry = signals.get(index);
        if (entry == null) {
            return null;
        }
        return signalType.cast(entry.getValue());
    }

    public boolean has(String id) {
        return signals.stream()
                .anyMatch(e -> e.getKey().equals(id));
    }

    public int getSize() {
        return signals.size();
    }
}
