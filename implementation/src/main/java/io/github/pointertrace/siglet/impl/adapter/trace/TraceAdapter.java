package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Trace;
import io.github.pointertrace.siglet.impl.adapter.ProtoUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public final class TraceAdapter implements Trace {
    private final long traceIdHigh;
    private final long traceIdLow;
    private final byte[] traceId;
    private final LinkedHashMap<Long, SpanAdapter> spans;

    public TraceAdapter(long traceIdHigh, long traceIdLow, List<SpanAdapter> initialSpans) {
        this.traceIdHigh = traceIdHigh;
        this.traceIdLow = traceIdLow;
        this.traceId = ProtoUtil.toTraceId(traceIdHigh, traceIdLow);
        this.spans = new LinkedHashMap<>();
        for (SpanAdapter span : Objects.requireNonNull(initialSpans, "initialSpans")) {
            Objects.requireNonNull(span, "span");
            this.spans.put(span.getSpanId(), span);
        }
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }
    @Override
    public long getTraceIdHigh() {
        return traceIdHigh;
    }

    @Override
    public long getTraceIdLow() {
        return traceIdLow;
    }

    @Override
    public byte[] getTraceId() {
        return traceId.clone();
    }

    @Override
    public String getTraceIdEx() {
        return ProtoUtil.hex(traceId);
    }

    @Override
    public int getSize() {
        return spans.size();
    }

    @Override
    public Span getAt(int index) {
        return new ArrayList<>(spans.values()).get(index);
    }

    @Override
    public boolean isComplete() {
        return spans.values().stream().allMatch(span -> !span.isRoot() || span.getParentSpanId() == 0L);
    }

    @Override
    public Trace add(Span span) {
        SpanAdapter adapter = Objects.requireNonNull((SpanAdapter) span, "span");
        spans.put(adapter.getSpanId(), adapter);
        return this;
    }

    @Override
    public boolean remove(long spanId) {
        return spans.remove(spanId) != null;
    }

    @Override
    public Span get(long spanId) {
        return spans.get(spanId);
    }

}
