package com.siglet.data.adapter.metric;

import com.siglet.data.modifiable.metric.ModifiableGauge;
import io.opentelemetry.proto.metrics.v1.Gauge;

public class ProtoGaugeAdapter implements ModifiableGauge {

    private final Gauge protoGauge;

    private final boolean updatable;

    private boolean updated;

    private Gauge.Builder protoGaugeBuilder;

    public ProtoGaugeAdapter(Gauge protoGauge, boolean updatable) {
        this.protoGauge = protoGauge;
        this.updatable = updatable;
    }


    @Override
    public ProtoDataPointsAdapter getDataPoints() {
        return null;
    }
}
