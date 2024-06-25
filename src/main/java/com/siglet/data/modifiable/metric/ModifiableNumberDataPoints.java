package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableNumberDataPoints;

public interface ModifiableNumberDataPoints extends UnmodifiableNumberDataPoints {

    ModifiableNumberDataPoint getAt(int i);

    void remove(int i);
}
