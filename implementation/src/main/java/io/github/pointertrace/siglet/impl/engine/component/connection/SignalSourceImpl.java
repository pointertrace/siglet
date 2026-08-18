package io.github.pointertrace.siglet.impl.engine.component.connection;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;

import java.util.ArrayList;
import java.util.List;

public class SignalSourceImpl implements SignalSource {

    private final GraphComponent<?> source;

    private final List<SignalDestination> availableDestinations = new ArrayList<>();

    public SignalSourceImpl(GraphComponent<?> source) {
        this.source = source;
    }

    @Override
    public GraphComponent<?> getGraphComponent() {
        return source;
    }

    @Override
    public void connect(SignalDestination destination) {
        availableDestinations.add(destination);
    }

    @Override
    public SignalEmitterFunction getSignalEmitterFunction() {
        return this::emit;
    }

    private boolean emit(Object signal, String destination) {
        if (SignalDestination.isAll(destination)) {
            for(SignalDestination availableDestination : availableDestinations) {
                availableDestination.getSignalReceiverFunction().receive(signal);
            }
            return true;
        } else if (SignalDestination.isDrop(destination)) {
            return true;
        } else {
            boolean result = true;
            for (SignalDestination availableDestination : availableDestinations) {
                if (availableDestination.is(destination)) {
                    result &= availableDestination.getSignalReceiverFunction().receive(signal);
                }
            }
            return result;
        }
    }



}
