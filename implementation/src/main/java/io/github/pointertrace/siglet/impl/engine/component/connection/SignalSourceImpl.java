package io.github.pointertrace.siglet.impl.engine.component.connection;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

public class SignalSourceImpl implements SignalSource {

    private final GraphComponent<?> source;

    private final List<AvailableDestination> availableDestinations = new ArrayList<>();

    private final DropAvailableDestination dropAvailableDestination;

    private final Interceptor interceptor;

    public SignalSourceImpl(GraphComponent<?> source, Interceptor interceptor) {
        this.source = source;
        this.interceptor = interceptor;
        this.dropAvailableDestination = new DropAvailableDestination(interceptor);
    }

    @Override
    public GraphComponent<?> getGraphComponent() {
        return source;
    }

    @Override
    public void connect(SignalDestination destination) {
        availableDestinations.add(new AvailableDestination(destination, interceptor));
    }

    @Override
    public SignalEmitterFunction getSignalEmitterFunction() {
        return this::emit;
    }

    private void emit(Object signal, String destination) {
        if (SignalDestination.isAll(destination)) {
            for (AvailableDestination availableDestination : availableDestinations) {
                availableDestination.receive(signal);
            }
        } else if (SignalDestination.isDrop(destination)) {
            dropAvailableDestination.receive(signal);
        } else {
            for (AvailableDestination availableDestination : availableDestinations) {
                if (availableDestination.is(destination)) {
                    availableDestination.receive(signal);
                }
            }
        }
    }

    private class AvailableDestination {

        private final SignalDestination destination;

        protected final Interceptor interceptor;

        private final SignalReceiverFunction signalReceiverFunction;

        private AvailableDestination(SignalDestination destination, Interceptor interceptor) {
            this.destination = destination;
            this.interceptor = interceptor;
            this.signalReceiverFunction = createSignalReceiverFunction(destination, interceptor);
        }

        protected SignalReceiverFunction createSignalReceiverFunction(
                SignalDestination destination, Interceptor interceptor) {
            return interceptor.componentEmitterFunctionCreation(
                    SignalSourceImpl.this.getGraphComponent(),destination.getComponent(), destination.getSignalReceiverFunction());
        }

        public boolean is(String destinationName) {
            return destination.is(destinationName);
        }

        public void receive(Object signal) {
            signalReceiverFunction.receive(signal);
        }

    }

    private class DropAvailableDestination extends AvailableDestination {

        private DropAvailableDestination(Interceptor interceptor) {
            super(null, interceptor);
        }

        protected SignalReceiverFunction createSignalReceiverFunction(
                SignalDestination destination, Interceptor interceptor) {
            return interceptor.componentEmitterDropFunctionCreation(SignalSourceImpl.this.getGraphComponent());
        }

    }

}
