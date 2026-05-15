package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.Event;
import io.github.pointertrace.siglet.impl.adapter.AttributesAdapter;

import java.util.Objects;
import java.util.function.Consumer;

public final class EventAdapter implements Event {
    private final io.opentelemetry.proto.trace.v1.Span.Event original;
    private io.opentelemetry.proto.trace.v1.Span.Event.Builder builder;
    private final Consumer<io.opentelemetry.proto.trace.v1.Span.Event> onChange;
    private AttributesAdapter attributesAdapter;

    public EventAdapter(io.opentelemetry.proto.trace.v1.Span.Event event) {
        this(event, null);
    }

    public EventAdapter(io.opentelemetry.proto.trace.v1.Span.Event event,
                        Consumer<io.opentelemetry.proto.trace.v1.Span.Event> onChange) {
        this.original = Objects.requireNonNull(event, "event");
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.trace.v1.Span.Event.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.trace.v1.Span.EventOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.trace.v1.Span.Event getUpdated() {
        return builder == null ? original : builder.build();
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(getUpdated());
        }
    }

    @Override
    public String getName() {
        return view().getName();
    }

    @Override
    public EventAdapter setName(String name) {
        mutate().setName(Objects.requireNonNull(name, "name"));
        changed();
        return this;
    }

    @Override
    public long getTimeUnixNano() {
        return view().getTimeUnixNano();
    }

    @Override
    public EventAdapter setTimeUnixNano(long timeUnixNano) {
        mutate().setTimeUnixNano(timeUnixNano);
        changed();
        return this;
    }

    @Override
    public Attributes getAttributes() {
        if (attributesAdapter == null) {
            attributesAdapter = new AttributesAdapter(view().getAttributesList(), values -> {
                io.opentelemetry.proto.trace.v1.Span.Event.Builder b = mutate();
                b.clearAttributes();
                b.addAllAttributes(values);
                changed();
            });
        }
        return attributesAdapter;
    }

    @Override
    public int getDroppedAttributesCount() {
        return view().getDroppedAttributesCount();
    }

    @Override
    public EventAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        mutate().setDroppedAttributesCount(droppedAttributesCount);
        changed();
        return this;
    }
}
