package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.AdapterList;
import com.siglet.data.modifiable.metric.ModifiableExponentialHistogramDataPoints;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

import java.util.List;

public class ProtoExponentialHistogramDataPointsAdapter extends AdapterList<ExponentialHistogramDataPoint,
        ExponentialHistogramDataPoint.Builder, ProtoExponentialHistogramDataPointAdapter>
        implements ModifiableExponentialHistogramDataPoints {

    public ProtoExponentialHistogramDataPointsAdapter(
            List<ExponentialHistogramDataPoint> protoExponentialHistogramDataPoints, boolean updatable) {
        super(protoExponentialHistogramDataPoints, updatable);
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter getAt(int i) {
        return super.getAdapter(i);
    }

    @Override
    public void remove(int i) {
        super.remove(i);
    }

    @Override
    public ProtoExponentialHistogramDataPointAdapter add() {
        return super.add();
    }

    @Override
    protected ProtoExponentialHistogramDataPointAdapter createNewAdapter() {
        return new ProtoExponentialHistogramDataPointAdapter(ExponentialHistogramDataPoint.newBuilder());
    }

    @Override
    protected ProtoExponentialHistogramDataPointAdapter createAdapter(int i) {
        return new ProtoExponentialHistogramDataPointAdapter(getMessage(i), isUpdatable());
    }
}
