package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.Event;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class EventsAdapterTest {
    @Test
    void shouldRejectNullEvents() {
        assertThrows(NullPointerException.class, () -> new EventsAdapter(null));
    }
    @Test
    void shouldReturnCorrectSize() {
        io.opentelemetry.proto.trace.v1.Span.Event e = io.opentelemetry.proto.trace.v1.Span.Event.getDefaultInstance();
        EventsAdapter adapter = new EventsAdapter(Collections.singletonList(e));
        assertEquals(1, adapter.getSize());
    }
    @Test
    void shouldGetEventAtIndex() {
        io.opentelemetry.proto.trace.v1.Span.Event e = io.opentelemetry.proto.trace.v1.Span.Event.newBuilder().setName("test-event").build();
        EventsAdapter adapter = new EventsAdapter(Collections.singletonList(e));
        Event event = adapter.get(0);
        assertNotNull(event);
        assertEquals("test-event", event.getName());
    }
    @Test
    void shouldRemoveEventAtIndex() {
        io.opentelemetry.proto.trace.v1.Span.Event e = io.opentelemetry.proto.trace.v1.Span.Event.getDefaultInstance();
        AtomicReference<List<io.opentelemetry.proto.trace.v1.Span.Event>> captured = new AtomicReference<>();
        EventsAdapter adapter = new EventsAdapter(Collections.singletonList(e), captured::set);
        adapter.remove(0);
        assertEquals(0, adapter.getSize());
        assertNotNull(captured.get());
    }
    @Test
    void shouldAddNewEvent() {
        EventsAdapter adapter = new EventsAdapter(Collections.emptyList(), list -> {});
        Event added = adapter.add();
        assertNotNull(added);
        assertEquals(1, adapter.getSize());
    }
}
