package io.github.pointertrace.siglet.impl.engine.metric.noop;

import io.github.pointertrace.siglet.impl.engine.metric.LongGauge;

public class NoopLongGauge implements LongGauge {

    @Override
    public void set(long value) {

    }
}
