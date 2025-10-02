package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.ValueAtQuantiles;
import io.github.pointertrace.siglet.impl.adapter.AdapterList;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

public class ProtoValueAtQuantilesAdapter extends AdapterList<SummaryDataPoint.ValueAtQuantile,
        SummaryDataPoint.ValueAtQuantile.Builder, ProtoValueAtQuantileAdapter>
        implements ValueAtQuantiles {

    public ProtoValueAtQuantilesAdapter() {
        super(AdapterListConfig.VALUE_AT_QUANTILE_ADAPTER_CONFIG);
    }

}
