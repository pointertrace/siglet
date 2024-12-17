package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.Adapter;
import com.siglet.data.modifiable.metric.ModifiableGauge;
import io.opentelemetry.proto.metrics.v1.Gauge;

public class ProtoGaugeAdapter extends Adapter<Gauge, Gauge.Builder> implements ModifiableGauge {

    private ProtoNumberDataPointsAdapter protoNumberDataPointsAdapter;

    public ProtoGaugeAdapter(Gauge protoGauge, boolean updatable) {
        super(protoGauge,Gauge::toBuilder,Gauge.Builder::build,updatable);
    }

    public ProtoGaugeAdapter() {
        super(Gauge.newBuilder(), Gauge.Builder::build);
    }

    @Override
    public ProtoNumberDataPointsAdapter getDataPoints() {
        if (protoNumberDataPointsAdapter == null) {
            protoNumberDataPointsAdapter = new ProtoNumberDataPointsAdapter(
                    getValue(Gauge::getDataPointsList,Gauge.Builder::getDataPointsList),isUpdatable());
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
