package com.siglet.container.adapter.metric;

import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;

public class ProtoHistogramDataPointsAdapter extends AdapterList<HistogramDataPoint, HistogramDataPoint.Builder,
        ProtoHistogramDataPointAdapter> implements com.siglet.api.signal.metric.HistogramDataPoints {

    public ProtoHistogramDataPointsAdapter() {
        super(AdapterListConfig.HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG);
    }

}
