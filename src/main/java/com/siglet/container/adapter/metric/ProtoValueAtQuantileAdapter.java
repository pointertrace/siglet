package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableValueAtQuantile;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

public class ProtoValueAtQuantileAdapter extends Adapter<SummaryDataPoint.ValueAtQuantile,
        SummaryDataPoint.ValueAtQuantile.Builder>
        implements ModifiableValueAtQuantile {

    public ProtoValueAtQuantileAdapter () {
        super(AdapterConfig.SUMMARY_DATA_POINT_VALUE_AT_QUANTILE_ADAPTER_CONFIG);
    }

    @Override
    public ModifiableValueAtQuantile setQuantile(double quantile) {
        setValue(SummaryDataPoint.ValueAtQuantile.Builder::setQuantile, quantile);
        return this;
    }

    @Override
    public ModifiableValueAtQuantile setValue(double value) {
        setValue(SummaryDataPoint.ValueAtQuantile.Builder::setValue, value);
        return this;
    }

    @Override
    public double getQuantile() {
        return getValue(SummaryDataPoint.ValueAtQuantile::getQuantile,
                SummaryDataPoint.ValueAtQuantile.Builder::getQuantile);
    }

    @Override
    public double getValue() {
        return getValue(SummaryDataPoint.ValueAtQuantile::getValue,
                SummaryDataPoint.ValueAtQuantile.Builder::getValue);
    }
}
