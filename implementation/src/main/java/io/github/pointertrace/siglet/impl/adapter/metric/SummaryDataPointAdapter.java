package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.metric.SummaryDataPoint;
import io.github.pointertrace.siglet.api.signal.metric.ValueAtQuantiles;
import io.github.pointertrace.siglet.impl.adapter.AttributesAdapter;

import java.util.Objects;
import java.util.function.Consumer;

public final class SummaryDataPointAdapter implements SummaryDataPoint {
    private final io.opentelemetry.proto.metrics.v1.SummaryDataPoint original;
    private io.opentelemetry.proto.metrics.v1.SummaryDataPoint.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> onChange;
    private AttributesAdapter attributesAdapter;
    private ValueAtQuantilesAdapter quantileValuesAdapter;

    public SummaryDataPointAdapter(io.opentelemetry.proto.metrics.v1.SummaryDataPoint value) {
        this(value, null);
    }

    public SummaryDataPointAdapter(io.opentelemetry.proto.metrics.v1.SummaryDataPoint value,
                                   Consumer<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> onChange) {
        this.original = Objects.requireNonNull(value, "value");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.SummaryDataPoint.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.SummaryDataPointOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.SummaryDataPoint getUpdated() {
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
    public long getStartTimeUnixNano() { return view().getStartTimeUnixNano(); }

    @Override
    public SummaryDataPointAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        mutate().setStartTimeUnixNano(startTimeUnixNano);
        changed();
        return this;
    }

    @Override
    public long getTimeUnixNano() { return view().getTimeUnixNano(); }

    @Override
    public SummaryDataPointAdapter setTimeUnixNano(long timeUnixNano) {
        mutate().setTimeUnixNano(timeUnixNano);
        changed();
        return this;
    }

    @Override
    public long getCount() { return view().getCount(); }

    @Override
    public SummaryDataPointAdapter setCount(long count) {
        mutate().setCount(count);
        changed();
        return this;
    }

    @Override
    public int getFlags() { return view().getFlags(); }

    @Override
    public SummaryDataPointAdapter setFlags(int flags) {
        mutate().setFlags(flags);
        changed();
        return this;
    }

    @Override
    public double getSum() { return view().getSum(); }

    @Override
    public SummaryDataPointAdapter setSum(double sum) {
        mutate().setSum(sum);
        changed();
        return this;
    }

    @Override
    public ValueAtQuantiles getQuantileValues() {
        if (quantileValuesAdapter == null) {
            quantileValuesAdapter = new ValueAtQuantilesAdapter(view().getQuantileValuesList(), quantiles -> {
                mutate().clearQuantileValues().addAllQuantileValues(quantiles);
                changed();
            });
        }
        return quantileValuesAdapter;
    }
}
