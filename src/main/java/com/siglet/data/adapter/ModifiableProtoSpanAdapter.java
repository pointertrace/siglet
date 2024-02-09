package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import com.siglet.data.trace.SpanKind;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import io.opentelemetry.proto.trace.v1.Span;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModifiableProtoSpanAdapter {

    private final Span span;

    private Span.Builder spanBuilder;

    public ModifiableProtoSpanAdapter(Span span) {
        this.span = span;
    }

    public byte[] getTraceId() {
        return read(span::getTraceId, spanBuilder::getTraceId).toByteArray();
    }

    public String getTraceIdAsString() {
        return TraceId.fromBytes(read(span::getTraceId, spanBuilder::getTraceId).toByteArray()).toString();
    }

    public void setTraceId(long high, long low) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.putLong(high);
        buffer.putLong(low);
        write(spanBuilder::setTraceId, ByteString.copyFrom(buffer.array()));
    }

    public byte[] getSpanId() {
        return read(span::getSpanId, spanBuilder::getSpanId).toByteArray();
    }

    public String getSpanIdAsString() {
        return SpanId.fromBytes(read(span::getSpanId, spanBuilder::getSpanId).toByteArray()).toString();
    }

    public void setSpanId(long spanId) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.putLong(spanId);
        write(spanBuilder::setSpanId, ByteString.copyFrom(buffer.array()));
    }

    public void setSpanId(byte[] spanId) {
        write(spanBuilder::setTraceId, ByteString.copyFrom(spanId));
    }

    public byte[] getParentSpanId() {
        return read(span::getParentSpanId, spanBuilder::getParentSpanId).toByteArray();
    }

    public String getParentSpanIdAsString() {
        return SpanId.fromBytes(read(span::getParentSpanId, spanBuilder::getParentSpanId).toByteArray()).toString();
    }

    public void setParentSpanId(long spanId) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.putLong(spanId);
        write(spanBuilder::setTraceId, ByteString.copyFrom(buffer.array()));
    }

    public void setParentSpanId(byte[] spanId) {
        write(spanBuilder::setParentSpanId, ByteString.copyFrom(spanId));
    }

    public String getName() {
        return read(span::getName, spanBuilder::getName);
    }

    public void setName(String name) {
        write(spanBuilder::setName, name);
    }

    public long getStartTimeUnixNano() {
        return read(span::getStartTimeUnixNano, spanBuilder::getStartTimeUnixNano);
    }

    public void setStartTimeUnixNano(long startTimeUnixNano) {
        write(spanBuilder::setStartTimeUnixNano, startTimeUnixNano);
    }

    public long getEndTimeUnixNano() {
        return read(span::getEndTimeUnixNano, spanBuilder::getEndTimeUnixNano);
    }

    public void setEndTimeUnixNano(long endTimeUnixNano) {
        write(spanBuilder::setEndTimeUnixNano, endTimeUnixNano);
    }

    public String getTraceState() {
        return read(span::getTraceState, spanBuilder::getTraceState);
    }

    public void setTraceState(String traceState) {
        write(spanBuilder::setTraceState, traceState);
    }

    public SpanKind getKind() {
        return switch(read(span::getKind, spanBuilder::getKind)) {
            case SPAN_KIND_UNSPECIFIED -> SpanKind.OTHER;
            case SPAN_KIND_INTERNAL -> SpanKind.INTERNAL;
            case SPAN_KIND_SERVER -> SpanKind.SERVER;
            case SPAN_KIND_CLIENT -> SpanKind.CLIENT;
            case SPAN_KIND_PRODUCER -> SpanKind.PRODUCER;
            case SPAN_KIND_CONSUMER -> SpanKind.CONSUMER;
            case UNRECOGNIZED -> SpanKind.OTHER;
        };
    }

    public void setKind(SpanKind kind) {
        write(spanBuilder::setKind,switch (kind) {
            case INTERNAL -> Span.SpanKind.SPAN_KIND_INTERNAL;
            case SERVER -> Span.SpanKind.SPAN_KIND_SERVER;
            case CLIENT -> Span.SpanKind.SPAN_KIND_CLIENT;
            case PRODUCER -> Span.SpanKind.SPAN_KIND_PRODUCER;
            case CONSUMER -> Span.SpanKind.SPAN_KIND_CONSUMER;
            case OTHER -> Span.SpanKind.SPAN_KIND_UNSPECIFIED;
        });
    }


    private <T> T read(Supplier<T> spanReader, Supplier<T> spanBuilderReader) {
        if (spanBuilder == null) {
            return spanReader.get();
        } else {
            return spanBuilderReader.get();
        }
    }

    private <T> void write(Consumer<T> spanBuilderWriter, T value) {
        if (spanBuilder == null) {
            spanBuilder = span.toBuilder();
        }
        spanBuilderWriter.accept(value);
    }


}
