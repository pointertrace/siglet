package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.ExponentialHistogramDataPoints;
import io.github.pointertrace.siglet.impl.adapter.AdapterList;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

public class ProtoExponentialHistogramDataPointsAdapter extends AdapterList<ExponentialHistogramDataPoint,
        ExponentialHistogramDataPoint.Builder, ProtoExponentialHistogramDataPointAdapter>
        implements ExponentialHistogramDataPoints {

    public ProtoExponentialHistogramDataPointsAdapter() {
        super(AdapterListConfig.EXPONENTIAL_HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG);
    }

}
