package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.AdapterList;
import com.siglet.data.modifiable.metric.ModifiableSummaryDataPoints;
import io.opentelemetry.proto.metrics.v1.SummaryDataPoint;

import java.util.List;

public class ProtoSummaryDataPointsAdapter extends AdapterList<SummaryDataPoint,
        SummaryDataPoint.Builder, ProtoSummaryDataPointAdapter>
        implements ModifiableSummaryDataPoints {

    public ProtoSummaryDataPointsAdapter(
            List<SummaryDataPoint> protoSummaryDataPoints, boolean updatable) {
        super(protoSummaryDataPoints, updatable);
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
    public ProtoSummaryDataPointAdapter add() {
        return super.add();
    }

    @Override
    protected ProtoSummaryDataPointAdapter createNewAdapter() {
        return new ProtoSummaryDataPointAdapter(SummaryDataPoint.newBuilder());
    }

    @Override
    protected ProtoSummaryDataPointAdapter createAdapter(int i) {
        return new ProtoSummaryDataPointAdapter(getMessage(i), isUpdatable());
    }
}
