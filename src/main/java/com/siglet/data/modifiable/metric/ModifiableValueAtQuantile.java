package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableValueAtQuantile;

public interface ModifiableValueAtQuantile extends UnmodifiableValueAtQuantile {

    ModifiableValueAtQuantile setQuantile(double quantile);

    ModifiableValueAtQuantile setValue(double value);
}
