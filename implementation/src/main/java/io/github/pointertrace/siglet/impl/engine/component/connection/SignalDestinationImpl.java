package io.github.pointertrace.siglet.impl.engine.component.connection;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;

public class SignalDestinationImpl implements SignalDestination {

    private final GraphComponent<?> graphComponent;

    private final SignalReceiverFunction signalReceiverFunction;

    public SignalDestinationImpl(GraphComponent<?> graphComponent, SignalReceiverFunction signalReceiverFunction) {
        this.graphComponent = graphComponent;
        this.signalReceiverFunction = signalReceiverFunction;
    }

    @Override
    public GraphComponent<?> getGraphComponent() {
        return graphComponent;
    }

    @Override
    public SignalReceiverFunction getSignalReceiverFunction() {
        return signalReceiverFunction;
    }
}
