package com.siglet.container.adapter.common;

import com.siglet.api.modifiable.ModifiableEvents;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.trace.v1.Span;

public class ProtoEventsAdapter extends AdapterList<Span.Event, Span.Event.Builder, ProtoEventAdapter>
        implements ModifiableEvents {

    public ProtoEventsAdapter() {
        super(AdapterListConfig.EVENTS_ADAPTER_CONFIG);
    }


}
