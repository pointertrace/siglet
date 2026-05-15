package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.AggregationTemporality;
import io.github.pointertrace.siglet.api.signal.metric.Sum;
import io.github.pointertrace.siglet.impl.adapter.ProtoUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class SumAdapter implements Sum {
    private final io.opentelemetry.proto.metrics.v1.Sum original;
    private io.opentelemetry.proto.metrics.v1.Sum.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.Sum> onChange;
    private NumberDataPointsAdapter dataPointsAdapter;

    public SumAdapter(io.opentelemetry.proto.metrics.v1.Sum sum) {
        this(sum, null);
    }

    public SumAdapter(io.opentelemetry.proto.metrics.v1.Sum sum, Consumer<io.opentelemetry.proto.metrics.v1.Sum> onChange) {
        this.original = Objects.requireNonNull(sum, "sum");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.Sum.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.SumOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.Sum getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public NumberDataPointsAdapter getDataPoints() {
        if (dataPointsAdapter == null) {
            dataPointsAdapter = new NumberDataPointsAdapter(view().getDataPointsList(), values -> {
                io.opentelemetry.proto.metrics.v1.Sum.Builder b = mutate();
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
    public SumAdapter setAggregationTemporality(AggregationTemporality aggregationTemporality) {
        mutate().setAggregationTemporality(ProtoUtil.toProto(Objects.requireNonNull(aggregationTemporality, "aggregationTemporality")));
        changed();
        return this;
    }

    @Override
    public boolean getMonotonic() {
        return view().getIsMonotonic();
    }

    @Override
    public SumAdapter setMonotonic(boolean monotonic) {
        mutate().setIsMonotonic(monotonic);
        changed();
        return this;
    }
}
