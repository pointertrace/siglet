package com.siglet.data.unmodifiable.metric;

import com.siglet.data.unmodifiable.UnmodifiableAttributes;

public interface UnmodifiableSummaryDataPoint {

    UnmodifiableAttributes getAttributes();

    long getStartTimeUnixNano();

    long getTimeUnixNano();

    long getCount();

    int getFlags();

    double getSum();

    UnmodifiableValueAtQuantiles getQuantileValues();

}
