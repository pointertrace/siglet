package com.siglet.data.adapter.common;

import com.google.protobuf.Message;
import com.siglet.data.adapter.AdapterList;
import com.siglet.data.modifiable.ModifiableEvents;
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


    public void remove(int i) {
        super.remove(i);
    }

    public ProtoEventAdapter add() {
        return super.add();
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
