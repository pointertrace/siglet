package io.github.pointertrace.siglet.api.signal.metric;

public interface SummaryDataPoints {

    int getSize();

    SummaryDataPoint get(int i);

    void remove(int i);
}
