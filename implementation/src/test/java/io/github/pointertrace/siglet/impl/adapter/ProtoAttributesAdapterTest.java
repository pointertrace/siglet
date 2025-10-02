package io.github.pointertrace.siglet.impl.adapter;

import com.google.protobuf.ByteString;
import io.github.pointertrace.siglet.impl.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.ArrayValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.common.v1.KeyValueList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtoAttributesAdapterTest {

    private List<KeyValue> protoAttributes;

    private ProtoAttributesAdapter protoAttributesAdapter;

    @BeforeEach
    void setUp() {

        protoAttributes = new ArrayList<>();

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("string-key")
                .setValue(AdapterUtils.objectToAnyValue("string-value"))
                .build());

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("bool-key")
                .setValue(AdapterUtils.objectToAnyValue(true))
                .build());

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("long-key")
                .setValue(AdapterUtils.objectToAnyValue(10L))
                .build());

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("double-key")
                .setValue(AdapterUtils.objectToAnyValue(1.2))
                .build());

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("array-key")
                .setValue(AdapterUtils.objectToAnyValue(new Object[]{"1", "2"}))
                .build());

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("key-value-list-key")
                .setValue(AdapterUtils.objectToAnyValue(List.of(
                        new AbstractMap.SimpleImmutableEntry<>("key1", "value1"),
                        new AbstractMap.SimpleImmutableEntry<>("key2", "value2"))))
                .build());

        protoAttributes.add(KeyValue.newBuilder()
                .setKey("byte-array-key")
                .setValue(AdapterUtils.objectToAnyValue(new byte[]{0, 1, 2}))
                .build());

        protoAttributesAdapter = new ProtoAttributesAdapter();
        protoAttributesAdapter.recycle(protoAttributes);
    }


    @Test
    void isType() {

        assertTrue(protoAttributesAdapter.isString("string-key"));
        assertTrue(protoAttributesAdapter.isBoolean("bool-key"));
        assertTrue(protoAttributesAdapter.isLong("long-key"));
        assertTrue(protoAttributesAdapter.isDouble("double-key"));
        assertTrue(protoAttributesAdapter.isArray("array-key"));
        assertTrue(protoAttributesAdapter.isKeyValueList("key-value-list-key"));
        assertTrue(protoAttributesAdapter.isByteArray("byte-array-key"));
        assertFalse(protoAttributesAdapter.isUpdated());

    }

    @Test
    void getAttributeAsType() {

        assertEquals("string-value", protoAttributesAdapter.getAsString("string-key"));
        assertTrue(protoAttributesAdapter.getAsBoolean("bool-key"));
        assertEquals(10L, protoAttributesAdapter.getAsLong("long-key"));
        assertEquals(1.2, protoAttributesAdapter.getAsDouble("double-key"));
        assertArrayEquals(new Object[]{"1", "2"}, protoAttributesAdapter.getAsArray("array-key"));
        assertEquals(List.of(
                        new AbstractMap.SimpleImmutableEntry<>("key1", "value1"),
                        new AbstractMap.SimpleImmutableEntry<>("key2", "value2")),
                protoAttributesAdapter.getAsKeyValueList("key-value-list-key"));
        assertArrayEquals(new byte[]{0, 1, 2}, protoAttributesAdapter.getAttributeAsByteArray("byte-array-key"));
        assertFalse(protoAttributesAdapter.isUpdated());


        List<KeyValue> actualKvl = protoAttributesAdapter.getUpdated();

        assertEquals(7, actualKvl.size());
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("string-key")
                .setValue(AnyValue.newBuilder().setStringValue("string-value")
                        .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("bool-key")
                .setValue(AnyValue.newBuilder().setBoolValue(true)
                        .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("long-key")
                .setValue(AnyValue.newBuilder().setIntValue(10L)
                        .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("double-key")
                .setValue(AnyValue.newBuilder().setDoubleValue(1.2)
                        .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("array-key")
                .setValue(
                        AnyValue.newBuilder()
                                .setArrayValue(
                                        ArrayValue.newBuilder()
                                                .addValues(AnyValue.newBuilder().setStringValue("1").build())
                                                .addValues(AnyValue.newBuilder().setStringValue("2").build())
                                                .build())
                                .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("key-value-list-key")
                .setValue(AnyValue.newBuilder()
                        .setKvlistValue(KeyValueList.newBuilder()
                                .addValues(KeyValue.newBuilder()
                                        .setKey("key1")
                                        .setValue(AnyValue.newBuilder().setStringValue("value1").build())
                                        .build())
                                .addValues(KeyValue.newBuilder()
                                        .setKey("key2")
                                        .setValue(AnyValue.newBuilder().setStringValue("value2").build())
                                        .build())
                                .build())
                        .build())
                .build()));

        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("byte-array-key")
                .setValue(AnyValue.newBuilder()
                        .setBytesValue(ByteString.copyFrom(new byte[]{0, 1, 2}))
                        .build())
                .build()));
    }

    @Test
    void changeAttributes() {

        protoAttributesAdapter
                .set("string-key", "other-value")
                .set("new-string-key", "new-value")
                .remove("bool-key")
                .set("long-key", 20L)
                .set("double-key", 2.3)
                .set("array-key", new Object[]{"3", "4"})
                .set("key-value-list-key", List.of(
                        new AbstractMap.SimpleImmutableEntry<>("key2", "new-value2"),
                        new AbstractMap.SimpleImmutableEntry<>("key3", "value3")))
                .set("byte-array-key", new byte[]{3, 4});


        assertTrue(protoAttributesAdapter.isString("string-key"));
        assertEquals("other-value", protoAttributesAdapter.getAsString("string-key"));

        assertTrue(protoAttributesAdapter.isString("new-string-key"));
        assertEquals("new-value", protoAttributesAdapter.getAsString("new-string-key"));

        assertFalse(protoAttributesAdapter.containsKey("bool-key"));

        assertTrue(protoAttributesAdapter.isLong("long-key"));
        assertEquals(20L, protoAttributesAdapter.getAsLong("long-key"));

        assertTrue(protoAttributesAdapter.isDouble("double-key"));
        assertEquals(2.3, protoAttributesAdapter.getAsDouble("double-key"));

        assertTrue(protoAttributesAdapter.isArray("array-key"));
        assertArrayEquals(new Object[]{"3", "4"}, protoAttributesAdapter.getAsArray("array-key"));

        assertTrue(protoAttributesAdapter.isKeyValueList("key-value-list-key"));
        assertEquals(List.of(
                        new AbstractMap.SimpleImmutableEntry<>("key2", "new-value2"),
                        new AbstractMap.SimpleImmutableEntry<>("key3", "value3")),
                protoAttributesAdapter.getAsKeyValueList("key-value-list-key"));

        assertTrue(protoAttributesAdapter.isByteArray("byte-array-key"));
        assertArrayEquals(new byte[]{3, 4}, protoAttributesAdapter.getAttributeAsByteArray("byte-array-key"));

        assertTrue(protoAttributesAdapter.isUpdated());

        List<KeyValue> actualKvl = protoAttributesAdapter.getUpdated();

        assertEquals(7, actualKvl.size());
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("string-key")
                .setValue(AnyValue.newBuilder().setStringValue("other-value")
                        .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("new-string-key")
                .setValue(AnyValue.newBuilder().setStringValue("new-value")
                        .build())
                .build()));
        assertFalse(actualKvl.contains(KeyValue.newBuilder()
                .setKey("bool-key")
                .setValue(AnyValue.newBuilder().setBoolValue(false)
                        .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("long-key")
                .setValue(AnyValue.newBuilder().setIntValue(20L)
                        .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("double-key")
                .setValue(AnyValue.newBuilder().setDoubleValue(2.3)
                        .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("array-key")
                .setValue(
                        AnyValue.newBuilder()
                                .setArrayValue(
                                        ArrayValue.newBuilder()
                                                .addValues(AnyValue.newBuilder().setStringValue("3").build())
                                                .addValues(AnyValue.newBuilder().setStringValue("4").build())
                                                .build())
                                .build())
                .build()));
        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("key-value-list-key")
                .setValue(AnyValue.newBuilder()
                        .setKvlistValue(KeyValueList.newBuilder()
                                .addValues(KeyValue.newBuilder()
                                        .setKey("key2")
                                        .setValue(AnyValue.newBuilder().setStringValue("new-value2").build())
                                        .build())
                                .addValues(KeyValue.newBuilder()
                                        .setKey("key3")
                                        .setValue(AnyValue.newBuilder().setStringValue("value3").build())
                                        .build())
                                .build())
                        .build())
                .build()));

        assertTrue(actualKvl.contains(KeyValue.newBuilder()
                .setKey("byte-array-key")
                .setValue(AnyValue.newBuilder()
                        .setBytesValue(ByteString.copyFrom(new byte[]{3, 4}))
                        .build())
                .build()));
    }


    @Test
    void getUpdated_notUpdatable() {

        protoAttributesAdapter = new ProtoAttributesAdapter();
        protoAttributesAdapter.recycle(protoAttributes);

        assertSame(protoAttributes, protoAttributesAdapter.getUpdated());

    }

    @Test
    void getUpdated_nothingUpdated() {

        assertSame(protoAttributes, protoAttributesAdapter.getUpdated());

    }

    @Test
    void getUpdated_getAttribute() {

        assertEquals("string-value", protoAttributesAdapter.getAsString("string-key"));

        assertSame(protoAttributes, protoAttributesAdapter.getUpdated());

    }

    @Test
    void getUpdated_attributeChange() {

        protoAttributesAdapter.set("string-key", "new-string-value");


        List<KeyValue> actual = protoAttributesAdapter.getUpdated();

        assertNotSame(protoAttributes, actual);

        assertEquals(7, actual.size());
        assertTrue(actual.contains(KeyValue.newBuilder()
                .setKey("string-key")
                .setValue(AnyValue.newBuilder().setStringValue("new-string-value")
                        .build())
                .build()));
        assertTrue(actual.contains(KeyValue.newBuilder()
                .setKey("bool-key")
                .setValue(AnyValue.newBuilder().setBoolValue(true)
                        .build())
                .build()));
        assertTrue(actual.contains(KeyValue.newBuilder()
                .setKey("long-key")
                .setValue(AnyValue.newBuilder().setIntValue(10L)
                        .build())
                .build()));
        assertTrue(actual.contains(KeyValue.newBuilder()
                .setKey("double-key")
                .setValue(AnyValue.newBuilder().setDoubleValue(1.2)
                        .build())
                .build()));
        assertTrue(actual.contains(KeyValue.newBuilder()
                .setKey("array-key")
                .setValue(
                        AnyValue.newBuilder()
                                .setArrayValue(
                                        ArrayValue.newBuilder()
                                                .addValues(AnyValue.newBuilder().setStringValue("1").build())
                                                .addValues(AnyValue.newBuilder().setStringValue("2").build())
                                                .build())
                                .build())
                .build()));
        assertTrue(actual.contains(KeyValue.newBuilder()
                .setKey("key-value-list-key")
                .setValue(AnyValue.newBuilder()
                        .setKvlistValue(KeyValueList.newBuilder()
                                .addValues(KeyValue.newBuilder()
                                        .setKey("key1")
                                        .setValue(AnyValue.newBuilder().setStringValue("value1").build())
                                        .build())
                                .addValues(KeyValue.newBuilder()
                                        .setKey("key2")
                                        .setValue(AnyValue.newBuilder().setStringValue("value2").build())
                                        .build())
                                .build())
                        .build())
                .build()));

        assertTrue(actual.contains(KeyValue.newBuilder()
                .setKey("byte-array-key")
                .setValue(AnyValue.newBuilder()
                        .setBytesValue(ByteString.copyFrom(new byte[]{0, 1, 2}))
                        .build())
                .build()));
    }

}