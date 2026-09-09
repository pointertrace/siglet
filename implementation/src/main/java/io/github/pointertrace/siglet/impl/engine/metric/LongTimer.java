package io.github.pointertrace.siglet.impl.engine.metric;

import java.util.concurrent.TimeUnit;

public interface LongTimer {

    void record(long duration, TimeUnit unit);

}
