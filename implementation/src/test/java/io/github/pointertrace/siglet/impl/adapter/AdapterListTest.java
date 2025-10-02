package io.github.pointertrace.siglet.impl.adapter;

import io.github.pointertrace.siglet.api.SigletError;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdapterListTest {

    private KeyValues keyValues;

    private List<KeyValue> keyValueList;

    private KeyValue first;

    private KeyValue second;

    @BeforeEach
    void setUp() {

        first = KeyValue.newBuilder()
                .setKey("first")
                .setValue(AnyValue.newBuilder().setStringValue("first-value").build())
                .build();

        second = KeyValue.newBuilder()
                .setKey("second")
                .setValue(AnyValue.newBuilder().setStringValue("second-value").build())
                .build();

        keyValueList = List.of(first, second);

        keyValues = new KeyValues();
        keyValues.recycle(keyValueList);

    }

    @Test
    void clear() {
        keyValues.add();
        keyValues.get(0);
        keyValues.getMessage(0);
        keyValues.getSize();
        keyValues.getUpdated();
        keyValues.find(kv -> "third".equals(kv.getKey()), kvb -> "third".equals(kvb.getKey()));
        keyValues.findIndex(kv -> "third".equals(kv.getKey()), kvb -> "third".equals(kvb.getKey()));
        keyValues.isUpdated();
        keyValues.remove(0);

        keyValues.clear();

        assertThrowsExactly(SigletError.class, () -> keyValues.add());
        assertThrowsExactly(SigletError.class, () -> keyValues.get(0));
        assertThrowsExactly(SigletError.class, () -> keyValues.getMessage(0));
        assertThrowsExactly(SigletError.class, () -> keyValues.getSize());
        assertThrowsExactly(SigletError.class, () -> keyValues.getUpdated());
        assertThrowsExactly(SigletError.class, () -> keyValues.find(kv -> "third".equals(kv.getKey()),
                kvb -> "third".equals(kvb.getKey())));
        assertThrowsExactly(SigletError.class, () -> keyValues.findIndex(kv -> "third".equals(kv.getKey()), kvb ->
                "third".equals(kvb.getKey())));
        assertThrowsExactly(SigletError.class, () -> keyValues.isUpdated());
        assertThrowsExactly(SigletError.class, () -> keyValues.remove(0));

        keyValues.recycle(keyValueList);

        keyValues.add();
        keyValues.get(0);
        keyValues.getMessage(0);
        keyValues.getSize();
        keyValues.getUpdated();
        keyValues.find(kv -> "third".equals(kv.getKey()), kvb -> "third".equals(kvb.getKey()));
        keyValues.findIndex(kv -> "third".equals(kv.getKey()), kvb -> "third".equals(kvb.getKey()));
        keyValues.isUpdated();
        keyValues.remove(0);


    }

    @Test
    void get() {

        KeyValueAdapter firstAdapter = keyValues.get(0);

        assertNotNull(firstAdapter);
        assertSame(first, firstAdapter.getUpdated());

        assertSame(first, keyValues.get(0).getUpdated());
        assertSame(first, keyValues.get(0).getUpdated());

        assertFalse(keyValues.isUpdated());
    }

    @Test
    void add() {

        keyValues.add().setKey("new-key").setValue("new-value");
        assertEquals(3, keyValues.getSize());
        KeyValueAdapter addedAdapter = keyValues.get(2);
        assertEquals("new-key", addedAdapter.getKey());
        assertEquals("new-value", addedAdapter.getValue());
        assertTrue(keyValues.isUpdated());

    }

    @Test
    void remove_index() {

        keyValues.remove(0);

        assertEquals(1, keyValues.getSize());
        KeyValueAdapter adapter = keyValues.get(0);
        assertSame(second, adapter.getUpdated());
        assertEquals("second", adapter.getKey());
        assertEquals("second-value", adapter.getValue());
        assertTrue(keyValues.isUpdated());

    }


    @Test
    void remove_predicate() {

        assertTrue(keyValues.remove(kv -> "second".equals(kv.getKey()), kvb -> "second".equals(kvb.getKey())));

        assertEquals(1, keyValues.getSize());
        KeyValueAdapter adapter = keyValues.get(0);
        assertEquals("first", adapter.getKey());
        assertEquals("first-value", adapter.getValue());
        assertTrue(keyValues.isUpdated());

    }

    @Test
    void find_adapter() {

        KeyValueAdapter adapter = keyValues.find(kv -> "first".equals(kv.getKey()), kvb -> "first".equals(kvb.getKey()));

        assertNotNull(adapter);
        assertEquals("first", adapter.getKey());
        assertEquals("first-value", adapter.getValue());

        assertNull(keyValues.find(kv -> "third".equals(kv.getKey()), kvb -> "third".equals(kvb.getKey())));
        assertFalse(keyValues.isUpdated());
    }

    @Test
    void find_index() {

        assertEquals(1, keyValues.findIndex(kv -> "second".equals(kv.getKey()), kvb -> "second".equals(kvb.getKey())));
        assertEquals(-1, keyValues.findIndex(kv -> "third".equals(kv.getKey()), kvb -> "third".equals(kvb.getKey())));
        assertFalse(keyValues.isUpdated());
    }

    static class KeyValues extends AdapterList<KeyValue, KeyValue.Builder, KeyValueAdapter> {

        protected KeyValues() {
            super(new AdapterListConfig<>(
                    KeyValues::new,
                    KeyValueAdapter::new,
                    KeyValue::newBuilder
            ));
        }
    }

    @Test
    void isUpdated_nothingChange() {

        assertFalse(keyValues.isUpdated());
    }

    @Test
    void isUpdated_get() {

        keyValues.get(0);
        assertFalse(keyValues.isUpdated());
    }

    @Test
    void isUpdated_onlyAdapterChanged() {

        keyValues.get(0).setKey("new-key");
        assertTrue(keyValues.isUpdated());
    }

    @Test
    void getUpdated_notUpdated() {

        List<KeyValue> actual = keyValues.getUpdated();

        assertSame(keyValueList, actual);

    }

    @Test
    void getUpdated_noChanges() {

        List<KeyValue> actual = keyValues.getUpdated();

        assertSame(keyValueList, actual);

    }

    @Test
    void getUpdated_add() {

        keyValues.add().setKey("new-key").setValue("new-value");
        assertNull(keyValues.getMessage(2));
        KeyValueAdapter newAdapter = keyValues.get(2);
        assertNotNull(newAdapter);
        assertEquals("new-key", newAdapter.getKey());
        assertEquals("new-value", newAdapter.getValue());

        List<KeyValue> actual = keyValues.getUpdated();

        assertEquals(3, actual.size());
        assertSame(first, actual.get(0));
        assertSame(second, actual.get(1));

        KeyValue inserted = actual.get(2);
        assertEquals("new-key", inserted.getKey());
        assertEquals("new-value", inserted.getValue().getStringValue());

    }

    @Test
    void getUpdated_remove() {

        keyValues.remove(0);

        List<KeyValue> actual = keyValues.getUpdated();

        assertEquals(1, actual.size());
        assertSame(second, actual.getFirst());

    }

    static class KeyValueAdapter extends Adapter<KeyValue, KeyValue.Builder> {
        public KeyValueAdapter() {
            super(AdapterConfig.KEY_VALUE_ADAPTER_CONFIG);
        }

        public KeyValueAdapter setKey(String key) {
            setValue(KeyValue.Builder::setKey, key);
            return this;
        }

        public KeyValueAdapter setValue(String value) {
            AnyValue strValue = AnyValue.newBuilder().setStringValue(value).build();
            setValue(KeyValue.Builder::setValue, strValue);
            return this;
        }

        public String getKey() {
            return getValue(KeyValue::getKey, KeyValue.Builder::getKey);
        }

        public String getValue() {
            AnyValue value = getValue(KeyValue::getValue, KeyValue.Builder::getValue);
            return value.getStringValue();
        }
    }

}