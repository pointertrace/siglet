package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.AggregationTemporality;
import com.siglet.data.unmodifiable.metric.UnmodifiableSum;

public interface ModifiableSum extends UnmodifiableSum, ModifiableData {

    ModifiableNumberDataPoints getDataPoints();

    ModifiableSum setAggregationTemporality(AggregationTemporality aggregationTemporality);

    ModifiableSum setMonotonic(boolean monotonic);

}
