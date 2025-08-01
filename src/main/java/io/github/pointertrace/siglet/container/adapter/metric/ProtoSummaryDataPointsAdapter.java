package io.github.pointertrace.siglet.container.adapter.metric;

import io.github.pointertrace.siglet.container.adapter.AdapterList;
import io.github.pointertrace.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

public class ProtoSummaryDataPointsAdapter extends AdapterList<SummaryDataPoint,
        SummaryDataPoint.Builder, ProtoSummaryDataPointAdapter>
        implements io.github.pointertrace.siglet.api.signal.metric.SummaryDataPoints {

    public ProtoSummaryDataPointsAdapter() {
        super(AdapterListConfig.SUMMARY_DATA_POINTS_ADAPTER_CONFIG);
    }

}
