package com.siglet.container.adapter.metric;

import com.siglet.api.data.metric.ValueAtQuantiles;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

public class ProtoValueAtQuantilesAdapter extends AdapterList<SummaryDataPoint.ValueAtQuantile,
        SummaryDataPoint.ValueAtQuantile.Builder, ProtoValueAtQuantileAdapter>
        implements ValueAtQuantiles {

    public ProtoValueAtQuantilesAdapter() {
        super(AdapterListConfig.VALUE_AT_QUANTILE_ADAPTER_CONFIG);
    }

}
