package com.siglet.container.adapter.common;

import com.siglet.api.modifiable.ModifiableEvents;
import com.siglet.container.adapter.AdapterList;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.List;

public class ProtoEventsAdapter extends AdapterList<Span.Event, Span.Event.Builder, ProtoEventAdapter>
        implements ModifiableEvents {

    public ProtoEventsAdapter(List<Span.Event> events, boolean updatable) {
        super(events, updatable);
    }

    @Override
    public ProtoEventAdapter get(int i) {
        return getAdapter(i);
    }


    @Override
    public void remove(int i) {
        super.remove(i);
    }

    @Override
    protected ProtoEventAdapter createAdapter(int i) {
        return new ProtoEventAdapter(getMessage(i), isUpdatable());
    }


    @Override
    protected ProtoEventAdapter createNewAdapter() {
        return new ProtoEventAdapter(Span.Event.newBuilder());
    }

}
