package io.github.pointertrace.siglet.impl.adapter;

import io.github.pointertrace.siglet.api.SigletError;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AttributesAdapterTest {

    @Test
    void shouldRejectNullAttributes() {
        assertThrows(NullPointerException.class, () -> new AttributesAdapter(null));
    }

    @Test
    void shouldSetAndGetStringAttribute() {
        AtomicReference<List<io.opentelemetry.proto.common.v1.KeyValue>> captured = new AtomicReference<>();
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList(), captured::set);

        adapter.set("key", "value");

        assertTrue(adapter.containsKey("key"));
        assertTrue(adapter.isString("key"));
        assertEquals("value", adapter.getAsString("key"));
        assertNotNull(captured.get());
    }

    @Test
    void shouldSetAndGetBooleanAttribute() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        adapter.set("flag", true);

        assertTrue(adapter.containsKey("flag"));
        assertTrue(adapter.isBoolean("flag"));
        assertTrue(adapter.getAsBoolean("flag"));
    }

    @Test
    void shouldSetAndGetLongAttribute() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        adapter.set("count", 42L);

        assertTrue(adapter.isLong("count"));
        assertEquals(42L, adapter.getAsLong("count"));
    }

    @Test
    void shouldSetAndGetDoubleAttribute() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        adapter.set("ratio", 3.14);

        assertTrue(adapter.isDouble("ratio"));
        assertEquals(3.14, adapter.getAsDouble("ratio"));
    }

    @Test
    void shouldSetAndGetArrayAttribute() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        adapter.set("arr", new Object[]{"a", "b"});

        assertTrue(adapter.isArray("arr"));
        Object[] result = adapter.getAsArray("arr");
        assertEquals(2, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
    }

    @Test
    void shouldSetAndGetKeyValueListAttribute() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        List<Map.Entry<String, Object>> kvList = new ArrayList<>();
        kvList.add(new AbstractMap.SimpleImmutableEntry<>("k1", "v1"));
        adapter.set("kvl", kvList);

        assertTrue(adapter.isKeyValueList("kvl"));
        List<Map.Entry<String, Object>> result = adapter.getAsKeyValueList("kvl");
        assertEquals(1, result.size());
        assertEquals("k1", result.get(0).getKey());
        assertEquals("v1", result.get(0).getValue());
    }

    @Test
    void shouldSetAndGetByteArrayAttribute() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        adapter.set("bytes", new byte[]{1, 2, 3});

        assertTrue(adapter.isByteArray("bytes"));
    }

    @Test
    void shouldOverwriteExistingAttribute() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        adapter.set("key", "v1");
        adapter.set("key", "v2");

        assertEquals("v2", adapter.getAsString("key"));
    }

    @Test
    void shouldRemoveAttribute() {
        List<io.opentelemetry.proto.common.v1.KeyValue> initial = Collections.singletonList(
            io.opentelemetry.proto.common.v1.KeyValue.newBuilder()
                .setKey("k")
                .setValue(io.opentelemetry.proto.common.v1.AnyValue.newBuilder().setStringValue("v"))
                .build());
        AttributesAdapter adapter = new AttributesAdapter(initial);

        assertTrue(adapter.containsKey("k"));
        adapter.remove("k");
        assertFalse(adapter.containsKey("k"));
    }

    @Test
    void shouldThrowOnRemoveNonExistent() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        assertThrows(SigletError.class, () -> adapter.remove("missing"));
    }

    @Test
    void shouldThrowOnGetNonExistentKey() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        assertThrows(SigletError.class, () -> adapter.getAsString("missing"));
    }

    @Test
    void shouldThrowOnTypeMismatch() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        adapter.set("num", 42L);
        assertThrows(SigletError.class, () -> adapter.getAsString("num"));
    }

    @Test
    void shouldPutAtCorrectValue() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        adapter.putAt("string", "value");
        adapter.putAt("bool", true);
        adapter.putAt("long", 42L);
        adapter.putAt("int", 100);
        adapter.putAt("double", 3.14);
        adapter.putAt("float", 1.2f);
        adapter.putAt("bytes", new byte[]{1, 2, 3});
        adapter.putAt("array", new Object[]{"a", 1L});
        
        List<Map.Entry<String, Object>> kvList = new ArrayList<>();
        kvList.add(new AbstractMap.SimpleImmutableEntry<>("k1", "v1"));
        adapter.putAt("kvl", kvList);

        assertEquals("value", adapter.getAt("string"));
        assertEquals(true, adapter.getAt("bool"));
        assertEquals(42L, adapter.getAt("long"));
        assertEquals(100L, adapter.getAt("int"));
        assertEquals(3.14, adapter.getAt("double"));
        assertEquals(1.2, (Double) adapter.getAt("float"), 0.001);
        assertArrayEquals(new byte[]{1, 2, 3}, (byte[]) adapter.getAt("bytes"));
        assertArrayEquals(new Object[]{"a", 1L}, (Object[]) adapter.getAt("array"));
        
        List<Map.Entry<String, Object>> resultKvl = (List<Map.Entry<String, Object>>) adapter.getAt("kvl");
        assertEquals(1, resultKvl.size());
        assertEquals("k1", resultKvl.get(0).getKey());
    }

    @Test
    void shouldThrowOnPutAtNull() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        assertThrows(SigletError.class, () -> adapter.putAt("key", null));
    }

    @Test
    void shouldGetAtCorrectValue() {
        AttributesAdapter adapter = new AttributesAdapter(Collections.emptyList());
        adapter.set("string", "value");
        adapter.set("bool", true);
        adapter.set("long", 42L);
        adapter.set("double", 3.14);
        adapter.set("bytes", new byte[]{1, 2, 3});
        
        List<Map.Entry<String, Object>> kvList = new ArrayList<>();
        kvList.add(new AbstractMap.SimpleImmutableEntry<>("k1", "v1"));
        adapter.set("kvl", kvList);

        assertEquals("value", adapter.getAt("string"));
        assertEquals(true, adapter.getAt("bool"));
        assertEquals(42L, adapter.getAt("long"));
        assertEquals(3.14, adapter.getAt("double"));
        assertArrayEquals(new byte[]{1, 2, 3}, (byte[]) adapter.getAt("bytes"));
        
        List<Map.Entry<String, Object>> resultKvl = (List<Map.Entry<String, Object>>) adapter.getAt("kvl");
        assertEquals(1, resultKvl.size());
        assertEquals("k1", resultKvl.get(0).getKey());

        assertNull(adapter.getAt("missing"));
    }
}

