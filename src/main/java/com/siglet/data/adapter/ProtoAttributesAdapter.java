package com.siglet.data.adapter;

import com.siglet.SigletError;
import com.siglet.data.modifiable.ModifiableAttributes;
import io.opentelemetry.proto.common.v1.KeyValue;

import java.util.*;

public class ProtoAttributesAdapter implements ModifiableAttributes {

    private final Map<String, Object> attributesAsMap;

    private final List<KeyValue> attributes;

    private final boolean updatable;

    private boolean updated;

    public ProtoAttributesAdapter(List<KeyValue> attributes, boolean updatable) {
        attributesAsMap = new HashMap<>();
        attributes.forEach((kv) -> attributesAsMap.put(kv.getKey(), AdapterUtils.anyValueToObject(kv.getValue())));
        this.attributes = attributes;
        this.updatable = updatable;
    }

    @Override
    public Map<String, Object> getAsMap() {
        return Collections.unmodifiableMap(attributesAsMap);
    }

    public List<KeyValue> getAsKeyValueList() {
        List<KeyValue> result = new ArrayList<>(attributesAsMap.size());
        attributesAsMap.forEach((key, value) -> result.add(KeyValue.newBuilder()
                .setKey(key)
                .setValue(AdapterUtils.objectToAnyValue(value))
                .build()));
        return result;
    }

    public int size() {
        return attributesAsMap.size();
    }

    @Override
    public boolean has(String key) {
        return attributesAsMap.containsKey(key);
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

    public void set(String key, String value) {
        setAttributeObj(key, value);
    }

    public void set(String key, boolean value) {
        setAttributeObj(key, value);
    }

    public void set(String key, long value) {
        setAttributeObj(key, value);
    }

    public void set(String key, double value) {
        setAttributeObj(key, value);
    }

    public void set(String key, Object[] value) {
        setAttributeObj(key, value);
    }

    public void set(String key, List<? extends Map.Entry<String, Object>> value) {
        setAttributeObj(key, value);
    }

    public void set(String key, byte[] value) {
        setAttributeObj(key, value);
    }

    @Override
    public void remove(String key) {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable attribute list");
        }
        attributesAsMap.remove(key);
        updated = true;
    }

    public void setAttributeObj(String key, Object value) {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable attribute list");
        }
        attributesAsMap.put(key, value);
        updated = true;
    }

    public boolean isUpdated() {
        return updated;
    }

    public List<KeyValue> getUpdated() {
        if (!updatable) {
            return attributes;
        } else if (!updated) {
            return attributes;
        } else {
            return AdapterUtils.mapToKeyValueList(attributesAsMap);
        }
    }
    private boolean isAttributeType(String key, Class<?> type) {
        Object value = attributesAsMap.get(key);
        if (value == null) {
            throw new SigletError("attribute [" + key + "] not found!");
        }
        return type.isAssignableFrom(value.getClass());
    }


    private <T> T getAttributeAs(String key, Class<T> type) {
        Object value = attributesAsMap.get(key);
        if (value == null) {
            throw new SigletError("attribute [" + key + "] not found!");
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new SigletError("attribute [" + key + "] is not " + type.getSimpleName());
        }

        return (T) value;
    }
}
