package com.siglet.data.adapter;

import com.siglet.SigletError;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoEventsAdapterTest {

    private List<Span.Event> protoEvents;

    private ProtoEventsAdapter protoEventsAdapter;

    @BeforeEach
    void setUp() {
        protoEvents = List.of(
                Span.Event.newBuilder()
                        .setName("first-event")
                        .setTimeUnixNano(1)
                        .setDroppedAttributesCount(1)
                        .build(),
                Span.Event.newBuilder()
                        .setName("second-event")
                        .setTimeUnixNano(2)
                        .setDroppedAttributesCount(2)
                        .build()
        );

        protoEventsAdapter = new ProtoEventsAdapter(protoEvents, true);
    }

    @Test
    public void get() {

        assertEquals(2, protoEventsAdapter.size());

        ProtoEventAdapter protoEventAdapter = protoEventsAdapter.get(0);

        assertEquals("first-event", protoEventAdapter.getName());
        assertEquals(1, protoEventAdapter.getTimeUnixNano());
        assertEquals(1, protoEventAdapter.getDroppedAttributesCount());


        protoEventAdapter = protoEventsAdapter.get(1);

        assertEquals("second-event", protoEventAdapter.getName());
        assertEquals(2, protoEventAdapter.getTimeUnixNano());
        assertEquals(2, protoEventAdapter.getDroppedAttributesCount());


        assertFalse(protoEventsAdapter.isUpdated());

    }

    @Test
    public void changeNonUpdatable() {
        protoEventsAdapter = new ProtoEventsAdapter(List.of(Span.Event.newBuilder().build()), false);


        assertThrowsExactly(SigletError.class, () ->
                protoEventsAdapter.add("new-name", 1, 1, Collections.emptyMap()));

        assertThrowsExactly(SigletError.class, () -> protoEventsAdapter.remove(0));

        assertThrowsExactly(SigletError.class, () -> protoEventsAdapter.get(0).setName("new-name"));

        assertFalse(protoEventsAdapter.isUpdated());
    }

    @Test
    public void remove() {


        assertEquals(2, protoEventsAdapter.size());

        ProtoEventAdapter eventRemoved = protoEventsAdapter.remove(0);


        assertEquals(1, protoEventsAdapter.size());

        assertEquals("first-event", eventRemoved.getName());
        assertEquals(1, eventRemoved.getTimeUnixNano());
        assertEquals(1, eventRemoved.getDroppedAttributesCount());

        ProtoEventAdapter protoEventAdapter = protoEventsAdapter.get(0);

        assertEquals("second-event", protoEventAdapter.getName());
        assertEquals(2, protoEventAdapter.getTimeUnixNano());
        assertEquals(2, protoEventAdapter.getDroppedAttributesCount());

    }

    @Test
    public void add() {

        assertEquals(2, protoEventsAdapter.size());

        protoEventsAdapter.add("third-event", 3, 3, Map.of("str-attribute", "str-attribute-value"));

        assertEquals(3, protoEventsAdapter.size());

        ProtoEventAdapter protoEventAdapter = protoEventsAdapter.get(0);

        assertEquals("first-event", protoEventAdapter.getName());
        assertEquals(1, protoEventAdapter.getTimeUnixNano());
        assertEquals(1, protoEventAdapter.getDroppedAttributesCount());

        protoEventAdapter = protoEventsAdapter.get(1);

        assertEquals("second-event", protoEventAdapter.getName());
        assertEquals(2, protoEventAdapter.getTimeUnixNano());
        assertEquals(2, protoEventAdapter.getDroppedAttributesCount());

        protoEventAdapter = protoEventsAdapter.get(2);

        assertEquals("third-event", protoEventAdapter.getName());
        assertEquals(3, protoEventAdapter.getTimeUnixNano());
        assertEquals(3, protoEventAdapter.getDroppedAttributesCount());

        ProtoAttributesAdapter attributes = protoEventAdapter.getAttributes();

        assertEquals(1, attributes.size());
        assertTrue(attributes.has("str-attribute"));
        assertTrue(attributes.isString("str-attribute"));
        assertEquals("str-attribute-value", attributes.getAsString("str-attribute"));


    }


    @Test
    public void getUpdated_notUpdatable() {

        protoEventsAdapter = new ProtoEventsAdapter(protoEvents, false);

        assertSame(protoEvents, protoEventsAdapter.getUpdated());

    }


    @Test
    public void getUpdated_notingUpdated() {

        assertSame(protoEvents, protoEventsAdapter.getUpdated());

    }

    @Test
    public void getUpdated_listChanged() {

        protoEventsAdapter.remove(0);


        List<Span.Event> actual = protoEventsAdapter.getUpdated();
        assertNotSame(protoEvents, actual);

        assertEquals(1, protoEventsAdapter.size());
        ProtoEventAdapter protoEventAdapter = protoEventsAdapter.get(0);

        assertNotNull(protoEventAdapter);

        assertEquals("second-event", protoEventAdapter.getName());

    }


    @Test
    public void getUpdated_listContentChanged() {

        protoEventsAdapter.get(0).setName("new-name");

        List<Span.Event> actual = protoEventsAdapter.getUpdated();
        assertNotSame(protoEvents, actual);

        assertEquals(2, actual.size());
        Span.Event protoEvent= actual.get(0);

        assertNotNull(protoEvent);

        assertEquals("new-name", protoEvent.getName());
        assertEquals(1, protoEvent.getTimeUnixNano());
        assertEquals(1, protoEvent.getDroppedAttributesCount());


        protoEvent = actual.get(1);

        assertEquals("second-event", protoEvent.getName());
        assertEquals(2, protoEvent.getTimeUnixNano());
        assertEquals(2, protoEvent.getDroppedAttributesCount());


    }
}