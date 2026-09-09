package io.github.pointertrace.siglet.impl.engine.metric.noop;

import io.github.pointertrace.siglet.impl.engine.metric.LongTimer;

import java.util.concurrent.TimeUnit;

public class NoopLongTimer implements LongTimer {

    @Override
    public void record(long duration, TimeUnit unit) {

    }
}
