package com.siglet.container.adapter.metric;

import com.siglet.api.signal.metric.ExponentialHistogramDataPoints;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

public class ProtoExponentialHistogramDataPointsAdapter extends AdapterList<ExponentialHistogramDataPoint,
        ExponentialHistogramDataPoint.Builder, ProtoExponentialHistogramDataPointAdapter>
        implements ExponentialHistogramDataPoints {

    public ProtoExponentialHistogramDataPointsAdapter() {
        super(AdapterListConfig.EXPONENTIAL_HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG);
    }

}
