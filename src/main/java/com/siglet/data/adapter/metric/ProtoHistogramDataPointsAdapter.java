package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.AdapterList;
import com.siglet.data.modifiable.metric.ModifiableHistogramDataPoints;
import com.siglet.data.modifiable.metric.ModifiableNumberDataPoints;
import io.opentelemetry.proto.metrics.v1.HistogramDataPoint;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

import java.util.List;

public class ProtoHistogramDataPointsAdapter extends AdapterList<HistogramDataPoint, HistogramDataPoint.Builder,
        ProtoHistogramDataPointAdapter> implements ModifiableHistogramDataPoints {

    public ProtoHistogramDataPointsAdapter(List<HistogramDataPoint> protoHistogramDataPoints, boolean updatable) {
        super(protoHistogramDataPoints, updatable);
    }

    @Override
    public ProtoHistogramDataPointAdapter getAt(int i) {
        return super.getAdapter(i);
    }

    @Override
    public void remove(int i) {
        super.remove(i);
    }

    @Override
    public ProtoHistogramDataPointAdapter add() {
        return super.add();
    }

    @Override
    protected ProtoHistogramDataPointAdapter createNewAdapter() {
        return new ProtoHistogramDataPointAdapter(HistogramDataPoint.newBuilder());
    }

    @Override
    protected ProtoHistogramDataPointAdapter createAdapter(int i) {
        return new ProtoHistogramDataPointAdapter(getMessage(i), isUpdatable());
    }
}
