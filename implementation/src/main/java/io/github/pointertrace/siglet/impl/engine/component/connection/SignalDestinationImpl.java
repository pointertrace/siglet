package io.github.pointertrace.siglet.impl.engine.component.connection;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;

public class SignalDestinationImpl implements SignalDestination {

    private final GraphComponent<?> graphComponent;

    private final SignalReceiverFunction signalReceiverFunction;

    public SignalDestinationImpl(GraphComponent<?> graphComponent, SignalReceiverFunction signalReceiverFunction,
                                Interceptor interceptor) {
        this.graphComponent = graphComponent;
        this.signalReceiverFunction = interceptor.componentReceiverFunctionCreation(graphComponent, signalReceiverFunction);
    }

    @Override
    public GraphComponent<?> getComponent() {
        return graphComponent;
    }

    @Override
    public SignalReceiverFunction getSignalReceiverFunction() {
        return signalReceiverFunction;
    }
}
