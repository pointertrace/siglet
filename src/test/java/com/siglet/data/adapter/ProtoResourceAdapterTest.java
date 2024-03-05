package com.siglet.data.adapter;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoResourceAdapterTest {

    private ProtoResourceAdapter protoResourceAdapter;

    @BeforeEach
    void setUp() {
        Resource resource = Resource.newBuilder()
                .setDroppedAttributesCount(1)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("str-attribute-value").build())
                        .build())
                .addAttributes(KeyValue.newBuilder()
                        .setKey("long-attribute")
                        .setValue(AnyValue.newBuilder().setIntValue(10L).build())
                        .build())
                .build();

        protoResourceAdapter = new ProtoResourceAdapter(resource, true);

    }

    @Test
    public void get(){

        assertEquals(1, protoResourceAdapter.getDroppedAttributesCount());

    }

    @Test
    public void setAndGet(){
        protoResourceAdapter.setDroppedAttributesCount(2);

        assertEquals(2, protoResourceAdapter.getDroppedAttributesCount());

    }


    @Test
    public void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoResourceAdapter.getAttributes();

        assertTrue(protoAttributesAdapter.has("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "str-attribute-value");

        Map<String, Object> attributesMap = protoAttributesAdapter.getAsMap();

        assertEquals(2, attributesMap.size());

        assertTrue(attributesMap.containsKey("str-attribute"));
        assertInstanceOf(String.class, attributesMap.get("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "str-attribute-value");

        assertTrue(attributesMap.containsKey("long-attribute"));
        assertInstanceOf(Long.class, attributesMap.get("long-attribute"));
        assertEquals(10L, protoAttributesAdapter.getAsLong("long-attribute"));

        assertFalse(protoAttributesAdapter.isChanged());
    }


    @Test
    public void attributesChangeAndGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoResourceAdapter.getAttributes();

        protoAttributesAdapter.set("str-attribute", "new-str-attribute-value");
        protoAttributesAdapter.set("bool-attribute", true);
        protoAttributesAdapter.remove("long-attribute");

        assertTrue(protoAttributesAdapter.has("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "new-str-attribute-value");

        Map<String, Object> attributesMap = protoAttributesAdapter.getAsMap();
        assertEquals(2, attributesMap.size());

        assertTrue(attributesMap.containsKey("str-attribute"));
        assertInstanceOf(String.class, attributesMap.get("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "new-str-attribute-value");

        assertTrue(attributesMap.containsKey("bool-attribute"));
        assertInstanceOf(Boolean.class, attributesMap.get("bool-attribute"));
        assertTrue(protoAttributesAdapter.getAsBoolean("bool-attribute"));

        assertTrue(protoAttributesAdapter.isChanged());
    }

    @Test
    public void changeNonUpdatable() {

        protoResourceAdapter = new ProtoResourceAdapter(Resource.newBuilder().build(), false);

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoResourceAdapter.setDroppedAttributesCount(0);
        });

        assertThrowsExactly(IllegalStateException.class, () -> {
            protoResourceAdapter.getAttributes().remove("str-key");
        });

    }

}