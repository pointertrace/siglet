package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality;
import io.github.pointertrace.siglet.api.signal.metric.ExponentialHistogram;
import io.github.pointertrace.siglet.api.signal.metric.ExponentialHistogramDataPoints;
import io.github.pointertrace.siglet.impl.adapter.ProtoUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class ExponentialHistogramAdapter implements ExponentialHistogram {
    private final io.opentelemetry.proto.metrics.v1.ExponentialHistogram original;
    private io.opentelemetry.proto.metrics.v1.ExponentialHistogram.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.ExponentialHistogram> onChange;
    private ExponentialHistogramDataPointsAdapter dataPointsAdapter;

    public ExponentialHistogramAdapter(io.opentelemetry.proto.metrics.v1.ExponentialHistogram value) {
        this(value, null);
    }

    public ExponentialHistogramAdapter(io.opentelemetry.proto.metrics.v1.ExponentialHistogram value,
                                       Consumer<io.opentelemetry.proto.metrics.v1.ExponentialHistogram> onChange) {
        this.original = Objects.requireNonNull(value, "value");
        this.onChange = onChange;
    }


    private io.opentelemetry.proto.metrics.v1.ExponentialHistogram.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.ExponentialHistogramOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.ExponentialHistogram getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public AggregationTemporality getAggregationTemporality() {
        return ProtoUtil.fromProto(view().getAggregationTemporality());
    }

    @Override
    public ExponentialHistogramDataPoints getDataPoints() {
        if (dataPointsAdapter == null) {
            dataPointsAdapter = new ExponentialHistogramDataPointsAdapter(view().getDataPointsList(), values -> {
                io.opentelemetry.proto.metrics.v1.ExponentialHistogram.Builder b = mutate();
                b.clearDataPoints();
                b.addAllDataPoints(values);
                changed();
            });
        }
        return dataPointsAdapter;
    }

    @Override
    public ExponentialHistogramAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        mutate().setAggregationTemporality(ProtoUtil.toProto(Objects.requireNonNull(aggregationTemporality, "aggregationTemporality")));
        changed();
        return this;
    }
}
