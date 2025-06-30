package com.siglet.container.adapter;

import com.siglet.SigletError;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.common.v1.KeyValue.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdapterTest {

    KeyValue keyValue;

    Adapter<KeyValue, Builder> adapter;

    @BeforeEach
    public void setUp() {
        keyValue = KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build())
                .build();

        adapter = new Adapter<>();
    }


    @Test
    void getValue_message() {

        adapter.recycle(keyValue, KeyValue::toBuilder, KeyValue.Builder::build);

        assertEquals("key", adapter.getValue(KeyValue::getKey, KeyValue.Builder::getKey));
        assertFalse(adapter.isUpdated());
    }


    @Test
    void getValue_builder() {

        KeyValue.Builder builder = KeyValue.newBuilder().setKey("other-key");
        adapter.recycle(builder, KeyValue.Builder::build);

        assertEquals("other-key", adapter.getValue(KeyValue::getKey, KeyValue.Builder::getKey));
        assertTrue(adapter.isUpdated());
    }

    @Test
    void setAndGetValue_message() {

        adapter.recycle(keyValue, KeyValue::toBuilder, KeyValue.Builder::build);

        adapter.setValue(KeyValue.Builder::setKey, "new-value");
        assertEquals("new-value", adapter.getValue(KeyValue::getKey, KeyValue.Builder::getKey));
        assertTrue(adapter.isUpdated());
    }

    @Test
    void setAndGetValue_builder() {

        KeyValue.Builder builder = KeyValue.newBuilder().setKey("other-key");

        adapter.recycle(builder, KeyValue.Builder::build);

        adapter.setValue(KeyValue.Builder::setKey, "new-value");

        assertEquals("new-value", adapter.getValue(KeyValue::getKey, KeyValue.Builder::getKey));
        assertTrue(adapter.isUpdated());

    }

    @Test
    void getUpdated_messageNoChange() {

        adapter.recycle(keyValue, KeyValue::toBuilder, KeyValue.Builder::build);

        assertSame(keyValue, adapter.getUpdated());

    }

    @Test
    void getUpdated_builderNoChange() {

        KeyValue.Builder builder = KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build());

        adapter.recycle(builder, KeyValue.Builder::build);


        assertEquals(KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build())
                .build(), adapter.getUpdated());
        assertTrue(adapter.isUpdated());

    }

    @Test
    void getUpdated_builderChange() {

        KeyValue.Builder builder = KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build());

        adapter.recycle(builder, KeyValue.Builder::build);
        adapter.setValue(KeyValue.Builder::setKey, "new-key");


        assertEquals(KeyValue.newBuilder()
                .setKey("new-key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build())
                .build(), adapter.getUpdated());
        assertTrue(adapter.isUpdated());

    }

    @Test
    void getUpdated_builderNull() {


        AdapterUpdatedWithoutBuilderChange adapter = new AdapterUpdatedWithoutBuilderChange();
        adapter.recycle(keyValue, KeyValue::toBuilder, KeyValue.Builder::build);
        adapter.updated = true;


        assertEquals(KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build())
                .build(), adapter.getUpdated());

        assertTrue(adapter.isUpdated());

    }

    @Test
    void test_builderPredicate() {

        KeyValue.Builder builder = KeyValue.newBuilder()
                .setKey("key")
                .setValue(AnyValue.newBuilder().setStringValue("value").build());

        adapter.recycle(builder, KeyValue.Builder::build);

        assertTrue(adapter.test(KeyValue::getKey, KeyValue.Builder::getKey, "key"::equals));
        assertFalse(adapter.test(KeyValue::getKey, KeyValue.Builder::getKey, "other-key"::equals));

    }

    @Test
    void test_messagePredicate() {


        adapter.recycle(keyValue, KeyValue::toBuilder, KeyValue.Builder::build);

        assertTrue(adapter.test(KeyValue::getKey, KeyValue.Builder::getKey, "key"::equals));
        assertFalse(adapter.test(KeyValue::getKey, KeyValue.Builder::getKey, "other-key"::equals));

    }

    @Test
    void checkNotReady() {
        assertThrowsExactly(SigletError.class, () -> adapter.getValue(KeyValue::getKey, KeyValue.Builder::getValue));
        assertThrowsExactly(SigletError.class, () -> adapter.setValue(KeyValue.Builder::setKey, "new-value"));
        assertThrowsExactly(SigletError.class, () -> adapter.getUpdated());
        assertThrowsExactly(SigletError.class, () -> adapter.getMessage());
        assertThrowsExactly(SigletError.class, () -> adapter.getBuilder());
        assertThrowsExactly(SigletError.class, () -> adapter.isUpdated());

    }

    @Test
    void checkAfterClear() {
        adapter.recycle(keyValue, KeyValue::toBuilder, KeyValue.Builder::build);

        adapter.getValue(KeyValue::getKey, KeyValue.Builder::getValue);
        adapter.setValue(KeyValue.Builder::setKey, "new-value");
        adapter.getUpdated();
        adapter.getMessage();
        adapter.getBuilder();
        adapter.isUpdated();

        adapter.clear();

        assertThrowsExactly(SigletError.class, () -> adapter.getValue(KeyValue::getKey, KeyValue.Builder::getValue));
        assertThrowsExactly(SigletError.class, () -> adapter.setValue(KeyValue.Builder::setKey, "new-value"));
        assertThrowsExactly(SigletError.class, () -> adapter.getUpdated());
        assertThrowsExactly(SigletError.class, () -> adapter.getMessage());
        assertThrowsExactly(SigletError.class, () -> adapter.getBuilder());
        assertThrowsExactly(SigletError.class, () -> adapter.isUpdated());
    }

    @Test
    void enrich() {

        AdapterWithBuilderEnrich adapter = new AdapterWithBuilderEnrich();
        adapter.recycle(keyValue, KeyValue::toBuilder, KeyValue.Builder::build);

        adapter.setValue(KeyValue.Builder::setKey, "new-value");


        assertEquals(KeyValue.newBuilder()
                .setKey("new-value.suffix")
                .setValue(AnyValue.newBuilder().setStringValue("value").build())
                .build(), adapter.getUpdated());


    }

    public static class AdapterWithBuilderEnrich extends Adapter<KeyValue, KeyValue.Builder> {


        public AdapterWithBuilderEnrich() {
        }

        @Override
        protected void enrich(Builder builder) {
            builder.setKey(builder.getKey() + ".suffix");
        }

    }

    public static class AdapterUpdatedWithoutBuilderChange extends Adapter<KeyValue, KeyValue.Builder> {

        private boolean updated;

        public AdapterUpdatedWithoutBuilderChange() {
        }

        @Override
        public boolean isUpdated() {
            return updated;
        }
    }
}