package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.SummaryDataPoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class SummaryDataPointsAdapter implements SummaryDataPoints {
    private final List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> values;
    private final Consumer<List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint>> onChange;

    public SummaryDataPointsAdapter(List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> values) {
        this(values, null);
    }

    public SummaryDataPointsAdapter(List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> values,
                                    Consumer<List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint>> onChange) {
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
    public SummaryDataPointAdapter get(int i) {
        io.opentelemetry.proto.metrics.v1.SummaryDataPoint value = values.get(i);
        return new SummaryDataPointAdapter(value, updated -> {
            values.set(i, updated);
            changed();
        });
    }

    @Override
    public void remove(int i) {
        values.remove(i);
        changed();
    }

    @Override
    public SummaryDataPointAdapter add() {
        io.opentelemetry.proto.metrics.v1.SummaryDataPoint value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.getDefaultInstance();
        values.add(value);
        changed();
        int index = values.size() - 1;
        return new SummaryDataPointAdapter(value, updated -> {
            values.set(index, updated);
            changed();
        });
    }
}
