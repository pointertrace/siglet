package com.siglet.container.adapter.common;

import com.siglet.SigletError;
import com.siglet.api.modifiable.ModifiableAttributes;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterList;
import com.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.common.v1.KeyValue;

import java.util.List;
import java.util.Map;

public class ProtoAttributesAdapter extends AdapterList<KeyValue, KeyValue.Builder,
        ProtoAttributesAdapter.KeyValueAdapter> implements ModifiableAttributes {

    public ProtoAttributesAdapter(List<KeyValue> attributes) {
        super(attributes);
    }

    @Override
    public boolean containsKey(String key) {
        return getAt(key) != null;
    }

    @Override
    public boolean isString(String key) {
        return isAttributeType(key, String.class);
    }

    @Override
    public boolean isBoolean(String key) {
        return isAttributeType(key, Boolean.class);
    }

    @Override
    public boolean isLong(String key) {
        return isAttributeType(key, Long.class);
    }

    @Override
    public boolean isDouble(String key) {
        return isAttributeType(key, Double.class);
    }

    @Override
    public boolean isArray(String key) {
        return isAttributeType(key, Object[].class);
    }

    @Override
    public boolean isKeyValueList(String key) {
        return isAttributeType(key, List.class);
    }

    @Override
    public boolean isByteArray(String key) {
        return isAttributeType(key, byte[].class);
    }

    @Override
    public String getAsString(String key) {
        return getAttributeAs(key, String.class);
    }

    @Override
    public boolean getAsBoolean(String key) {
        return getAttributeAs(key, Boolean.class);
    }

    @Override
    public long getAsLong(String key) {
        return getAttributeAs(key, Long.class);
    }

    @Override
    public double getAsDouble(String key) {
        return getAttributeAs(key, Double.class);
    }

    @Override
    public Object[] getAsArray(String key) {
        return getAttributeAs(key, Object[].class);
    }

    @Override
    public List<Map.Entry<String, Object>> getAsKeyValueList(String key) {
        return getAttributeAs(key, List.class);
    }

    public byte[] getAttributeAsByteArray(String key) {
        return getAttributeAs(key, byte[].class);
    }

    public ProtoAttributesAdapter set(String key, String value) {
        putAt(key, value);
        return this;
    }

    public ProtoAttributesAdapter set(String key, boolean value) {
        putAt(key, value);
        return this;
    }

    public ProtoAttributesAdapter set(String key, long value) {
        putAt(key, value);
        return this;
    }

    public ProtoAttributesAdapter set(String key, double value) {
        putAt(key, value);
        return this;
    }

    public ProtoAttributesAdapter set(String key, Object[] value) {
        putAt(key, value);
        return this;
    }

    public ProtoAttributesAdapter set(String key, List<? extends Map.Entry<String, Object>> value) {
        putAt(key, value);
        return this;
    }

    public ProtoAttributesAdapter set(String key, byte[] value) {
        putAt(key, value);
        return this;
    }

    @Override
    public ProtoAttributesAdapter remove(String key) {
        int idx = findKey(key);
        if (idx < 0) {
            throw new SigletError("Could not find attribute with " + key + " as key");
        } else {
            remove(idx);
        }
        return this;
    }

    public ProtoAttributesAdapter minus(String key) {
        remove(key);
        return this;
    }

    public boolean isCase(String key) {
        return findKey(key) >= 0;
    }

    public void putAt(String key, Object value) {
        int idx = findKey(key);
        if (idx < 0) {
            add().setKey(key).setValue(value);
        } else {
            KeyValueAdapter adapter = getAdapter(idx);
            adapter.setValue(value);
        }
    }

    public Object getAt(String key) {
        int idx = findKey(key);
        if (idx < 0) {
            return null;
        } else {
            return getAdapter(idx).getValue();
        }
    }

    @Override
    protected KeyValueAdapter createNewAdapter() {
        return new KeyValueAdapter(KeyValue.newBuilder());
    }

    @Override
    protected KeyValueAdapter createAdapter(int i) {
        return new KeyValueAdapter(getMessage(i));
    }


    private int findKey(String key) {
        return findIndex(m -> key.equals(m.getKey()), b -> key.equals(b.getKey()));
    }

    private boolean isAttributeType(String key, Class<?> type) {
        Object value = getAt(key);
        if (value == null) {
            throw new SigletError("attribute [" + key + "] not found!");
        }
        return type.isAssignableFrom(value.getClass());
    }


    private <T> T getAttributeAs(String key, Class<T> type) {
        Object value = getAt(key);
        if (value == null) {
            throw new SigletError("attribute [" + key + "] not found!");
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new SigletError("attribute [" + key + "] is not " + type.getSimpleName());
        }

        return (T) value;
    }

    static class KeyValueAdapter extends Adapter<KeyValue, KeyValue.Builder> {
        public KeyValueAdapter(KeyValue message) {
            super(message, KeyValue::toBuilder, KeyValue.Builder::build);
        }

        public KeyValueAdapter(KeyValue.Builder builder) {
            super(builder, KeyValue.Builder::build);
        }

        public String getKey() {
            return getValue(KeyValue::getKey, KeyValue.Builder::getKey);
        }

        public KeyValueAdapter setKey(String key) {
            setValue(KeyValue.Builder::setKey, key);
            return this;
        }

        public Object getValue() {
            return AdapterUtils.anyValueToObject(getValue(KeyValue::getValue, KeyValue.Builder::getValue));
        }

        public KeyValueAdapter setValue(Object value) {
            setValue(KeyValue.Builder::setValue, AdapterUtils.objectToAnyValue(value));
            return this;
        }
    }

}

