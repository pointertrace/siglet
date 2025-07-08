package com.siglet.api.adapter

import com.siglet.container.adapter.AdapterUtils
import com.siglet.container.adapter.common.ProtoAttributesAdapter
import io.opentelemetry.proto.common.v1.KeyValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class ProtoAttributesAdapterGroovyTest {

    private List<KeyValue> protoAttributes

    private ProtoAttributesAdapter protoAttributesAdapter

    @BeforeEach
    void setUp() {

        protoAttributes = new ArrayList<>()

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("string-key")
                .setValue(AdapterUtils.objectToAnyValue("string-value"))
                .build())

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("bool-key")
                .setValue(AdapterUtils.objectToAnyValue(true))
                .build())

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("long-key")
                .setValue(AdapterUtils.objectToAnyValue(10L))
                .build())

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("double-key")
                .setValue(AdapterUtils.objectToAnyValue(1.2D))
                .build())

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("array-key")
                .setValue(AdapterUtils.objectToAnyValue(new Object[]{"1", "2"}))
                .build())

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("key-value-list-key")
                .setValue(AdapterUtils.objectToAnyValue(List.of(
                        new AbstractMap.SimpleImmutableEntry<>("key1", "value1"),
                        new AbstractMap.SimpleImmutableEntry<>("key2", "value2"))))
                .build())

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("byte-array-key")
                .setValue(AdapterUtils.objectToAnyValue(new byte[]{0, 1, 2}))
                .build())

        protoAttributesAdapter = new ProtoAttributesAdapter()
        protoAttributesAdapter.recycle(protoAttributes)
    }


    @Test
    void get() {
        assertEquals("string-value", protoAttributesAdapter["string-key"])
        assertTrue(protoAttributesAdapter["bool-key"])
        assertEquals(10L, protoAttributesAdapter["long-key"])
        assertEquals(1.2D, protoAttributesAdapter["double-key"])
        assertArrayEquals(new Object[]{"1", "2"}, protoAttributesAdapter["array-key"])
        assertEquals(List.of(
                new AbstractMap.SimpleImmutableEntry<>("key1", "value1"),
                new AbstractMap.SimpleImmutableEntry<>("key2", "value2")),
                protoAttributesAdapter["key-value-list-key"])
        assertArrayEquals(new byte[]{0, 1, 2}, protoAttributesAdapter["byte-array-key"])
    }

    @Test
    void change() {
        protoAttributesAdapter["string-key"] = "other-value"
        protoAttributesAdapter["new-string-key"] = "new-value"
        protoAttributesAdapter -= "bool-key"
        protoAttributesAdapter["long-key"] = 20L
        protoAttributesAdapter["double-key"] = 2.3D
        protoAttributesAdapter["array-key"] = new Object[]{"3", "4"}
        protoAttributesAdapter["key-value-list-key"] = List.of(
                new AbstractMap.SimpleImmutableEntry<>("key2", "new-value2"),
                new AbstractMap.SimpleImmutableEntry<>("key3", "value3"))
        protoAttributesAdapter["byte-array-key"] = new byte[]{3, 4}


        assertTrue(protoAttributesAdapter.containsKey("string-key"))
        assertEquals("other-value", protoAttributesAdapter["string-key"])
        assertTrue("string-key" in protoAttributesAdapter)
        assertEquals("new-value", protoAttributesAdapter["new-string-key"])
        assertFalse("bool-key" in protoAttributesAdapter)
        assertEquals(20L, protoAttributesAdapter["long-key"])
        assertEquals(2.3D, protoAttributesAdapter["double-key"])
        assertArrayEquals(new Object[]{"3", "4"}, protoAttributesAdapter["array-key"])
        assertEquals(List.of(
                new AbstractMap.SimpleImmutableEntry<>("key2", "new-value2"),
                new AbstractMap.SimpleImmutableEntry<>("key3", "value3")),
                protoAttributesAdapter["key-value-list-key"])
        assertArrayEquals(new byte[]{3, 4}, protoAttributesAdapter["byte-array-key"])
    }

}