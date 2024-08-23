package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.AggregationTemporality;
import com.siglet.data.unmodifiable.metric.UnmodifiableHistogram;
import com.siglet.data.unmodifiable.metric.UnmodifiableSum;

public interface ModifiableHistogram extends UnmodifiableHistogram {

    ModifiableNumberDataPoints getDataPoints();

    ModifiableHistogram setAggregationTemporality(AggregationTemporality aggregationTemporality);

}
