package com.siglet.data.modifiable.metric;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.unmodifiable.metric.UnmodifiableSummaryDataPoint;

public interface ModifiableSummaryDataPoint extends UnmodifiableSummaryDataPoint {

    ModifiableAttributes getAttributes();

    ModifiableSummaryDataPoint setStartTimeUnixNano(long startTimeUnixNano);

    ModifiableSummaryDataPoint setTimeUnixNano(long timeUnixNano);

    ModifiableSummaryDataPoint setCount(long count);

    ModifiableSummaryDataPoint setFlags(int flags);

    ModifiableSummaryDataPoint setSum(double sum);

    ModifiableValueAtQuantiles getQuantileValues();

}
