package com.siglet.data.adapter;

import com.siglet.SigletError;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import com.siglet.data.adapter.common.ProtoEventAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoEventAdapterTest {

    private Span.Event protoEvent;
    ProtoEventAdapter protoEventAdapter;

    @BeforeEach
    void setUp() {
        protoEvent = Span.Event.newBuilder()
                .setName("event-name")
                .setTimeUnixNano(1)
                .setDroppedAttributesCount(2)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("str-attribute-value").build())
                        .build())
                .addAttributes(KeyValue.newBuilder()
                        .setKey("long-attribute")
                        .setValue(AnyValue.newBuilder().setIntValue(10L).build())
                        .build())
                .build();

        protoEventAdapter = new ProtoEventAdapter(protoEvent, true);

    }

    @Test
    public void get() {

        assertEquals("event-name", protoEventAdapter.getName());
        assertEquals(1, protoEventAdapter.getTimeUnixNano());
        assertEquals(2, protoEventAdapter.getDroppedAttributesCount());
        assertFalse(protoEventAdapter.isUpdated());

    }

    @Test
    public void setAndGet() {

        protoEventAdapter.setName("new-event-name");
        protoEventAdapter.setTimeUnixNano(2);
        protoEventAdapter.setDroppedAttributesCount(3);

        assertEquals("new-event-name", protoEventAdapter.getName());
        assertEquals(2, protoEventAdapter.getTimeUnixNano());
        assertEquals(3, protoEventAdapter.getDroppedAttributesCount());
        assertTrue(protoEventAdapter.isUpdated());
    }

    @Test
    public void changeNonUpdatable() {
        protoEventAdapter = new ProtoEventAdapter(Span.Event.newBuilder().build(), false);


        assertThrowsExactly(SigletError.class, () -> protoEventAdapter.setName("new-event-name"));

        assertThrowsExactly(SigletError.class, () -> protoEventAdapter.setTimeUnixNano(2));

        assertThrowsExactly(SigletError.class, () -> protoEventAdapter.setDroppedAttributesCount(3));

        assertThrowsExactly(SigletError.class, () -> protoEventAdapter.setDroppedAttributesCount(3));

        assertThrowsExactly(SigletError.class, () -> protoEventAdapter.getAttributes().set("any", "any-value"));

        assertThrowsExactly(SigletError.class, () -> protoEventAdapter.getAttributes().remove("any"));

        assertThrowsExactly(SigletError.class, () -> protoEventAdapter.getAttributes().remove("any"));

        assertFalse(protoEventAdapter.isUpdated());
    }

    @Test
    public void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoEventAdapter.getAttributes();

        assertTrue(protoAttributesAdapter.has("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "str-attribute-value");

        assertFalse(protoAttributesAdapter.isUpdated());
    }


    @Test
    public void attributesChangeAndGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoEventAdapter.getAttributes();

        protoAttributesAdapter.set("str-attribute", "new-str-attribute-value");
        protoAttributesAdapter.set("bool-attribute", true);
        protoAttributesAdapter.remove("long-attribute");

        assertTrue(protoAttributesAdapter.has("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "new-str-attribute-value");

        assertTrue(protoAttributesAdapter.isUpdated());
    }


    @Test
    public void getUpdate_notUpdatable() {

        Span.Event actualProtoEvent = Span.Event.newBuilder().build();
        protoEventAdapter = new ProtoEventAdapter(actualProtoEvent, false);

        assertSame(actualProtoEvent, protoEventAdapter.getUpdated());

    }

    @Test
    public void getUpdated_nothingUpdated() {

        assertSame(protoEvent, protoEventAdapter.getUpdated());
        assertSame(protoEvent.getAttributesList(), protoEventAdapter.getUpdated().getAttributesList());

    }

    @Test
    public void getUpdated_onlyEventUpdated() {

        protoEventAdapter.setName("new-name");

        Span.Event actual = protoEventAdapter.getUpdated();
        assertNotSame(protoEvent, actual);
        assertSame(protoEvent.getAttributesList(), actual.getAttributesList());
        assertEquals("new-name", actual.getName());

    }


    @Test
    public void getUpdated_onlyPropertiesUpdated() {

        protoEventAdapter.getAttributes().set("bool-attribute", true);

        Span.Event actual = protoEventAdapter.getUpdated();
        assertNotSame(protoEvent, actual);
        assertNotSame(protoEvent.getAttributesList(), actual.getAttributesList());

        assertEquals("event-name", actual.getName());
        assertEquals(1, actual.getTimeUnixNano());
        assertEquals(2, actual.getDroppedAttributesCount());

        assertEquals(3, actual.getAttributesList().size());
        Map<String, Object> attrAsMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());
        assertEquals(3, attrAsMap.size());
        assertTrue(attrAsMap.containsKey("str-attribute"));
        assertTrue(attrAsMap.containsKey("long-attribute"));
        assertTrue(attrAsMap.containsKey("bool-attribute"));

    }

    @Test
    public void getUpdated_EventAndPropertiesUpdated() {

        protoEventAdapter.getAttributes().set("bool-attribute", true);
        protoEventAdapter.setName("new-name");

        Span.Event actual = protoEventAdapter.getUpdated();
        assertNotSame(protoEvent, actual);
        assertNotSame(protoEvent.getAttributesList(), actual.getAttributesList());

        assertEquals("new-name", actual.getName());
        assertEquals(1, actual.getTimeUnixNano());
        assertEquals(2, actual.getDroppedAttributesCount());

        assertEquals(3, actual.getAttributesList().size());
        Map<String, Object> attrAsMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());
        assertEquals(3, attrAsMap.size());
        assertTrue(attrAsMap.containsKey("str-attribute"));
        assertTrue(attrAsMap.containsKey("long-attribute"));
        assertTrue(attrAsMap.containsKey("bool-attribute"));

    }

}