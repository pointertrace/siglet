package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.NumberDataPoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class NumberDataPointsAdapter implements NumberDataPoints {
    private final List<io.opentelemetry.proto.metrics.v1.NumberDataPoint> points;
    private final Consumer<List<io.opentelemetry.proto.metrics.v1.NumberDataPoint>> onChange;

    public NumberDataPointsAdapter(List<io.opentelemetry.proto.metrics.v1.NumberDataPoint> points) {
        this(points, null);
    }

    public NumberDataPointsAdapter(List<io.opentelemetry.proto.metrics.v1.NumberDataPoint> points,
                                   Consumer<List<io.opentelemetry.proto.metrics.v1.NumberDataPoint>> onChange) {
        this.points = new ArrayList<>(Objects.requireNonNull(points, "points"));
        this.onChange = onChange;
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(new ArrayList<>(points));
        }
    }

    @Override
    public int getSize() {
        return points.size();
    }

    @Override
    public NumberDataPointAdapter get(int i) {
        io.opentelemetry.proto.metrics.v1.NumberDataPoint p = points.get(i);
        return new NumberDataPointAdapter(p, updated -> {
            points.set(i, updated);
            changed();
        });
    }

    @Override
    public void remove(int i) {
        points.remove(i);
        changed();
    }

    @Override
    public NumberDataPointAdapter add() {
        io.opentelemetry.proto.metrics.v1.NumberDataPoint value = io.opentelemetry.proto.metrics.v1.NumberDataPoint.getDefaultInstance();
        points.add(value);
        changed();
        int idx = points.size() - 1;
        return new NumberDataPointAdapter(value, updated -> {
            points.set(idx, updated);
            changed();
        });
    }
}
