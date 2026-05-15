package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.Summary;
import io.github.pointertrace.siglet.api.signal.metric.SummaryDataPoints;

import java.util.Objects;
import java.util.function.Consumer;

public final class SummaryAdapter implements Summary {
    private final io.opentelemetry.proto.metrics.v1.Summary original;
    private io.opentelemetry.proto.metrics.v1.Summary.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.Summary> onChange;
    private SummaryDataPointsAdapter dataPointsAdapter;

    public SummaryAdapter(io.opentelemetry.proto.metrics.v1.Summary summary) {
        this(summary, null);
    }

    public SummaryAdapter(io.opentelemetry.proto.metrics.v1.Summary summary, Consumer<io.opentelemetry.proto.metrics.v1.Summary> onChange) {
        this.original = Objects.requireNonNull(summary, "summary");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.Summary.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.SummaryOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.Summary getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public SummaryDataPoints getDataPoints() {
        if (dataPointsAdapter == null) {
            dataPointsAdapter = new SummaryDataPointsAdapter(view().getDataPointsList(), values -> {
                io.opentelemetry.proto.metrics.v1.Summary.Builder b = mutate();
                b.clearDataPoints();
                b.addAllDataPoints(values);
                changed();
            });
        }
        return dataPointsAdapter;
    }
}
