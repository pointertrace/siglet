package io.github.pointertrace.siglet.api.signal.metric;

public interface HistogramDataPoints {

    int getSize();

    HistogramDataPoint get(int i);

    void remove(int i);
}
