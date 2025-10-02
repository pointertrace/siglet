package io.github.pointertrace.siglet.impl.engine.receiver.debug;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.receiver.Receiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DebugReceiver implements Receiver {

    private volatile State state = State.CREATED;

    private final List<SignalDestination> destinations = new ArrayList<>();

    private final ReceiverNode node;

    public DebugReceiver(ReceiverNode node) {
        this.node = node;
        DebugReceivers.INSTANCE.add(this);
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
    public void connect(SignalDestination signalDestination) {
        destinations.add(signalDestination);
    }

    public boolean send(Signal signal) {
        if (! isCompatible(signal, destinations)) {
            throw new SigletError(String.format("Cannot send signal %s because there is no compatible destination",
                    signal));
        }
        for (SignalDestination destination : destinations) {
            if (isCompatible(signal, destination.getSignalCapabilities())) {
                if (!destination.send(signal)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isCompatible(Signal signal, Set<SignalType> signalTypes) {
        for (SignalType signalType : signalTypes) {
            if (signalType.isCompatible(signal)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCompatible(Signal signal, List<SignalDestination> destinations) {
        for (SignalDestination destination : destinations) {
            for (SignalType signalType : destination.getSignalCapabilities()) {
                if (signalType.isCompatible(signal)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ReceiverNode getNode() {
        return node;
    }
}
