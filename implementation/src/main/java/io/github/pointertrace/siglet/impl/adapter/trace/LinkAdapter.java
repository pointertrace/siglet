package io.github.pointertrace.siglet.impl.adapter.trace;

import com.google.protobuf.ByteString;
import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.trace.Link;
import io.github.pointertrace.siglet.impl.adapter.AttributesAdapter;
import io.github.pointertrace.siglet.impl.adapter.ProtoUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class LinkAdapter implements Link {
    private final io.opentelemetry.proto.trace.v1.Span.Link original;
    private io.opentelemetry.proto.trace.v1.Span.Link.Builder builder;
    private final Consumer<io.opentelemetry.proto.trace.v1.Span.Link> onChange;
    private AttributesAdapter attributesAdapter;

    public LinkAdapter(io.opentelemetry.proto.trace.v1.Span.Link link) {
        this(link, null);
    }

    public LinkAdapter(io.opentelemetry.proto.trace.v1.Span.Link link,
                       Consumer<io.opentelemetry.proto.trace.v1.Span.Link> onChange) {
        this.original = Objects.requireNonNull(link, "link");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.trace.v1.Span.Link.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.trace.v1.Span.LinkOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.trace.v1.Span.Link getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public String getTraceIdEx() {
        return ProtoUtil.hex(view().getTraceId().toByteArray());
    }

    @Override
    public long getTraceIdHigh() {
        return ProtoUtil.readLong(view().getTraceId().toByteArray(), 0);
    }

    @Override
    public long getTraceIdLow() {
        return ProtoUtil.readLong(view().getTraceId().toByteArray(), 8);
    }

    @Override
    public LinkAdapter setTraceId(long high, long low) {
        mutate().setTraceId(ByteString.copyFrom(ProtoUtil.toTraceId(high, low)));
        changed();
        return this;
    }

    @Override
    public String getSpanIdEx() {
        return ProtoUtil.hex(view().getSpanId().toByteArray());
    }

    @Override
    public long getSpanId() {
        return ProtoUtil.readLong(view().getSpanId().toByteArray(), 0);
    }

    @Override
    public LinkAdapter setSpanId(long spanId) {
        mutate().setSpanId(ByteString.copyFrom(ProtoUtil.toSpanId(spanId)));
        changed();
        return this;
    }

    @Override
    public String getTraceState() {
        return view().getTraceState();
    }

    @Override
    public LinkAdapter setTraceState(String traceState) {
        mutate().setTraceState(Objects.requireNonNull(traceState, "traceState"));
        changed();
        return this;
    }

    @Override
    public int getFlags() {
        return view().getFlags();
    }

    @Override
    public LinkAdapter setFlags(int flags) {
        mutate().setFlags(flags);
        changed();
        return this;
    }

    @Override
    public int getDroppedAttributesCount() {
        return view().getDroppedAttributesCount();
    }

    @Override
    public LinkAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        mutate().setDroppedAttributesCount(droppedAttributesCount);
        changed();
        return this;
    }

    @Override
    public Attributes getAttributes() {
        if (attributesAdapter == null) {
            attributesAdapter = new AttributesAdapter(view().getAttributesList(), attrs -> {
                io.opentelemetry.proto.trace.v1.Span.Link.Builder b = mutate();
                b.clearAttributes();
                b.addAllAttributes(attrs);
                changed();
            });
        }
        return attributesAdapter;
    }
}
