package com.siglet.data.adapter;

import com.siglet.data.common.ModifiableAttributes;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtoAttributesAdapter implements ModifiableAttributes {

    private List<KeyValue> attributes;

    private Map<String, Object> attributesAsMap;

    public ProtoAttributesAdapter(List<KeyValue> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAsMap() {
        convertIfNeeded();
        return Collections.unmodifiableMap(attributesAsMap);
    }

    @Override
    public boolean hasAttribute(String key) {
        convertIfNeeded();
        return attributes.contains(key);
    }

    @Override
    public boolean isAttributeString(String key) {
        return isAttributeType(key, String.class);
    }

    @Override
    public boolean isAttributeBoolean(String key) {
        return isAttributeType(key, Boolean.class);
    }

    @Override
    public boolean isAttributeLong(String key) {
        return isAttributeType(key, Long.class);
    }

    @Override
    public boolean isAttributeDouble(String key) {
        return isAttributeType(key, Double.class);
    }

    @Override
    public boolean isAttributeArray(String key) {
        return isAttributeType(key, Object[].class);
    }

    @Override
    public boolean isAttributeKeyValueList(String key) {
        return isAttributeType(key, Map.Entry.class);
    }

    @Override
    public boolean isAttributeByteArray(String key) {
        return isAttributeType(key, byte[].class);
    }

    @Override
    public String getAttributeAsString(String key) {
        return getAttributeAs(key, String.class);
    }

    @Override
    public boolean getAttributeAsBoolean(String key) {
        return getAttributeAs(key, Boolean.class);
    }

    @Override
    public long getAttributeAsLong(String key) {
        return getAttributeAs(key, Long.class);
    }

    @Override
    public double getAttributeAsDouble(String key) {
        return getAttributeAs(key, Double.class);
    }

    @Override
    public Object[] getAttributeAsArray(String key) {
        return getAttributeAs(key, Object[].class);
    }

    @Override
    public List<Map.Entry<String, Object>> getAttributeAsKeyValueList(String key) {
        return getAttributeAs(key,List.class);
    }

    public void setAttribute(String key, String value) {
        setAttributeObj(key, value);
    }

    public void setAttribute(String key, boolean value) {
        setAttributeObj(key, value);
    }

    public void setAttribute(String key, long value) {
        setAttributeObj(key, value);
    }

    public void setAttribute(String key, double value) {
        setAttributeObj(key, value);
    }

    public void setAttribute(String key, Object[] value) {
        setAttributeObj(key, value);
    }

    public void setAttribute(String key, List<Map.Entry<String, Object>> value) {
        setAttributeObj(key, value);
    }

    public void setAttribute(String key, byte[] value) {
        setAttributeObj(key, value);
    }

    public void setAttributeObj(String key, Object value) {
        convertIfNeeded();
        AnyValue anyValue = AdapterUtils.objectToAnyValue(value);
        KeyValue keyValue = KeyValue.newBuilder()
                .setKey(key)
                .setValue(anyValue)
                .build();

        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getKey().equals(key)) {
                attributes.set(i, keyValue);
                attributesAsMap.put(key, value);
                return;
            }
        }
        attributes.add(keyValue);
        attributesAsMap.put(key, value);
    }


    private void convertIfNeeded() {
        if (attributesAsMap == null) {
            attributesAsMap = new HashMap<>();
            attributes.forEach((kv) -> attributesAsMap.put(kv.getKey(), AdapterUtils.anyValueToObject(kv.getValue())));
        }
    }


    private boolean isAttributeType(String key, Class<?> type) {
        convertIfNeeded();
        Object value = attributesAsMap.get(key);
        if (value == null) {
            throw new IllegalStateException("attribute [" + key + "] not found!");
        }
        return type.isAssignableFrom(value.getClass());
    }


    private <T> T getAttributeAs(String key, Class<T> type) {
        convertIfNeeded();
        Object value = attributesAsMap.get(key);
        if (value == null) {
            throw new IllegalStateException("attribute [" + key + "] not found!");
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalStateException("attribute [" + key + "] is not " + type.getSimpleName());
        }

        return (T) value;
    }
}
