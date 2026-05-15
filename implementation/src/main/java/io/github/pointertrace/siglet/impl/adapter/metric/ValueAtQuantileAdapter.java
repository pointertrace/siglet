package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.ValueAtQuantile;

import java.util.Objects;
import java.util.function.Consumer;

public final class ValueAtQuantileAdapter implements ValueAtQuantile {
    private final io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile original;
    private io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> onChange;

    public ValueAtQuantileAdapter(io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile value) {
        this(value, null);
    }

    public ValueAtQuantileAdapter(io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile value,
                                  Consumer<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> onChange) {
        this.original = Objects.requireNonNull(value, "value");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantileOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public double getQuantile() { return view().getQuantile(); }

    @Override
    public ValueAtQuantileAdapter setQuantile(double quantile) {
        mutate().setQuantile(quantile);
        changed();
        return this;
    }

    @Override
    public double getValue() { return view().getValue(); }

    @Override
    public ValueAtQuantileAdapter setValue(double value) {
        mutate().setValue(value);
        changed();
        return this;
    }
}
