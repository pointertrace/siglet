package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.Gauge;

import java.util.Objects;
import java.util.function.Consumer;

public final class GaugeAdapter implements Gauge {
    private final io.opentelemetry.proto.metrics.v1.Gauge original;
    private io.opentelemetry.proto.metrics.v1.Gauge.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.Gauge> onChange;
    private NumberDataPointsAdapter dataPointsAdapter;

    public GaugeAdapter(io.opentelemetry.proto.metrics.v1.Gauge gauge) {
        this(gauge, null);
    }

    public GaugeAdapter(io.opentelemetry.proto.metrics.v1.Gauge gauge, Consumer<io.opentelemetry.proto.metrics.v1.Gauge> onChange) {
        this.original = Objects.requireNonNull(gauge, "gauge");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.Gauge.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.GaugeOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.Gauge getUpdated() {
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
                io.opentelemetry.proto.metrics.v1.Gauge.Builder b = mutate();
                b.clearDataPoints();
                b.addAllDataPoints(values);
                changed();
            });
        }
        return dataPointsAdapter;
    }
}
