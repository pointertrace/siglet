package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableSummaryDataPoints;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

public class ProtoSummaryDataPointsAdapter extends AdapterList<SummaryDataPoint,
        SummaryDataPoint.Builder, ProtoSummaryDataPointAdapter>
        implements ModifiableSummaryDataPoints {

    public ProtoSummaryDataPointsAdapter() {
        super(AdapterListConfig.SUMMARY_DATA_POINTS_ADAPTER_CONFIG);
    }

}
