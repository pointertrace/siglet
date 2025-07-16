package com.siglet.container.adapter.metric;

import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

public class ProtoSummaryDataPointsAdapter extends AdapterList<SummaryDataPoint,
        SummaryDataPoint.Builder, ProtoSummaryDataPointAdapter>
        implements com.siglet.api.data.metric.SummaryDataPoints {

    public ProtoSummaryDataPointsAdapter() {
        super(AdapterListConfig.SUMMARY_DATA_POINTS_ADAPTER_CONFIG);
    }

}
