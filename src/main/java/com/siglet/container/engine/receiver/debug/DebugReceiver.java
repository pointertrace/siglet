package com.siglet.container.engine.receiver.debug;

import com.siglet.api.Signal;
import com.siglet.container.config.graph.ReceiverNode;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.receiver.Receiver;

import java.util.ArrayList;
import java.util.List;

public class DebugReceiver implements Receiver {

    private volatile State state = State.CREATED;

    private final List<SignalDestination<Signal>> destinations = new ArrayList<>();

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
    public void connect(SignalDestination<Signal> signalDestination) {
        destinations.add(signalDestination);
    }

    public boolean send(Signal signal) {
        for(SignalDestination<Signal> destination : destinations) {
            if (!destination.send(signal)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ReceiverNode getNode() {
        return node;
    }
}
