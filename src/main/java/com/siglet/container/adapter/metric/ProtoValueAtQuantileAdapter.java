package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableValueAtQuantile;
import com.siglet.container.adapter.Adapter;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

public class ProtoValueAtQuantileAdapter extends Adapter<SummaryDataPoint.ValueAtQuantile,
        SummaryDataPoint.ValueAtQuantile.Builder>
        implements ModifiableValueAtQuantile {

    public ProtoValueAtQuantileAdapter(SummaryDataPoint.ValueAtQuantile message) {
        super(message, SummaryDataPoint.ValueAtQuantile::toBuilder,
        SummaryDataPoint.ValueAtQuantile.Builder::build);
    }

    public ProtoValueAtQuantileAdapter(SummaryDataPoint.ValueAtQuantile.Builder builder) {
        super(builder, SummaryDataPoint.ValueAtQuantile.Builder::build);
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
