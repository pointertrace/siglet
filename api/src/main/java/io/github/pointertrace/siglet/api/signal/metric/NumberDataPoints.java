package io.github.pointertrace.siglet.api.signal.metric;

public interface NumberDataPoints {

    int getSize();

    NumberDataPoint get(int i);

    void remove(int i);

    NumberDataPoint add();
}
