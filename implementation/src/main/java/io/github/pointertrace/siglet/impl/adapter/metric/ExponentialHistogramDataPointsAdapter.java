package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.ExponentialHistogramDataPoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ExponentialHistogramDataPointsAdapter implements ExponentialHistogramDataPoints {
    private final List<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> values;
    private final Consumer<List<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint>> onChange;

    public ExponentialHistogramDataPointsAdapter(List<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> values) {
        this(values, null);
    }

    public ExponentialHistogramDataPointsAdapter(List<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> values,
                                                 Consumer<List<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint>> onChange) {
        this.values = new ArrayList<>(Objects.requireNonNull(values, "values"));
        this.onChange = onChange;
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(new ArrayList<>(values));
        }
    }

    @Override
    public int getSize() { return values.size(); }

    @Override
    public ExponentialHistogramDataPointAdapter get(int i) {
        io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint value = values.get(i);
        return new ExponentialHistogramDataPointAdapter(value, updated -> {
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
