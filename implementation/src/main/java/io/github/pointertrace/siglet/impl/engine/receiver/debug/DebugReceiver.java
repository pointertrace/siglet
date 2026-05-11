package io.github.pointertrace.siglet.impl.engine.receiver.debug;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.receiver.Receiver;

import java.util.ArrayList;
import java.util.List;

public class DebugReceiver implements Receiver {

    private volatile State state = State.CREATED;

    private final List<SignalDestination> destinations = new ArrayList<>();

    private final SignalCapabilities signalCapabilities = SignalCapabilities.of(Signal.class);

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
        signalCapabilities.checkCompatibility(signalDestination.getIncomingCapabilities());
        destinations.add(signalDestination);
    }

    @Override
    public SignalCapabilities  getOutgoingCapabilities() {
        return signalCapabilities;
    }

    public boolean send(Signal signal) {
        for (SignalDestination destination : destinations) {
            if (signalCapabilities.isAbleToHandle(signal)) {
                if (!destination.send(signal)) {
                    return false;
                }
            }
        }
        return true;
    }



    @Override
    public ReceiverNode getNode() {
        return node;
    }
}
