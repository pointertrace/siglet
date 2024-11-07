package com.siglet.data.adapter.metric;

import com.siglet.data.adapter.Adapter;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import com.siglet.data.modifiable.metric.ModifiableExponentialHistogramDataPoint;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

public class ProtoExponentialHistogramDataPointAdapter extends Adapter<ExponentialHistogramDataPoint,
        ExponentialHistogramDataPoint.Builder> implements ModifiableExponentialHistogramDataPoint {

    private ProtoAttributesAdapter protoAttributesAdapter;

    private ProtoExemplarsAdapter protoExemplarsAdapter;

    private ProtoBucketsAdapter protoPositiveBucketAdapter;

    private ProtoBucketsAdapter protoNegativeBucketAdapter;

    public ProtoExponentialHistogramDataPointAdapter(ExponentialHistogramDataPoint protoExponentialHistogramDataPoint,
                                                     boolean updatable) {
        super(protoExponentialHistogramDataPoint, ExponentialHistogramDataPoint::toBuilder,
                ExponentialHistogramDataPoint.Builder::build, updatable);
    }


    public ProtoExponentialHistogramDataPointAdapter(ExponentialHistogramDataPoint.Builder protoExponentialHistogramDataPointBuilder) {
        super(protoExponentialHistogramDataPointBuilder, ExponentialHistogramDataPoint.Builder::build);
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(getMessage().getAttributesList(), isUpdatable());
        }
        return protoAttributesAdapter;
    }

    @Override
    public long getStartTimeUnixNano() {
        return getValue(ExponentialHistogramDataPoint::getStartTimeUnixNano,
                ExponentialHistogramDataPoint.Builder::getStartTimeUnixNano);
    }

    @Override
    public long getTimeUnixNano() {
        return getValue(ExponentialHistogramDataPoint::getTimeUnixNano,
                ExponentialHistogramDataPoint.Builder::getTimeUnixNano);
    }

    @Override
    public long getCount() {
        return getValue(ExponentialHistogramDataPoint::getCount, ExponentialHistogramDataPoint.Builder::getCount);
    }


    @Override
    public int getFlags() {
        return getValue(ExponentialHistogramDataPoint::getFlags, ExponentialHistogramDataPoint.Builder::getFlags);
    }

    @Override
    public double getSum() {
        return getValue(ExponentialHistogramDataPoint::getSum, ExponentialHistogramDataPoint.Builder::getSum);
    }

    @Override
    public int getScale() {
        return getValue(ExponentialHistogramDataPoint::getScale, ExponentialHistogramDataPoint.Builder::getScale);
    }

    @Override
    public long getZeroCount() {
        return getValue(ExponentialHistogramDataPoint::getZeroCount,
                ExponentialHistogramDataPoint.Builder::getZeroCount);
    }

    @Override
    public double getMax() {
        return getValue(ExponentialHistogramDataPoint::getMax, ExponentialHistogramDataPoint.Builder::getMax);
    }

    @Override
    public double getMin() {
        return getValue(ExponentialHistogramDataPoint::getMin, ExponentialHistogramDataPoint.Builder::getMin);
    }

    @Override
    public double getZeoThreshold() {
        return getValue(ExponentialHistogramDataPoint::getZeroThreshold,
                ExponentialHistogramDataPoint.Builder::getZeroThreshold);
    }

    @Override
    public ProtoBucketsAdapter getPositive() {
        if (protoPositiveBucketAdapter == null) {
            protoPositiveBucketAdapter = new ProtoBucketsAdapter(getMessage().getPositive(), isUpdatable());
        }
        return protoPositiveBucketAdapter;
    }

    @Override
    public ProtoBucketsAdapter getNegative() {
        if (protoNegativeBucketAdapter == null) {
            protoNegativeBucketAdapter = new ProtoBucketsAdapter(getMessage().getNegative(), isUpdatable());
        }
        return protoNegativeBucketAdapter;
    }

    @Override
    public ProtoExemplarsAdapter getExemplars() {
        if (protoExemplarsAdapter == null) {
            protoExemplarsAdapter = new ProtoExemplarsAdapter(getMessage().getExemplarsList(), isUpdatable());

        }
        return protoExemplarsAdapter;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setStartTimeUnixNano(long startTimeUnixNano) {
        setValue(ExponentialHistogramDataPoint.Builder::setStartTimeUnixNano, startTimeUnixNano);
        return this;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setTimeUnixNano(long timeUnixNano) {
        setValue(ExponentialHistogramDataPoint.Builder::setTimeUnixNano, timeUnixNano);
        return this;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setCount(long count) {
        setValue(ExponentialHistogramDataPoint.Builder::setCount, count);
        return this;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setFlags(int flags) {
        setValue(ExponentialHistogramDataPoint.Builder::setFlags, flags);
        return this;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setSum(double sum) {
        setValue(ExponentialHistogramDataPoint.Builder::setSum, sum);
        return this;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setScale(int scale) {
        setValue(ExponentialHistogramDataPoint.Builder::setScale, scale);
        return this;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setZeroCount(long zeroCount) {
        setValue(ExponentialHistogramDataPoint.Builder::setZeroCount, zeroCount);
        return this;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setMax(double max) {
        setValue(ExponentialHistogramDataPoint.Builder::setMax, max);
        return this;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setMin(double min) {
        setValue(ExponentialHistogramDataPoint.Builder::setMin, min);
        return this;
    }

    @Override
    public ModifiableExponentialHistogramDataPoint setZeroThreshold(double zeroThreshold) {
        setValue(ExponentialHistogramDataPoint.Builder::setZeroThreshold, zeroThreshold);
        return this;
    }

    @Override
    public boolean isUpdated() {
        return super.isUpdated() ||
                (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) ||
                (protoExemplarsAdapter != null && protoExemplarsAdapter.isUpdated()) ||
                (protoPositiveBucketAdapter != null && protoPositiveBucketAdapter.isUpdated()) ||
                (protoNegativeBucketAdapter != null && protoNegativeBucketAdapter.isUpdated());
    }

    @Override
    protected void enrich(ExponentialHistogramDataPoint.Builder builder) {
        if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
            builder.clearAttributes();
            builder.addAllAttributes(protoAttributesAdapter.getUpdated());
        }
        if (protoExemplarsAdapter != null && protoExemplarsAdapter.isUpdated()) {
            builder.clearExemplars();
            builder.addAllExemplars(protoExemplarsAdapter.getUpdated());
        }
        if (protoPositiveBucketAdapter != null && protoPositiveBucketAdapter.isUpdated()) {
            builder.setPositive(protoPositiveBucketAdapter.getUpdated());
        }
        if (protoNegativeBucketAdapter != null && protoNegativeBucketAdapter.isUpdated()) {
            builder.setNegative(protoNegativeBucketAdapter.getUpdated());
        }
    }
}
