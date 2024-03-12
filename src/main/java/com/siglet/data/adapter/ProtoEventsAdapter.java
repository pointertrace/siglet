package com.siglet.data.adapter;

import com.siglet.data.modifiable.ModifiableEvents;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProtoEventsAdapter implements ModifiableEvents {

    private List<ProtoEventAdapter> eventsAdapter;

    private List<Span.Event> protoEvents;

    private boolean updatable;

    private boolean updated;

    public ProtoEventsAdapter(List<Span.Event> protoEvents, boolean updatable) {
        this.eventsAdapter = new ArrayList<>();
        protoEvents.forEach(e -> eventsAdapter.add(new ProtoEventAdapter(e, updatable)) );
        this.protoEvents = protoEvents;
        this.updatable = updatable;
    }

    public int size() {
        return eventsAdapter.size();
    }

    public ProtoEventAdapter get(int i) {
        return eventsAdapter.get(i);
    }

    public ProtoEventAdapter remove(int i) {
        checkUpdate();
        return eventsAdapter.remove(i);
    }

    public void add(String name, long timeUnixNano, int droppedAttributesCount, Map<String, Object> attributes) {
        checkUpdate();
        eventsAdapter.add(new ProtoEventAdapter(Span.Event.newBuilder()
                .setName(name)
                .setTimeUnixNano(timeUnixNano)
                .setDroppedAttributesCount(droppedAttributesCount)
                .addAllAttributes(AdapterUtils.mapToKeyValueList(attributes))
                .build(), updatable));
    }

    public boolean isUpdated() {
        return updated;
    }

    public List<Span.Event> getUpdated() {
        if (!updatable) {
            return  protoEvents;
        } else if (!updated && !anyEventUpdated()) {
            return protoEvents;
        } else {
            List<Span.Event> result = new ArrayList<>(protoEvents.size());
            for(ProtoEventAdapter eventAdapter: eventsAdapter) {
                result.add(eventAdapter.getUpdated());
            }
            return result;
        }
    }


    private boolean anyEventUpdated() {
        for(ProtoEventAdapter ea: eventsAdapter) {
            if (ea.isUpdated()) {
                return true;
            }
        }
        return false;
    }
    private void checkUpdate() {
        if (!updatable) {
            throw new IllegalStateException("trying to change a non updatable event list!");
        }
        updated = true;
    }

}
