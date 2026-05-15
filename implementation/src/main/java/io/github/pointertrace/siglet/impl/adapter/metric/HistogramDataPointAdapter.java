package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.metric.Exemplars;
import io.github.pointertrace.siglet.api.signal.metric.HistogramDataPoint;
import io.github.pointertrace.siglet.impl.adapter.AttributesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class HistogramDataPointAdapter implements HistogramDataPoint {
    private final io.opentelemetry.proto.metrics.v1.HistogramDataPoint original;
    private io.opentelemetry.proto.metrics.v1.HistogramDataPoint.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> onChange;
    private AttributesAdapter attributesAdapter;
    private ExemplarsAdapter exemplarsAdapter;

    public HistogramDataPointAdapter(io.opentelemetry.proto.metrics.v1.HistogramDataPoint value) {
        this(value, null);
    }

    public HistogramDataPointAdapter(io.opentelemetry.proto.metrics.v1.HistogramDataPoint value,
                                     Consumer<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> onChange) {
        this.original = Objects.requireNonNull(value, "value");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.HistogramDataPoint.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.HistogramDataPointOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.HistogramDataPoint getUpdated() {
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
    public HistogramDataPointAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        mutate().setStartTimeUnixNano(startTimeUnixNano);
        changed();
        return this;
    }

    @Override
    public long getTimeUnixNano() { return view().getTimeUnixNano(); }

    @Override
    public HistogramDataPointAdapter setTimeUnixNano(long timeUnixNano) {
        mutate().setTimeUnixNano(timeUnixNano);
        changed();
        return this;
    }

    @Override
    public long getCount() { return view().getCount(); }

    @Override
    public HistogramDataPointAdapter setCount(long count) {
        mutate().setCount(count);
        changed();
        return this;
    }

    @Override
    public int getFlags() { return view().getFlags(); }

    @Override
    public HistogramDataPointAdapter setFlags(int flags) {
        mutate().setFlags(flags);
        changed();
        return this;
    }

    @Override
    public double getSum() { return view().getSum(); }

    @Override
    public HistogramDataPointAdapter setSum(double sum) {
        mutate().setSum(sum);
        changed();
        return this;
    }

    @Override
    public List<Long> getBucketCounts() { return new ArrayList<>(view().getBucketCountsList()); }

    @Override
    public HistogramDataPointAdapter addBucketCount(long count) {
        mutate().addBucketCounts(count);
        changed();
        return this;
    }

    @Override
    public List<Double> getExplicitBounds() { return new ArrayList<>(view().getExplicitBoundsList()); }

    @Override
    public HistogramDataPointAdapter addAllBucketCounts(List<Long> count) {
        mutate().addAllBucketCounts(Objects.requireNonNull(count, "count"));
        changed();
        return this;
    }

    @Override
    public HistogramDataPointAdapter clearBucketCounts() {
        mutate().clearBucketCounts();
        changed();
        return this;
    }

    @Override
    public HistogramDataPointAdapter addExplicitBound(Double explicitBound) {
        mutate().addExplicitBounds(Objects.requireNonNull(explicitBound, "explicitBound"));
        changed();
        return this;
    }

    @Override
    public HistogramDataPointAdapter addAllExplicitBounds(List<Double> count) {
        mutate().addAllExplicitBounds(Objects.requireNonNull(count, "count"));
        changed();
        return this;
    }

    @Override
    public HistogramDataPointAdapter clearExplicitBounds() {
        mutate().clearExplicitBounds();
        changed();
        return this;
    }

    @Override
    public Double getMin() { return view().hasMin() ? view().getMin() : null; }

    @Override
    public HistogramDataPointAdapter setMin(Double min) {
        if (min == null) {
            mutate().clearMin();
        } else {
            mutate().setMin(min);
        }
        changed();
        return this;
    }

    @Override
    public Double getMax() { return view().hasMax() ? view().getMax() : null; }

    @Override
    public HistogramDataPointAdapter setMax(Double max) {
        if (max == null) {
            mutate().clearMax();
        } else {
            mutate().setMax(max);
        }
        changed();
        return this;
    }
}
