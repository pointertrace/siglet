package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableValueAtQuantiles {

    int getSize();

    UnmodifiableValueAtQuantile getAt(int i);
}
