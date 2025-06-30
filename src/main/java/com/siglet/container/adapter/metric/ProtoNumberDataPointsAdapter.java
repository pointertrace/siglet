package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableNumberDataPoints;
import com.siglet.container.adapter.AdapterList;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;

import java.util.List;

public class ProtoNumberDataPointsAdapter extends AdapterList<NumberDataPoint, NumberDataPoint.Builder,
    ProtoNumberDataPointAdapter> implements ModifiableNumberDataPoints {


    public ProtoNumberDataPointsAdapter() {
    }

    public ProtoNumberDataPointsAdapter recycle(List<NumberDataPoint> protoNumberDataPoints) {
        super.recycle(protoNumberDataPoints);
        return this;
    }

    public ProtoNumberDataPointsAdapter(List<NumberDataPoint> protoNumberDataPoints) {
        super(protoNumberDataPoints);
    }

    @Override
    public ProtoNumberDataPointAdapter getAt(int i) {
        return super.getAdapter(i);
    }

    @Override
    public void remove(int i) {
        super.remove(i);
    }

    @Override
    protected ProtoNumberDataPointAdapter createNewAdapter() {
        return new ProtoNumberDataPointAdapter();
    }

    @Override
    protected ProtoNumberDataPointAdapter createAdapter(int i) {
        return new ProtoNumberDataPointAdapter(getMessage(i));
    }
}
