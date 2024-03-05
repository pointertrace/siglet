package com.siglet.data.adapter;

import com.siglet.data.modifiable.ModifiableEvent;
import io.opentelemetry.proto.trace.v1.Span;

public class ProtoEventAdapter implements ModifiableEvent {

    private Span.Event protoEvent;

    private boolean changeable;

    private boolean changed;

    private Span.Event.Builder protoEventBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoEventAdapter(Span.Event protoEvent, boolean changeable) {
        this.protoEvent = protoEvent;
        this.changeable = changeable;
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
            protoAttributesAdapter = new ProtoAttributesAdapter(protoEvent.getAttributesList(), changeable);
        }
        return protoAttributesAdapter;
    }

    public boolean isChanged() {
        return changed;
    }

    private void checkAndPrepareUpdate() {
        if (!changeable) {
            throw new IllegalStateException("trying to change a non updatable event!");
        }
        if (protoEventBuilder == null) {
            protoEventBuilder = Span.Event.newBuilder();
        }
        changed = true;
    }
}
