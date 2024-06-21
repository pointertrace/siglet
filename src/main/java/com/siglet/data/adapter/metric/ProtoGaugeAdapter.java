package com.siglet.data.adapter.metric;

import com.siglet.data.modifiable.metric.ModifiableGauge;
import io.opentelemetry.proto.metrics.v1.Gauge;

public class ProtoGaugeAdapter implements ModifiableGauge {

    private final Gauge protoGauge;

    private final boolean updatable;

    private ProtoNumberDataPointsAdapter protoNumberDataPointsAdapter;

    public ProtoGaugeAdapter(Gauge protoGauge, boolean updatable) {
        this.protoGauge = protoGauge;
        this.updatable = updatable;
    }


    @Override
    public ProtoNumberDataPointsAdapter getDataPoints() {
        if (protoNumberDataPointsAdapter == null) {
            protoNumberDataPointsAdapter = new ProtoNumberDataPointsAdapter(protoGauge.getDataPointsList(), updatable);
        }
        return protoNumberDataPointsAdapter;
    }

    public Gauge getUpdatedGauge() {
        if (! updatable) {
            return protoGauge;
        } else if (! isUpdated()) {
            return protoGauge;
        } else {
            Gauge.Builder bld = protoGauge.toBuilder();
            bld.addAllDataPoints(protoNumberDataPointsAdapter.getUpdated());
            return bld.build();
        }
    }

    public boolean isUpdated() {
        return protoNumberDataPointsAdapter.isUpdated();
    }


}
