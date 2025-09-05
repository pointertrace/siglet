package io.github.pointertrace.siglet.api.signal.metric;

public interface ExponentialHistogramDataPoints {

    int getSize();

    ExponentialHistogramDataPoint get(int i);

    void remove(int i);
}
