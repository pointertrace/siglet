package com.siglet.container.adapter.metric;

import com.siglet.api.modifiable.metric.ModifiableExponentialHistogramDataPoints;
import com.siglet.container.adapter.AdapterList;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

import java.util.List;

public class ProtoExponentialHistogramDataPointsAdapter extends AdapterList<ExponentialHistogramDataPoint,
        ExponentialHistogramDataPoint.Builder, ProtoExponentialHistogramDataPointAdapter>
        implements ModifiableExponentialHistogramDataPoints {

    public ProtoExponentialHistogramDataPointsAdapter recycle(
            List<ExponentialHistogramDataPoint> protoExponentialHistogramDataPoints) {
        super.recycle(protoExponentialHistogramDataPoints);
        return this;
    }

    public ProtoExponentialHistogramDataPointsAdapter() {
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
        return new ProtoExponentialHistogramDataPointAdapter(getMessage(i));
    }
}
