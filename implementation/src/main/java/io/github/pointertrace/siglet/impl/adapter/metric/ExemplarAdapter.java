package io.github.pointertrace.siglet.impl.adapter.metric;

import com.google.protobuf.ByteString;
import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.metric.Exemplar;
import io.github.pointertrace.siglet.impl.adapter.AttributesAdapter;
import io.github.pointertrace.siglet.impl.adapter.ProtoUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class ExemplarAdapter implements Exemplar {
    private final io.opentelemetry.proto.metrics.v1.Exemplar original;
    private io.opentelemetry.proto.metrics.v1.Exemplar.Builder builder;
    private final Consumer<io.opentelemetry.proto.metrics.v1.Exemplar> onChange;
    private AttributesAdapter attributesAdapter;

    public ExemplarAdapter(io.opentelemetry.proto.metrics.v1.Exemplar exemplar) {
        this(exemplar, null);
    }

    public ExemplarAdapter(io.opentelemetry.proto.metrics.v1.Exemplar exemplar, Consumer<io.opentelemetry.proto.metrics.v1.Exemplar> onChange) {
        this.original = Objects.requireNonNull(exemplar, "exemplar");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.metrics.v1.Exemplar.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.metrics.v1.ExemplarOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.metrics.v1.Exemplar getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public Attributes getAttributes() {
        if (attributesAdapter == null) {
            attributesAdapter = new AttributesAdapter(view().getFilteredAttributesList(), values -> {
                io.opentelemetry.proto.metrics.v1.Exemplar.Builder b = mutate();
                b.clearFilteredAttributes();
                b.addAllFilteredAttributes(values);
                changed();
            });
        }
        return attributesAdapter;
    }

    @Override
    public long getTimeUnixNanos() {
        return view().getTimeUnixNano();
    }

    @Override
    public ExemplarAdapter setTimeUnixNanos(long timeUnixNanos) {
        mutate().setTimeUnixNano(timeUnixNanos);
        changed();
        return this;
    }

    @Override
    public long getAsLong() {
        return view().getAsInt();
    }

    @Override
    public ExemplarAdapter setAsLong(long value) {
        mutate().setAsInt(value);
        changed();
        return this;
    }

    @Override
    public double getAsDouble() {
        return view().getAsDouble();
    }

    @Override
    public ExemplarAdapter setAsDouble(double value) {
        mutate().setAsDouble(value);
        changed();
        return this;
    }

    @Override
    public long getSpanId() {
        return ProtoUtil.readLong(view().getSpanId().toByteArray(), 0);
    }

    @Override
    public ExemplarAdapter setSpanId(long spanId) {
        mutate().setSpanId(ByteString.copyFrom(ProtoUtil.toSpanId(spanId)));
        changed();
        return this;
    }

    @Override
    public long getTraceIdLow() {
        return ProtoUtil.readLong(view().getTraceId().toByteArray(), 8);
    }

    @Override
    public long getTraceIdHigh() {
        return ProtoUtil.readLong(view().getTraceId().toByteArray(), 0);
    }

    @Override
    public byte[] getTraceId() {
        return view().getTraceId().toByteArray();
    }

    @Override
    public ExemplarAdapter setTraceId(long traceIdHigh, long traceIdLow) {
        mutate().setTraceId(ByteString.copyFrom(ProtoUtil.toTraceId(traceIdHigh, traceIdLow)));
        changed();
        return this;
    }

    @Override
    public ExemplarAdapter setTraceId(byte[] traceId) {
        mutate().setTraceId(ByteString.copyFrom(Objects.requireNonNull(traceId, "traceId")));
        changed();
        return this;
    }
}
