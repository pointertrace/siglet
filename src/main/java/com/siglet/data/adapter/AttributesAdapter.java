package com.siglet.data.adapter;

import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.siglet.data.common.ModifiableAttributes;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributesAdapter implements ModifiableAttributes {

    private List<KeyValue> attributes;

    private Map<String, Object> attributesAsMap;

    public AttributesAdapter(List<KeyValue> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAsMap() {
        return null;
    }

    @Override
    public boolean isAttributeString(String key) {
        convertIfNeeded();
        checkIfExists(key);
        return attributesAsMap.get(key) instanceof String;
    }

    @Override
    public boolean isAttributeBoolean(String key) {
        convertIfNeeded();
        checkIfExists(key);
        return attributesAsMap.get(key) instanceof Boolean;
    }

    @Override
    public boolean isAttributeLong(String key) {
        convertIfNeeded();
        checkIfExists(key);
        return attributesAsMap.get(key) instanceof Integer;
    }

    @Override
    public boolean isAttributeDouble(String key) {
        convertIfNeeded();
        checkIfExists(key);
        return attributesAsMap.get(key) instanceof Double;
    }

    @Override
    public boolean isAttributeStringArray(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        return value instanceof Object[] && value.getClass().getComponentType().equals(String.class);
    }

    // TODO checar array
    @Override
    public boolean isAttributeBooleanArray(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        return value instanceof Object[] && value.getClass().getComponentType().equals(Boolean.class);
    }

    // TODO checar array
    @Override
    public boolean isAttributeLongArray(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        return value instanceof Object[] && value.getClass().getComponentType().equals(Long.class);
    }

    // TODO checar array
    @Override
    public boolean isAttributeDoubleArray(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        return value instanceof Object[] && value.getClass().getComponentType().equals(Double.class);
    }

    @Override
    public String getAttributeAsString(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        checkType(key, value, String.class);
        return (String) value;
    }

    @Override
    public boolean getAttributeAsBoolean(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        checkType(key, value, Boolean.class);
        return (Boolean) value;
    }

    @Override
    public long getAttributeAsLong(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        checkType(key, value, Long.class);
        return (Long) value;
    }

    @Override
    public double getAttributeAsDouble(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        checkType(key, value, Double.class);
        return (Double) value;
    }

    @Override
    public String[] getAttributeAsStringArray(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        checkArrayType(key, value, String.class);
        return (String[]) value;
    }

    @Override
    public Boolean[] getAttributeAsBooleanArray(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        checkArrayType(key, value, Boolean.class);
        return (Boolean[]) value;
    }

    @Override
    public Long[] getAttributeAsLongArray(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        checkArrayType(key, value, Boolean.class);
        return (Long[]) value;
    }

    @Override
    public Double[] getAttributeAsDoubleArray(String key) {
        convertIfNeeded();
        checkIfExists(key);
        Object value = attributesAsMap.get(key);
        checkArrayType(key, value, Boolean.class);
        return (Double[]) value;

    }

    private void convertIfNeeded() {
        if (attributesAsMap == null) {
            attributesAsMap = new HashMap<>();
            attributes.forEach((kv) -> attributesAsMap.put(kv.getKey(), AdapterUtils.anyValueToObject(kv.getValue())));
        }
    }

    private void checkIfExists(String key) {
        if (!attributesAsMap.containsKey(key)) {
            throw new IllegalStateException("attribute [" + key + "] not found!");
        }
    }

    private void checkType(String propertyName, Object value, Class<?> type) {
        if (type.isAssignableFrom(value.getClass())) {
            throw new IllegalStateException("property [" + propertyName + "] is not " + type.getSimpleName());
        }
    }

    private void checkArrayType(String propertyName, Object value, Class<?> type) {
        if (!value.getClass().isArray()) {
            throw new IllegalStateException("property [" + propertyName + "] is not array!");
        }
        if (!value.getClass().getComponentType().equals(type)) {
            throw new IllegalStateException("property [" + propertyName + "] is not " + type.getSimpleName());
        }
    }
}
