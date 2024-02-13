package com.siglet.data.adapter;

import com.google.protobuf.Message;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import io.opentelemetry.proto.trace.v1.Span;

public class ProtoLinkAdapter {

    private Span.Link protoLink;

    private Span.Link.Builder protoLinkBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoLinkAdapter(Span.Link protoLink) {
        this.protoLink = protoLink;
    }

    public String getTraceId() {
        return TraceId.fromBytes(protoLink.getTraceId().toByteArray()).toString();
    }

    public String getSpanId() {
        return SpanId.fromBytes(protoLink.getSpanId().toByteArray()).toString();
    }

    public String getTraceState() {
        return AdapterUtils.read(protoLink, protoLink::getTraceState,protoLinkBuilder,protoLinkBuilder::getTraceState);
    }

    public void setTraceState(String traceState) {
        AdapterUtils.write(protoLinkBuilder,this::createProtoLinkBuilder,protoLinkBuilder::setTraceState,traceState);
    }

    public ProtoAttributesAdapter getProtoAttributesAdapter() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoLink.getAttributesList());
        }
        return protoAttributesAdapter;
    }

    protected void createProtoLinkBuilder() {
        protoLinkBuilder = Span.Link.newBuilder();
    }








}
