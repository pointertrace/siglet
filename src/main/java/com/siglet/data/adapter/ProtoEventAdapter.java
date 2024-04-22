package com.siglet.data.adapter;

import com.siglet.data.modifiable.ModifiableEvent;
import io.opentelemetry.proto.trace.v1.Span;

public class ProtoEventAdapter implements ModifiableEvent {

    private final Span.Event protoEvent;

    private final boolean updatable;

    private boolean updated;

    private Span.Event.Builder protoEventBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoEventAdapter(Span.Event protoEvent, boolean updatable) {
        this.protoEvent = protoEvent;
        this.updatable = updatable;
    }

    public long getTimeUnixNano() {
        return protoEventBuilder == null ? protoEvent.getTimeUnixNano() : protoEventBuilder.getTimeUnixNano();
    }

    public void setTimeUnixNano(long timeUnixNano) {
        checkAndPrepareUpdate();
        protoEventBuilder.setTimeUnixNano(timeUnixNano);
    }

    public String getName() {
        return protoEventBuilder == null ? protoEvent.getName() : protoEventBuilder.getName();
    }

    public void setName(String name) {
        checkAndPrepareUpdate();
        protoEventBuilder.setName(name);
    }

    public int getDroppedAttributesCount() {
        return protoEventBuilder == null ? protoEvent.getDroppedAttributesCount() :
                protoEventBuilder.getDroppedAttributesCount();
    }

    public void setDroppedAttributesCount(int droppedAttributesCount) {
        checkAndPrepareUpdate();
        protoEventBuilder.setDroppedAttributesCount(droppedAttributesCount);
    }

    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoEvent.getAttributesList(), updatable);
        }
        return protoAttributesAdapter;
    }

    public boolean isUpdated() {
        return updated;
    }
    public Span.Event getUpdated() {
        if (!updatable) {
            return protoEvent;
        } else if (!updated && (protoAttributesAdapter == null || !protoAttributesAdapter.isUpdated())) {
            return protoEvent;
        } else {
            Span.Event.Builder bld = protoEventBuilder != null ?
                    protoEventBuilder: protoEvent.toBuilder();
            if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
                bld.clearAttributes();
                bld.addAllAttributes(protoAttributesAdapter.getAsKeyValueList());
            }
            return bld.build();
        }
    }
    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new IllegalStateException("trying to change a non updatable event!");
        }
        if (protoEventBuilder == null) {
            protoEventBuilder = protoEvent.toBuilder();
        }
        updated = true;
    }
}
