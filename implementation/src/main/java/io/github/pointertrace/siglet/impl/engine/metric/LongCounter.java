package io.github.pointertrace.siglet.impl.engine.metric;

public interface LongCounter {

    void increment(long amount);

    void increment();
}
