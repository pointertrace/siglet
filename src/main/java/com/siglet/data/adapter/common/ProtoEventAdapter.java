package com.siglet.data.adapter.common;

import com.siglet.data.adapter.Adapter;
import com.siglet.data.modifiable.ModifiableEvent;
import io.opentelemetry.proto.trace.v1.Span;

public class ProtoEventAdapter extends Adapter<Span.Event, Span.Event.Builder> implements ModifiableEvent {

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoEventAdapter(Span.Event protoEvent, boolean updatable) {
        super(protoEvent, Span.Event::toBuilder, Span.Event.Builder::build, updatable);
    }

    public ProtoEventAdapter(Span.Event.Builder protoEventBuilder) {
        super(protoEventBuilder, Span.Event.Builder::build);
    }
    public long getTimeUnixNano() {
        return getValue(Span.Event::getTimeUnixNano, Span.Event.Builder::getTimeUnixNano);
    }

    public ProtoEventAdapter setTimeUnixNano(long timeUnixNano) {
        setValue(Span.Event.Builder::setTimeUnixNano, timeUnixNano);
        return this;
    }

    public String getName() {
        return getValue(Span.Event::getName, Span.Event.Builder::getName);
    }

    public ProtoEventAdapter setName(String name) {
        setValue(Span.Event.Builder::setName, name);
        return this;
    }

    public int getDroppedAttributesCount() {
        return getValue(Span.Event::getDroppedAttributesCount, Span.Event.Builder::getDroppedAttributesCount);
    }

    public ProtoEventAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        setValue(Span.Event.Builder::setDroppedAttributesCount, droppedAttributesCount);
        return this;
    }


    @Override
    public boolean isUpdated() {
        return super.isUpdated() || (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated());
    }

    @Override
    protected void enrich(Span.Event.Builder builder) {
        if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
            builder.clearAttributes();
            builder.addAllAttributes(protoAttributesAdapter.getUpdated());
        }
    }

    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(
                    getValue(Span.Event::getAttributesList, Span.Event.Builder::getAttributesList), isUpdatable());

        }
        return protoAttributesAdapter;
    }

}
