package com.siglet.data.adapter;

import com.siglet.data.trace.*;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;

public class UnmodifiableSpanAdapter implements UnmodifiableSpan {

    private final Span protoSpan;

    private final Resource protoResource;

    private final InstrumentationScope protoInstrumentationScope;

    private UnmodifiableResource unmodifiableResource;

    private UnmodifiableInstrumentationScope unmodifiableInstrumentationScope;

    public UnmodifiableSpanAdapter(Span protoSpan, Resource protoResource, InstrumentationScope protoInstrumentationScope) {
        this.protoSpan = protoSpan;
        this.protoResource = protoResource;
        this.protoInstrumentationScope = protoInstrumentationScope;
    }

    @Override
    public String getTraceId() {
        return TraceId.fromBytes(protoSpan.getTraceId().toByteArray()).toString();
    }

    @Override
    public String getSpanId() {
        return SpanId.fromBytes(protoSpan.getSpanId().toByteArray()).toString();
    }

    @Override
    public String getParentSpanId() {
        return SpanId.fromBytes(protoSpan.getSpanId().toByteArray()).toString();
    }

    @Override
    public String getTraceState() {
        return protoSpan.getTraceState();
    }


    @Override
    public String getName() {
        return protoSpan.getName();
    }

    @Override
    public long getStartUnixNano() {
        return protoSpan.getStartTimeUnixNano();
    }

    @Override
    public long getEndUnixNano() {
        return protoSpan.getEndTimeUnixNano();
    }

    @Override
    public SpanKind getKind() {
        return switch (protoSpan.getKind()) {
            case SPAN_KIND_UNSPECIFIED -> null;
            case SPAN_KIND_INTERNAL -> SpanKind.INTERNAL;
            case SPAN_KIND_SERVER -> SpanKind.SERVER;
            case SPAN_KIND_CLIENT -> SpanKind.CLIENT;
            case SPAN_KIND_PRODUCER -> SpanKind.PRODUCER;
            case SPAN_KIND_CONSUMER -> SpanKind.CONSUMER;
            case UNRECOGNIZED -> null;
        };
    }

    public UnmodifiableResource getResource() {
//        if (unmodifiableResource == null)
        return null;
    }

    public UnmodifiableInstrumentationScope getInstrumentationScope() {
        return null;
    }

    public Resource getProtoResource() {
        return protoResource;

    }

    public InstrumentationScope getProtoInstrumentationScope() {
        return protoInstrumentationScope;
    }

    public Span getProtoSpan() {
        return protoSpan;
    }


}
