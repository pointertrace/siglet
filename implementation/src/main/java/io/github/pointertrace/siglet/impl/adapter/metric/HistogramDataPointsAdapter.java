package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.HistogramDataPoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class HistogramDataPointsAdapter implements HistogramDataPoints {
    private final List<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> values;
    private final Consumer<List<io.opentelemetry.proto.metrics.v1.HistogramDataPoint>> onChange;

    public HistogramDataPointsAdapter(List<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> values) {
        this(values, null);
    }

    public HistogramDataPointsAdapter(List<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> values,
                                      Consumer<List<io.opentelemetry.proto.metrics.v1.HistogramDataPoint>> onChange) {
        this.values = new ArrayList<>(Objects.requireNonNull(values, "values"));
        this.onChange = onChange;
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(new ArrayList<>(values));
        }
    }

    @Override
    public int getSize() {
        return values.size();
    }

    @Override
    public HistogramDataPointAdapter get(int i) {
        io.opentelemetry.proto.metrics.v1.HistogramDataPoint value = values.get(i);
        return new HistogramDataPointAdapter(value, updated -> {
            values.set(i, updated);
            changed();
        });
    }

    @Override
    public void remove(int i) {
        values.remove(i);
        changed();
    }
}
