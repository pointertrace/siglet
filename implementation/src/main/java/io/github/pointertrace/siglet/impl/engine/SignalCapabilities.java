package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;

import java.util.Set;

public class SignalCapabilities {

    private final Set<Class<? extends Signal>> signalsTypes;


    @SafeVarargs
    private SignalCapabilities(Class<? extends Signal> ...signalsTypes) {
        this.signalsTypes = Set.of(signalsTypes);
    }

    @SafeVarargs
    public static SignalCapabilities of(Class<? extends Signal> ...signals) {
        return new SignalCapabilities(signals);
    }

    public static SignalCapabilities of(String sigletName) {
        if (sigletName.toLowerCase().contains("span")) {
            return new SignalCapabilities(Span.class);
        } else if (sigletName.toLowerCase().contains("metric")) {
            return new SignalCapabilities(Metric.class);
        }  else {
            throw new SigletError(String.format("Cannot infer signal type for siglet named %s",sigletName));
        }
    }

    public boolean isAbleToHandle(Signal signal) {
        return isAbleToHandle(signal.getClass());
    }

    public boolean isAbleToHandle(Class<? extends Signal> signalType) {
        for(Class<? extends Signal> signalClass : signalsTypes) {
            if(signalClass.isAssignableFrom(signalType)) {
                return true;
            }
        }
        return false;
    }

    public void checkIsAbleToHandle(Signal signal) {
        checkIsAbleToHandle(signal.getClass());
    }

    public void checkIsAbleToHandle(Class<? extends Signal> signalType) {
        if (! isAbleToHandle(signalType)) {
            throw new SigletError(String.format("Signal type [%s] cannot be handled! Can only handle signal types [%s]",
                    signalType.getName(),
                    String.join(", ", signalsTypes.stream().map(Class::getName).toList())));
        }
    }

    public boolean isAbleToSend(SignalCapabilities destinationCapabilities) {
        for(Class<? extends Signal> incomingSignalType : signalsTypes) {
            for(Class<? extends Signal> outgoingSignalType : destinationCapabilities.signalsTypes) {
                if (outgoingSignalType.isAssignableFrom(incomingSignalType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String print() {
        return String.join(", ", signalsTypes.stream().map(Class::getName).toList());
    }


}
