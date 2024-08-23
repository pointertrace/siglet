package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.AggregationTemporality;
import com.siglet.data.unmodifiable.metric.UnmodifiableExponentialHistogram;

public interface ModifiableExponentialHistogram extends UnmodifiableExponentialHistogram {

    ModifiableNumberDataPoints getDataPoints();

    ModifiableExponentialHistogram setAggregationTemporality(AggregationTemporality aggregationTemporality);

}
