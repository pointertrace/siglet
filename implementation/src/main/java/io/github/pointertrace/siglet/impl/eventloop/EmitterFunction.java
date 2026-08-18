package io.github.pointertrace.siglet.impl.eventloop;

@FunctionalInterface
public interface EmitterFunction<T> {

    void emit(T signal);
}
