package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality;
import io.github.pointertrace.siglet.api.signal.metric.Histogram;
import io.github.pointertrace.siglet.api.signal.metric.HistogramDataPoints;
import io.github.pointertrace.siglet.impl.adapter.ProtoUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class HistogramAdapter implements Histogram {
    private final io.opentelemetry.proto.metrics.v1.Histogram original;
    private io.opentelemetry.proto.metrics.v1.Histogram.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.Histogram> onChange;
    private HistogramDataPointsAdapter dataPointsAdapter;

    public HistogramAdapter(io.opentelemetry.proto.metrics.v1.Histogram histogram) {
        this(histogram, null);
    }

    public HistogramAdapter(io.opentelemetry.proto.metrics.v1.Histogram histogram, Consumer<io.opentelemetry.proto.metrics.v1.Histogram> onChange) {
        this.original = Objects.requireNonNull(histogram, "histogram");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.Histogram.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.HistogramOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.Histogram getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public HistogramDataPoints getDataPoints() {
        if (dataPointsAdapter == null) {
            dataPointsAdapter = new HistogramDataPointsAdapter(view().getDataPointsList(), values -> {
                io.opentelemetry.proto.metrics.v1.Histogram.Builder b = mutate();
                b.clearDataPoints();
                b.addAllDataPoints(values);
                changed();
            });
        }
        return dataPointsAdapter;
    }

    @Override
    public AggregationTemporality getAggregationTemporality() {
        return ProtoUtil.fromProto(view().getAggregationTemporality());
    }

    @Override
    public HistogramAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        mutate().setAggregationTemporality(ProtoUtil.toProto(Objects.requireNonNull(aggregationTemporality, "aggregationTemporality")));
        changed();
        return this;
    }
}
