package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.impl.adapter.AdapterList;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;

public class ProtoHistogramDataPointsAdapter extends AdapterList<HistogramDataPoint, HistogramDataPoint.Builder,
        ProtoHistogramDataPointAdapter> implements io.github.pointertrace.siglet.api.signal.metric.HistogramDataPoints {

    public ProtoHistogramDataPointsAdapter() {
        super(AdapterListConfig.HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG);
    }

}
