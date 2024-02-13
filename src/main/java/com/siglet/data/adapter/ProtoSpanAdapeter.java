package com.siglet.data.adapter;

import com.siglet.data.trace.SpanKind;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;

public class ProtoSpanAdapeter {

    private Span protoSpan;

    private Span.Builder protoSpanBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    private ProtoResourceAdapter protoResourceAdapter;

    private ProtoLinksAdapter protoLinksAdapter;

    private Resource resource;

    private InstrumentationScope instrumentationScope;
    public ProtoSpanAdapeter(Span protoSpan, Resource resource, InstrumentationScope instrumentationScope) {
        this.protoSpan = protoSpan;
        this.resource = resource;
        this.instrumentationScope = instrumentationScope;
    }

    public String getTraceId() {
        return TraceId.fromBytes(protoSpan.getTraceId().toByteArray()).toString();
    }

    public String getSpanId() {
        return SpanId.fromBytes(protoSpan.getSpanId().toByteArray()).toString();
    }

    public String getParentSpanId() {
        return SpanId.fromBytes(protoSpan.getSpanId().toByteArray()).toString();
    }

    public String getTraceState() {
        return protoSpan.getTraceState();
    }

    public void setTraceState(String traceState) {
       AdapterUtils.write(protoSpanBuilder, this::createBuilder, protoSpanBuilder::setTraceState, traceState);
    }

    public String getName() {
        return AdapterUtils.read(protoSpanBuilder, protoSpanBuilder::getName, protoSpan, protoSpan::getName);
    }

    public void setName(String name) {
       AdapterUtils.write(protoSpanBuilder, this::createBuilder, protoSpanBuilder::setName, name);
    }

    public long getStartUnixNano() {
        return AdapterUtils.read(protoSpanBuilder, protoSpanBuilder::getStartTimeUnixNano, protoSpan, protoSpan::getStartTimeUnixNano);
    }

    public long getEndUnixNano() {
        return AdapterUtils.read(protoSpanBuilder, protoSpanBuilder::getEndTimeUnixNano, protoSpan, protoSpan::getEndTimeUnixNano);
    }

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

    public ProtoAttributesAdapter getAttributesAdapter() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoSpan.getAttributesList());
        }
        return protoAttributesAdapter;
    }

    public ProtoResourceAdapter getProtoResourceAdapter() {
        if (protoResourceAdapter == null) {
            protoResourceAdapter = new ProtoResourceAdapter(resource);
        }
        return protoResourceAdapter;
    }

    public ProtoLinksAdapter getProtoLinksAdapter() {
        if (protoLinksAdapter == null)  {
            protoLinksAdapter = new ProtoLinksAdapter(protoSpan.getLinksList());
        }
        return protoLinksAdapter;
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
        return protoSpan;
    }

    protected void createBuilder() {
        protoSpanBuilder = Span.newBuilder();
    }



}
