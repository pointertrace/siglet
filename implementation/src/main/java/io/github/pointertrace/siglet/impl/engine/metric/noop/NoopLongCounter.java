package io.github.pointertrace.siglet.impl.engine.metric.noop;

import io.github.pointertrace.siglet.impl.engine.metric.LongCounter;

public class NoopLongCounter implements LongCounter {

    @Override
    public void increment(long amount) {
    }

    @Override
    public void increment() {
    }
}
