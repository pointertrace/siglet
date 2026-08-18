package io.github.pointertrace.siglet.impl.engine.component;

@FunctionalInterface
public interface SignalEmitterFunction {

    void emit(Object signal, String destinationName);

}
