package com.siglet.data.adapter;

import com.siglet.data.trace.ModifiableResource;
import com.siglet.data.trace.ModifiableSpan;
import com.siglet.data.trace.SpanKind;
import com.siglet.data.trace.UnmodifiableInstrumentationScope;
import io.opencensus.trace.SpanBuilder;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModifiableSpanAdapter {

    private Span span;

    private Span.Builder changedSpanBuilder;

    private Resource resource;

    private InstrumentationScope instrumentationScope;

    public ModifiableSpanAdapter(Span span, Resource resource, InstrumentationScope instrumentationScope) {
        this.span = span;
        this.resource = resource;
        this.instrumentationScope = instrumentationScope;
    }

    public String getTraceId() {
        return TraceId.fromBytes(span.getTraceId().toByteArray()).toString();
    }

    public String getSpanId() {
        return SpanId.fromBytes(span.getSpanId().toByteArray()).toString();
    }

    public String getParentSpanId() {
        return SpanId.fromBytes(span.getSpanId().toByteArray()).toString();
    }

    public String getTraceState() {
        return span.getTraceState();
    }

    public void setTraceState(String traceState) {
        write(changedSpanBuilder::setTraceState, traceState);
    }

    public String getName() {
        return read(changedSpanBuilder::getName, span::getName);
    }

    public void setName(String name) {
        write(changedSpanBuilder::setName, name);
    }

    public long getStartUnixNano() {
        return read(changedSpanBuilder::getStartTimeUnixNano, span::getStartTimeUnixNano);
    }

    public long getEndUnixNano() {
        return span.getEndTimeUnixNano();
    }

    public SpanKind getKind() {
        return switch (span.getKind()) {
            case SPAN_KIND_UNSPECIFIED -> null;
            case SPAN_KIND_INTERNAL -> SpanKind.INTERNAL;
            case SPAN_KIND_SERVER -> SpanKind.SERVER;
            case SPAN_KIND_CLIENT -> SpanKind.CLIENT;
            case SPAN_KIND_PRODUCER -> SpanKind.PRODUCER;
            case SPAN_KIND_CONSUMER -> SpanKind.CONSUMER;
            case UNRECOGNIZED -> null;
        };
    }

//    public ModifiableResource getProtoResource() {
//        return null;
//    }
//
//    public UnmodifiableInstrumentationScope getProtoInstrumentationScope() {
//        return null;
//    }
//
//    public Resource getProtoResource() {
//        return resource;
//
//    }
//
//    public InstrumentationScope getProtoInstrumentationScope() {
//        return instrumentationScope;
//    }

    public Span getProtoSpan() {
        return span;
    }

    private <T> T read(Supplier<T> spanBuilderGetter, Supplier<T> spanGetter) {
        if (changedSpanBuilder == null) {
            return spanBuilderGetter.get();
        } else {
            return spanGetter.get();
        }

    }

    private <T> void write(Consumer<T> spanBuilderSetter, T value) {
        if (changedSpanBuilder == null) {
            changedSpanBuilder = span.toBuilder();
        }
        spanBuilderSetter.accept(value);

    }


}
