package io.github.pointertrace.siglet.impl.adapter.common;

import io.github.pointertrace.siglet.api.signal.Events;
import io.github.pointertrace.siglet.impl.adapter.AdapterList;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.opentelemetry.proto.trace.v1.Span;

public class ProtoEventsAdapter extends AdapterList<Span.Event, Span.Event.Builder, ProtoEventAdapter>
        implements Events {

    public ProtoEventsAdapter() {
        super(AdapterListConfig.EVENTS_ADAPTER_CONFIG);
    }


}
