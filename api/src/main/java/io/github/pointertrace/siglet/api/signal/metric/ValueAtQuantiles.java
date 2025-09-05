package io.github.pointertrace.siglet.api.signal.metric;

public interface ValueAtQuantiles {

    int getSize();

    ValueAtQuantile get(int i);

    void remove(int i);
}
