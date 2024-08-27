package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.AdapterList;
import com.siglet.data.modifiable.metric.ModifiableValueAtQuantiles;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

import java.util.List;

public class ProtoValueAtQuantilesAdapter extends AdapterList<SummaryDataPoint.ValueAtQuantile,
        SummaryDataPoint.ValueAtQuantile.Builder, ProtoValueAtQuantileAdapter>
        implements ModifiableValueAtQuantiles {

    public ProtoValueAtQuantilesAdapter(
            List<SummaryDataPoint.ValueAtQuantile> protoValueAtQuantiles, boolean updatable) {
        super(protoValueAtQuantiles, updatable);
    }

    @Override
    public ProtoValueAtQuantileAdapter getAt(int i) {
        return super.getAdapter(i);
    }

    @Override
    public void remove(int i) {
        super.remove(i);
    }

    @Override
    public ProtoValueAtQuantileAdapter add() {
        return super.add();
    }

    @Override
    protected ProtoValueAtQuantileAdapter createNewAdapter() {
        return new ProtoValueAtQuantileAdapter(SummaryDataPoint.ValueAtQuantile.newBuilder());
    }

    @Override
    protected ProtoValueAtQuantileAdapter createAdapter(int i) {
        return new ProtoValueAtQuantileAdapter(getMessage(i), isUpdatable());
    }
}
