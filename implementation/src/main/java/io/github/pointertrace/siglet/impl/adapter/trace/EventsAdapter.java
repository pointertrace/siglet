package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.Event;
import io.github.pointertrace.siglet.api.signal.Events;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class EventsAdapter implements Events {
    private final List<io.opentelemetry.proto.trace.v1.Span.Event> events;
    private final Consumer<List<io.opentelemetry.proto.trace.v1.Span.Event>> onChange;

    public EventsAdapter(List<io.opentelemetry.proto.trace.v1.Span.Event> events) {
        this(events, null);
    }

    public EventsAdapter(List<io.opentelemetry.proto.trace.v1.Span.Event> events,
                         Consumer<List<io.opentelemetry.proto.trace.v1.Span.Event>> onChange) {
        this.events = new ArrayList<>(Objects.requireNonNull(events, "events"));
        this.onChange = onChange;
    }

    public List<io.opentelemetry.proto.trace.v1.Span.Event> getUpdated() {
        return new ArrayList<>(events);
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(new ArrayList<>(events));
        }
    }

    @Override
    public int getSize() {
        return events.size();
    }

    @Override
    public Event get(int i) {
        io.opentelemetry.proto.trace.v1.Span.Event event = events.get(i);
        return new EventAdapter(event, updated -> {
            events.set(i, updated);
            changed();
        });
    }

    @Override
    public void remove(int i) {
        events.remove(i);
        changed();
    }

    @Override
    public EventAdapter add() {
        io.opentelemetry.proto.trace.v1.Span.Event value = io.opentelemetry.proto.trace.v1.Span.Event.getDefaultInstance();
        events.add(value);
        changed();
        int idx = events.size() - 1;
        return new EventAdapter(value, updated -> {
            events.set(idx, updated);
            changed();
        });
    }
}
