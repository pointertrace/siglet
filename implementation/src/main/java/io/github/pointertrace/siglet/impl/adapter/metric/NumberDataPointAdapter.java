package io.github.pointertrace.siglet.impl.adapter.metric;

import io.github.pointertrace.siglet.api.signal.metric.NumberDataPoint;
import io.github.pointertrace.siglet.impl.adapter.AttributesAdapter;

import java.util.Objects;
import java.util.function.Consumer;

public final class NumberDataPointAdapter implements NumberDataPoint {
    private final io.opentelemetry.proto.metrics.v1.NumberDataPoint original;
    private io.opentelemetry.proto.metrics.v1.NumberDataPoint.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.NumberDataPoint> onChange;
    private AttributesAdapter attributesAdapter;
    private ExemplarsAdapter exemplarsAdapter;

    public NumberDataPointAdapter(io.opentelemetry.proto.metrics.v1.NumberDataPoint point) {
        this(point, null);
    }

    public NumberDataPointAdapter(io.opentelemetry.proto.metrics.v1.NumberDataPoint point,
                                  Consumer<io.opentelemetry.proto.metrics.v1.NumberDataPoint> onChange) {
        this.original = Objects.requireNonNull(point, "point");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.NumberDataPoint.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.NumberDataPointOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.NumberDataPoint getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public AttributesAdapter getAttributes() {
        if (attributesAdapter == null) {
            attributesAdapter = new AttributesAdapter(view().getAttributesList(), values -> {
                io.opentelemetry.proto.metrics.v1.NumberDataPoint.Builder b = mutate();
                b.clearAttributes();
                b.addAllAttributes(values);
                changed();
            });
        }
        return attributesAdapter;
    }

    @Override
    public ExemplarsAdapter getExemplars() {
        if (exemplarsAdapter == null) {
            exemplarsAdapter = new ExemplarsAdapter(view().getExemplarsList(), values -> {
                io.opentelemetry.proto.metrics.v1.NumberDataPoint.Builder b = mutate();
                b.clearExemplars();
                b.addAllExemplars(values);
                changed();
            });
        }
        return exemplarsAdapter;
    }

    @Override
    public long getStartTimeUnixNano() {
        return view().getStartTimeUnixNano();
    }

    @Override
    public NumberDataPointAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        mutate().setStartTimeUnixNano(startTimeUnixNano);
        changed();
        return this;
    }

    @Override
    public long getTimeUnixNano() {
        return view().getTimeUnixNano();
    }

    @Override
    public NumberDataPointAdapter setTimeUnixNano(long timeUnixNano) {
        mutate().setTimeUnixNano(timeUnixNano);
        changed();
        return this;
    }

    @Override
    public boolean hasLongValue() {
        return view().getValueCase() == io.opentelemetry.proto.metrics.v1.NumberDataPoint.ValueCase.AS_INT;
    }

    @Override
    public long getAsLong() {
        return view().getAsInt();
    }

    @Override
    public NumberDataPointAdapter setAsLong(long value) {
        mutate().setAsInt(value);
        changed();
        return this;
    }

    @Override
    public boolean hasDoubleValue() {
        return view().getValueCase() == io.opentelemetry.proto.metrics.v1.NumberDataPoint.ValueCase.AS_DOUBLE;
    }

    @Override
    public double getAsDouble() {
        return view().getAsDouble();
    }

    @Override
    public NumberDataPointAdapter setAsDouble(double value) {
        mutate().setAsDouble(value);
        changed();
        return this;
    }

    @Override
    public Object getValue() {
        if (hasLongValue()) {
            return getAsLong();
        }
        if (hasDoubleValue()) {
            return getAsDouble();
        }
        return null;
    }

    @Override
    public int getFlags() {
        return view().getFlags();
    }

    @Override
    public NumberDataPointAdapter setFlags(int flags) {
        mutate().setFlags(flags);
        changed();
        return this;
    }
}
