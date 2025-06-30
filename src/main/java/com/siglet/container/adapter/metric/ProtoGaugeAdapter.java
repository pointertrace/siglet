package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableGauge;
import com.siglet.container.adapter.Adapter;
import io.opentelemetry.proto.metrics.v1.Gauge;

public class ProtoGaugeAdapter extends Adapter<Gauge, Gauge.Builder> implements ModifiableGauge {

    private ProtoNumberDataPointsAdapter protoNumberDataPointsAdapter;

    public ProtoGaugeAdapter(Gauge protoGauge) {
        super(protoGauge, Gauge::toBuilder, Gauge.Builder::build);
    }

    public ProtoGaugeAdapter() {
    }

    public ProtoGaugeAdapter recycle(Gauge protoGauge) {
        super.recycle(protoGauge, Gauge::toBuilder, Gauge.Builder::build);
        return this;
    }

    public ProtoGaugeAdapter recycle(Gauge.Builder protoGaugeBuilder) {
        super.recycle(protoGaugeBuilder, Gauge.Builder::build);
        return this;
    }

    @Override
    public ProtoNumberDataPointsAdapter getDataPoints() {
        if (protoNumberDataPointsAdapter == null) {
            protoNumberDataPointsAdapter = new ProtoNumberDataPointsAdapter()
                    .recycle(getValue(Gauge::getDataPointsList, Gauge.Builder::getDataPointsList));
        } else if (!protoNumberDataPointsAdapter.isReady()) {
            protoNumberDataPointsAdapter
                    .recycle(getValue(Gauge::getDataPointsList, Gauge.Builder::getDataPointsList));
        }
        return protoNumberDataPointsAdapter;
    }

    @Override
    public boolean isUpdated() {
        return super.isUpdated() || (protoNumberDataPointsAdapter != null && protoNumberDataPointsAdapter.isUpdated());
    }

    @Override
    protected void enrich(Gauge.Builder builder) {
        if (protoNumberDataPointsAdapter != null && protoNumberDataPointsAdapter.isUpdated()) {
            builder.clearDataPoints();
            builder.addAllDataPoints(protoNumberDataPointsAdapter.getUpdated());
        }
    }
}
