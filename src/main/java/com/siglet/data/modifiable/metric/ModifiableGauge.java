package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableGauge;

public interface ModifiableGauge extends ModifiableData, UnmodifiableGauge {


   ModifiableNumberDataPoints getDataPoints();

}
