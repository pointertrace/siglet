package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableValueAtQuantiles;
import com.siglet.container.adapter.AdapterList;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

import java.util.List;

public class ProtoValueAtQuantilesAdapter extends AdapterList<SummaryDataPoint.ValueAtQuantile,
        SummaryDataPoint.ValueAtQuantile.Builder, ProtoValueAtQuantileAdapter>
        implements ModifiableValueAtQuantiles {

    public ProtoValueAtQuantilesAdapter(
            List<SummaryDataPoint.ValueAtQuantile> protoValueAtQuantiles) {
        super(protoValueAtQuantiles);
    }

    public ProtoValueAtQuantilesAdapter() {
    }

    public ProtoValueAtQuantilesAdapter recycle(List<SummaryDataPoint.ValueAtQuantile> protoValueAtQuantiles) {
        super.recycle(protoValueAtQuantiles);
        return this;
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
    protected ProtoValueAtQuantileAdapter createNewAdapter() {
        return new ProtoValueAtQuantileAdapter(SummaryDataPoint.ValueAtQuantile.newBuilder());
    }

    @Override
    protected ProtoValueAtQuantileAdapter createAdapter(int i) {
        return new ProtoValueAtQuantileAdapter(getMessage(i));
    }
}
