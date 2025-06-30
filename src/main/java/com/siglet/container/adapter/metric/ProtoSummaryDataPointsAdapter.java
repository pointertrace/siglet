package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableSummaryDataPoints;
import com.siglet.container.adapter.AdapterList;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

import java.util.List;

public class ProtoSummaryDataPointsAdapter extends AdapterList<SummaryDataPoint,
        SummaryDataPoint.Builder, ProtoSummaryDataPointAdapter>
        implements ModifiableSummaryDataPoints {

    public ProtoSummaryDataPointsAdapter() {
    }

    public ProtoSummaryDataPointsAdapter(List<SummaryDataPoint> protoSummaryDataPoints) {
        super(protoSummaryDataPoints);
    }

    public ProtoSummaryDataPointsAdapter recycle(List<SummaryDataPoint> protoSummaryDataPoints) {

        super.recycle(protoSummaryDataPoints);
        return this;
    }

    @Override
    public ProtoSummaryDataPointAdapter getAt(int i) {
        return super.getAdapter(i);
    }

    @Override
    public void remove(int i) {
        super.remove(i);
    }

    @Override
    protected ProtoSummaryDataPointAdapter createNewAdapter() {
        return new ProtoSummaryDataPointAdapter(SummaryDataPoint.newBuilder());
    }

    @Override
    protected ProtoSummaryDataPointAdapter createAdapter(int i) {
        return new ProtoSummaryDataPointAdapter(getMessage(i));
    }
}
