package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.trace.Links;
import io.github.pointertrace.siglet.impl.adapter.ProtoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class LinksAdapter implements Links {
    private final List<io.opentelemetry.proto.trace.v1.Span.Link> links;
    private final Consumer<List<io.opentelemetry.proto.trace.v1.Span.Link>> onChange;

    public LinksAdapter(List<io.opentelemetry.proto.trace.v1.Span.Link> links) {
        this(links, null);
    }

    public LinksAdapter(List<io.opentelemetry.proto.trace.v1.Span.Link> links,
                        Consumer<List<io.opentelemetry.proto.trace.v1.Span.Link>> onChange) {
        this.links = new ArrayList<>(Objects.requireNonNull(links, "links"));
        this.onChange = onChange;
    }

    public List<io.opentelemetry.proto.trace.v1.Span.Link> getUpdated() {
        return new ArrayList<>(links);
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(new ArrayList<>(links));
        }
    }

    @Override
    public int getSize() {
        return links.size();
    }

    @Override
    public boolean has(long traceIdHigh, long traceIdLow, long spanId) {
        return links.stream().anyMatch(link ->
            ProtoUtil.readLong(link.getTraceId().toByteArray(), 0) == traceIdHigh
                && ProtoUtil.readLong(link.getTraceId().toByteArray(), 8) == traceIdLow
                && ProtoUtil.readLong(link.getSpanId().toByteArray(), 0) == spanId);
    }

    @Override
    public LinkAdapter add(long traceIdHigh, long traceIdLow, long spanId, String traceState, Map<String, Object> attributes) {
        io.opentelemetry.proto.trace.v1.Span.Link.Builder b = io.opentelemetry.proto.trace.v1.Span.Link.newBuilder()
            .setTraceId(com.google.protobuf.ByteString.copyFrom(ProtoUtil.toTraceId(traceIdHigh, traceIdLow)))
            .setSpanId(com.google.protobuf.ByteString.copyFrom(ProtoUtil.toSpanId(spanId)))
            .setTraceState(Objects.requireNonNull(traceState, "traceState"))
            .addAllAttributes(ProtoUtil.fromMap(Objects.requireNonNull(attributes, "attributes")));
        io.opentelemetry.proto.trace.v1.Span.Link value = b.build();
        links.add(value);
        changed();
        return new LinkAdapter(value, updated -> {
            links.set(links.size() - 1, updated);
            changed();
        });
    }

    @Override
    public LinkAdapter get(long traceIdHigh, long traceIdLow, int spanId) {
        for (int i = 0; i < links.size(); i++) {
            io.opentelemetry.proto.trace.v1.Span.Link link = links.get(i);
            if (ProtoUtil.readLong(link.getTraceId().toByteArray(), 0) == traceIdHigh
                && ProtoUtil.readLong(link.getTraceId().toByteArray(), 8) == traceIdLow
                && ProtoUtil.readLong(link.getSpanId().toByteArray(), 0) == spanId) {
                final int index = i;
                return new LinkAdapter(link, updated -> {
                    links.set(index, updated);
                    changed();
                });
            }
        }
        return null;
    }

    @Override
    public boolean remove(long traceIdHigh, long traceIdLow, int spanId) {
        for (int i = 0; i < links.size(); i++) {
            io.opentelemetry.proto.trace.v1.Span.Link link = links.get(i);
            if (ProtoUtil.readLong(link.getTraceId().toByteArray(), 0) == traceIdHigh
                && ProtoUtil.readLong(link.getTraceId().toByteArray(), 8) == traceIdLow
                && ProtoUtil.readLong(link.getSpanId().toByteArray(), 0) == spanId) {
                links.remove(i);
                changed();
                return true;
            }
        }
        return false;
    }
}
