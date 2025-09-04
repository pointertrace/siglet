package io.github.pointertrace.siglet.container.eventloop.processor.result;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.engine.SignalDestination;
import io.github.pointertrace.siglet.container.eventloop.EventLoopError;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SignalRoute {

    private static final Signal PROCESS_SIGNAL = () -> "process-signal";

    private static final Signal DROP_SIGNAL = () -> "drop-signal";

    private static final String ANY_DESTINATION = "*";

    public static  SignalRoute drop() {
        return new SignalRoute(DROP_SIGNAL, ANY_DESTINATION);
    }

    public static SignalRoute proceed() {
        return new SignalRoute(PROCESS_SIGNAL, ANY_DESTINATION);
    }

    public static SignalRoute proceed(String destination) {
        return new SignalRoute(PROCESS_SIGNAL, destination);
    }

    public static SignalRoute route(Signal signal, String destination) {
        if (signal == PROCESS_SIGNAL || signal == DROP_SIGNAL || signal == null) {
            throw new EventLoopError(String.format("Invalid signal %s", signal == null ? "NULL" : signal.getId()));
        }
        if (destination == null) {
            throw new EventLoopError(String.format("Invalid signal %s", signal.getId()));

        }
        return new SignalRoute(signal, destination);
    }

    private final Signal signal;

    private final String destination;

    private SignalRoute(Signal signal, String destination) {
        this.signal = signal;
        this.destination = destination;
    }

    public void dispatch(Map<String, String> destinationMappings, Signal processSignal,
                         List<SignalDestination> destinations) {
        if (signal == DROP_SIGNAL) {
            return;
        }
        if (destination.equals(ANY_DESTINATION)) {
            for (SignalDestination availableSignalDestination : destinations) {
                availableSignalDestination.send(chooseSignal(processSignal, signal));
            }
            return;
        }
        for (SignalDestination availableSignalDestination : destinations) {
            String destinationName;
            if (!destinationMappings.isEmpty()) {
                destinationName = destinationMappings.get(destination);
                if (destinationName == null) {
                    throw new EventLoopError(String.format("Destination %s is not mapped to any signal", destination));
                }
            } else {
                destinationName = destination;
            }
            if (destinationName.equals(availableSignalDestination.getName())) {
                availableSignalDestination.send(chooseSignal(processSignal, signal));
                return;
            }
        }
        if (destinationMappings.isEmpty()) {
            throw new EventLoopError(String.format("Signal %s to be sent to %s which is not available (%s)",
                    chooseSignal(processSignal, signal).getId(), destination,
                    destinations.stream().map(SignalDestination::getName).collect(Collectors.joining(","))));
        } else {
            throw new EventLoopError(String.format("Signal %s to be sent to %s which is not available (%s)",
                    chooseSignal(processSignal, signal).getId(), destination,
                    destinationMappings.keySet().stream()
                            .sorted()
                            .map(key -> key + ":" + destinationMappings.get(key))
                            .collect(Collectors.joining(","))));
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SignalRoute that = (SignalRoute) o;
        return Objects.equals(signal, that.signal) && Objects.equals(destination, that.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signal, destination);
    }

    private Signal chooseSignal(Signal processSignal, Signal routeSignal) {
        if (signal == PROCESS_SIGNAL) {
            return processSignal;

        } else {
            return routeSignal;
        }
    }

}
