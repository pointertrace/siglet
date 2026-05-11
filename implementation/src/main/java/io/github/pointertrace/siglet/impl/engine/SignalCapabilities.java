package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;

import java.util.Collections;
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
        if (sigletName.toLowerCase().contains("spanlet")) {
            return new SignalCapabilities(Span.class);
        } else if (sigletName.toLowerCase().contains("metriclet")) {
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
            throw new SigletError(String.format("Can only handle signal types %s and signal is type %s for signal %s",
                    String.join(", ", signalsTypes.stream().map(Class::getSimpleName).toList()),
                    signalType.getSimpleName(), signalType));
        }
    }

    public boolean isCompatible(SignalCapabilities other) {
        return !Collections.disjoint(signalsTypes,other.signalsTypes);
    }

    public void checkCompatibility(SignalCapabilities other) {
        if (! isCompatible(other)) {
            throw new SigletError(String.format("The two components are not compatible because there is no intersection between them (%s,%s)",
                    String.join(", ", signalsTypes.stream().map(Class::getSimpleName).toList()),
                    String.join(", ", other.signalsTypes.stream().map(Class::getSimpleName).toList())));
        }
    }
}
