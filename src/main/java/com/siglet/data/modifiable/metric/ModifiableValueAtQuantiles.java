package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableValueAtQuantiles;

public interface ModifiableValueAtQuantiles extends UnmodifiableValueAtQuantiles {

    ModifiableValueAtQuantile getAt(int i);

    void remove(int i);
}
