package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

import java.util.List;

public interface HistogramDataPoint {


    Attributes getAttributes();

    Exemplars getExemplars();

    long getStartTimeUnixNano();

    HistogramDataPoint setStartTimeUnixNano(long startTimeUnixNano);

    long getTimeUnixNano();

    HistogramDataPoint setTimeUnixNano(long timeUnixNano);

    long getCount();

    HistogramDataPoint setCount(long count);

    int getFlags();

    HistogramDataPoint setFlags(int flags);

    double getSum();

    HistogramDataPoint setSum(double sum);

    List<Long> getBucketCounts();

    HistogramDataPoint addBucketCount(long count);

    List<Double> getExplicitBounds();

    HistogramDataPoint addAllBucketCounts(List<Long> count);

    HistogramDataPoint clearBucketCounts();

    HistogramDataPoint addExplicitBound(Double explicitBound);

    HistogramDataPoint addAllExplicitBounds(List<Double> count);

    HistogramDataPoint clearExplicitBounds();

    Double getMin();

    HistogramDataPoint setMin(Double min);

    Double getMax();

    HistogramDataPoint setMax(Double max);

}
