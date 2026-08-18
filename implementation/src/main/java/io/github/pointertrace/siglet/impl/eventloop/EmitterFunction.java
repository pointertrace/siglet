package io.github.pointertrace.siglet.impl.eventloop;

@FunctionalInterface
public interface EmitterFunction<OUT> {

    void emit(OUT signal);
}
