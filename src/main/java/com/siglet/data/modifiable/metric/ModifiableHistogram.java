package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.AggregationTemporality;
import com.siglet.data.unmodifiable.metric.UnmodifiableHistogram;

public interface ModifiableHistogram extends ModifiableData, UnmodifiableHistogram {

    ModifiableHistogramDataPoints getDataPoints();

    ModifiableHistogram setAggregationTemporality(AggregationTemporality aggregationTemporality);

}
