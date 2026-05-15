package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.metric.Buckets;
import io.github.pointertrace.siglet.api.signal.metric.Exemplars;
import io.github.pointertrace.siglet.api.signal.metric.ExponentialHistogramDataPoint;
import io.github.pointertrace.siglet.impl.adapter.AttributesAdapter;

import java.util.Objects;
import java.util.function.Consumer;

public final class ExponentialHistogramDataPointAdapter implements ExponentialHistogramDataPoint {
    private final io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint original;
    private io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> onChange;
    private AttributesAdapter attributesAdapter;
    private ExemplarsAdapter exemplarsAdapter;
    private BucketsAdapter positiveAdapter;
    private BucketsAdapter negativeAdapter;

    public ExponentialHistogramDataPointAdapter(io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint value) {
        this(value, null);
    }

    public ExponentialHistogramDataPointAdapter(io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint value,
                                                Consumer<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> onChange) {
        this.original = Objects.requireNonNull(value, "value");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPointOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public Attributes getAttributes() {
        if (attributesAdapter == null) {
            attributesAdapter = new AttributesAdapter(view().getAttributesList(), attrs -> {
                mutate().clearAttributes().addAllAttributes(attrs);
                changed();
            });
        }
        return attributesAdapter;
    }

    @Override
    public Exemplars getExemplars() {
        if (exemplarsAdapter == null) {
            exemplarsAdapter = new ExemplarsAdapter(view().getExemplarsList(), exemplars -> {
                mutate().clearExemplars().addAllExemplars(exemplars);
                changed();
            });
        }
        return exemplarsAdapter;
    }

    @Override
    public long getStartTimeUnixNano() { return view().getStartTimeUnixNano(); }

    @Override
    public ExponentialHistogramDataPointAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        mutate().setStartTimeUnixNano(startTimeUnixNano);
        changed();
        return this;
    }

    @Override
    public long getTimeUnixNano() { return view().getTimeUnixNano(); }

    @Override
    public ExponentialHistogramDataPointAdapter setTimeUnixNano(long timeUnixNano) {
        mutate().setTimeUnixNano(timeUnixNano);
        changed();
        return this;
    }

    @Override
    public long getCount() { return view().getCount(); }

    @Override
    public ExponentialHistogramDataPointAdapter setCount(long count) {
        mutate().setCount(count);
        changed();
        return this;
    }

    @Override
    public int getFlags() { return view().getFlags(); }

    @Override
    public ExponentialHistogramDataPointAdapter setFlags(int flags) {
        mutate().setFlags(flags);
        changed();
        return this;
    }

    @Override
    public double getSum() { return view().hasSum() ? view().getSum() : 0.0d; }

    @Override
    public ExponentialHistogramDataPointAdapter setSum(double sum) {
        mutate().setSum(sum);
        changed();
        return this;
    }

    @Override
    public int getScale() { return view().getScale(); }

    @Override
    public ExponentialHistogramDataPointAdapter setScale(int scale) {
        mutate().setScale(scale);
        changed();
        return this;
    }

    @Override
    public long getZeroCount() { return view().getZeroCount(); }

    @Override
    public ExponentialHistogramDataPointAdapter setZeroCount(long zeroCount) {
        mutate().setZeroCount(zeroCount);
        changed();
        return this;
    }

    @Override
    public double getMax() { return view().hasMax() ? view().getMax() : 0.0d; }

    @Override
    public ExponentialHistogramDataPointAdapter setMax(double max) {
        mutate().setMax(max);
        changed();
        return this;
    }

    @Override
    public double getMin() { return view().hasMin() ? view().getMin() : 0.0d; }

    @Override
    public ExponentialHistogramDataPointAdapter setMin(double min) {
        mutate().setMin(min);
        changed();
        return this;
    }

    @Override
    public double getZeroThreshold() { return view().getZeroThreshold(); }

    @Override
    public ExponentialHistogramDataPointAdapter setZeroThreshold(double zeroThreshold) {
        mutate().setZeroThreshold(zeroThreshold);
        changed();
        return this;
    }

    @Override
    public Buckets getPositive() {
        if (positiveAdapter == null) {
            positiveAdapter = new BucketsAdapter(view().hasPositive() ? view().getPositive() : io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.getDefaultInstance(), updated -> {
                mutate().setPositive(updated);
                changed();
            });
        }
        return positiveAdapter;
    }

    @Override
    public Buckets getNegative() {
        if (negativeAdapter == null) {
            negativeAdapter = new BucketsAdapter(view().hasNegative() ? view().getNegative() : io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.getDefaultInstance(), updated -> {
                mutate().setNegative(updated);
                changed();
            });
        }
        return negativeAdapter;
    }
}
