package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;

import java.util.Map;
import java.util.Objects;

public interface SignalRoute {


    static SignalRoute drop() {
        return new DefaultSignalRoute(SignalDestination.DROP);
    }

    static SignalRoute proceed() {
        return new DefaultSignalRoute(SignalDestination.ALL);
    }

    static SignalRoute proceed(String destination) {
        return new DefaultSignalRoute(destination);
    }

    static SignalRoute route(Object signal, String destination) {
        Objects.requireNonNull(destination, "Destination must not be null");
        Objects.requireNonNull(signal, "Signal must not be null");
        if (SignalDestination.isDrop(destination)) {
            throw new SigletError("Non default signal cannot have drop destination");
        }
        return new NonDefaultSignalRoute(signal, destination);
    }

    void emmit(Map<String, String> destinationMappings, SignalEmitterFunction emitter, Object processorSignal);

    class DefaultSignalRoute implements SignalRoute {

        private final String destination;

        public DefaultSignalRoute(String destination) {
            this.destination = destination;
        }

        @Override
        public void emmit(Map<String, String> destinationMappings, SignalEmitterFunction emitter, Object processorSignal) {
            emitter.emit(processorSignal, destinationMappings.getOrDefault(destination, destination));
        }
    }

    class NonDefaultSignalRoute implements SignalRoute {

        private final Object signal;
        private final String destination;

        public NonDefaultSignalRoute(Object signal, String destination) {
            this.signal = signal;
            this.destination = destination;
        }

        @Override
        public void emmit(Map<String, String> destinationMappings, SignalEmitterFunction emitter, Object processorSignal) {
            emitter.emit(signal, destinationMappings.getOrDefault(destination, destination));
        }
    }
}
