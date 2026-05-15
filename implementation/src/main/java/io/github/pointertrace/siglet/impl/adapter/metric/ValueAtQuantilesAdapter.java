package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.ValueAtQuantiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ValueAtQuantilesAdapter implements ValueAtQuantiles {
    private final List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> values;
    private final Consumer<List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile>> onChange;

    public ValueAtQuantilesAdapter(List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> values) {
        this(values, null);
    }

    public ValueAtQuantilesAdapter(List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> values,
                                   Consumer<List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile>> onChange) {
        this.values = new ArrayList<>(Objects.requireNonNull(values, "values"));
        this.onChange = onChange;
    }

    public List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> getUpdated() {
        return new ArrayList<>(values);
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(new ArrayList<>(values));
        }
    }

    @Override
    public int getSize() { return values.size(); }

    @Override
    public ValueAtQuantileAdapter get(int i) {
        io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile value = values.get(i);
        return new ValueAtQuantileAdapter(value, updated -> {
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
