package com.siglet.data.adapter;

import io.opentelemetry.proto.trace.v1.Span;

public class ProtoEventAdapter {

    private Span.Event protoEvent;

    private Span.Event.Builder protoEventBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoEventAdapter(Span.Event protoEvent) {
        this.protoEvent = protoEvent;
    }

    public long getTimeUnixNano() {
       return  AdapterUtils.read(protoEvent, protoEvent::getTimeUnixNano,
               protoEventBuilder, protoEventBuilder::getTimeUnixNano);

    }

    public void setTimeUnixNano(long timeUnixNano) {
        AdapterUtils.write(protoEventBuilder, this::createEventBuilder,
                protoEventBuilder::setTimeUnixNano, timeUnixNano);
    }

    public String getName() {
        return  AdapterUtils.read(protoEvent, protoEvent::getName,
                protoEventBuilder, protoEventBuilder::getName);
    }

    public void setName(String name) {
        AdapterUtils.write(protoEventBuilder, this::createEventBuilder,
                protoEventBuilder::setName, name);
    }

    public ProtoAttributesAdapter getProtoAttributesAdapter() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoEvent.getAttributesList());
        }
        return protoAttributesAdapter;
    }

    protected void createEventBuilder() {
        protoEventBuilder = Span.Event.newBuilder();
    }
}
