package com.siglet.container.eventloop.processor.result;

import com.siglet.api.Signal;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.eventloop.EventLoopError;

import java.util.List;

public class SignalRoute<T extends Signal> {

    private static final Signal PROCESS_SIGNAL = new Signal() {
        @Override
        public String getId() {
            return "process-signal";
        }
    };

    private static final Signal DROP_SIGNAL = new Signal() {
        @Override
        public String getId() {
            return "drop-signal";
        }
    };

    private static final String ANY_DESTINATION = "*";

    public static <S extends Signal> SignalRoute<S> drop() {
        return new SignalRoute(DROP_SIGNAL, ANY_DESTINATION);
    }

    public static <S extends Signal> SignalRoute<S> proceed() {
        return new SignalRoute(PROCESS_SIGNAL, ANY_DESTINATION);
    }

    public static <S extends Signal> SignalRoute<S> proceed(String destination) {
        return new SignalRoute(PROCESS_SIGNAL, destination);
    }

    public static <S extends Signal> SignalRoute<S> route(Signal signal, String destination) {
        if (signal == PROCESS_SIGNAL || signal == DROP_SIGNAL || signal == null) {
            throw new EventLoopError(String.format("Invalid signal %s", signal == null ? "NULL" : signal.getId()));
        }
        if (destination == null) {
            throw new EventLoopError(String.format("Invalid signal %s", signal.getId()));

        }
        return new SignalRoute(signal, destination);
    }

    private final T signal;

    private final String destination;

    private SignalRoute(T signal, String destination) {
        this.signal = signal;
        this.destination = destination;
    }

    public void dispatch(T processSignal, List<SignalDestination<T>> destinations) {
        if (signal == DROP_SIGNAL) {
            return;
        }
        if (destination.equals(ANY_DESTINATION)) {
            for (SignalDestination<T> availableSignalDestination : destinations) {
                availableSignalDestination.send(chooseSignal(processSignal, signal));
            }
            return;
        }
        for (SignalDestination<T> availableSignalDestination : destinations) {
            if (availableSignalDestination.getName().equals(destination)) {
                availableSignalDestination.send(chooseSignal(processSignal, signal));
                return;
            }
        }
        throw new EventLoopError(String.format("Signal %s to be sent to %s which is not available",
                chooseSignal(processSignal, signal).getId(), destination));
    }

    private T chooseSignal(T processSignal, T routeSignal) {
        if (signal == PROCESS_SIGNAL) {
            return processSignal;

        } else {
            return routeSignal;
        }
    }

}
