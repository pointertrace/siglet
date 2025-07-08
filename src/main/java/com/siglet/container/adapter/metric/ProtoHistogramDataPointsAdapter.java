package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableHistogramDataPoints;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;

public class ProtoHistogramDataPointsAdapter extends AdapterList<HistogramDataPoint, HistogramDataPoint.Builder,
        ProtoHistogramDataPointAdapter> implements ModifiableHistogramDataPoints {

    public ProtoHistogramDataPointsAdapter() {
        super(AdapterListConfig.HISTOGRAM_DATA_POINTS_ADAPTER_CONFIG);
    }

}
