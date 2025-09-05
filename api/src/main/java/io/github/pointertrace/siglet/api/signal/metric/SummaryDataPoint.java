package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

public interface SummaryDataPoint {


    Attributes getAttributes();

    long getStartTimeUnixNano();

    SummaryDataPoint setStartTimeUnixNano(long startTimeUnixNano);

    long getTimeUnixNano();

    SummaryDataPoint setTimeUnixNano(long timeUnixNano);

    long getCount();

    SummaryDataPoint setCount(long count);

    int getFlags();

    SummaryDataPoint setFlags(int flags);

    double getSum();

    SummaryDataPoint setSum(double sum);

    ValueAtQuantiles getQuantileValues();

}
