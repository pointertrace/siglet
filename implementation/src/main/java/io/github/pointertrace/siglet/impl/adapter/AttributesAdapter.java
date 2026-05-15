package io.github.pointertrace.siglet.impl.adapter;

import com.google.protobuf.ByteString;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class AttributesAdapter implements Attributes {
    private final List<io.opentelemetry.proto.common.v1.KeyValue> attributes;
    private final Consumer<List<io.opentelemetry.proto.common.v1.KeyValue>> onChange;

    public AttributesAdapter(List<io.opentelemetry.proto.common.v1.KeyValue> attributes) {
        this(attributes, null);
    }

    public AttributesAdapter(List<io.opentelemetry.proto.common.v1.KeyValue> attributes,
                             Consumer<List<io.opentelemetry.proto.common.v1.KeyValue>> onChange) {
        this.attributes = new ArrayList<>(Objects.requireNonNull(attributes, "attributes"));
        this.onChange = onChange;
    }

    private io.opentelemetry.proto.common.v1.AnyValue requireValue(String key) {
        return find(key).orElseThrow(() -> new SigletError("Attribute not found: " + key)).getValue();
    }

    private java.util.Optional<io.opentelemetry.proto.common.v1.KeyValue> find(String key) {
        return attributes.stream().filter(kv -> kv.getKey().equals(key)).findFirst();
    }

    public List<io.opentelemetry.proto.common.v1.KeyValue> getUpdated() {
        return new ArrayList<>(attributes);
    }

    private void changed() {
        if (onChange != null) {
            onChange.accept(new ArrayList<>(attributes));
        }
    }

    private void setAny(String key, io.opentelemetry.proto.common.v1.AnyValue value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        int index = -1;
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getKey().equals(key)) {
                index = i;
                break;
            }
        }
        io.opentelemetry.proto.common.v1.KeyValue kv = io.opentelemetry.proto.common.v1.KeyValue.newBuilder()
            .setKey(key)
            .setValue(value)
            .build();
        if (index >= 0) {
            attributes.set(index, kv);
        } else {
            attributes.add(kv);
        }
        changed();
    }

    @Override
    public boolean containsKey(String key) {
        return find(key).isPresent();
    }

    @Override
    public boolean isString(String key) {
        return requireValue(key).hasStringValue();
    }

    @Override
    public boolean isBoolean(String key) {
        return requireValue(key).hasBoolValue();
    }

    @Override
    public boolean isLong(String key) {
        return requireValue(key).hasIntValue();
    }

    @Override
    public boolean isDouble(String key) {
        return requireValue(key).hasDoubleValue();
    }

    @Override
    public boolean isArray(String key) {
        return requireValue(key).hasArrayValue();
    }

    @Override
    public boolean isKeyValueList(String key) {
        return requireValue(key).hasKvlistValue();
    }

    @Override
    public boolean isByteArray(String key) {
        return requireValue(key).hasBytesValue();
    }

    @Override
    public String getAsString(String key) {
        io.opentelemetry.proto.common.v1.AnyValue value = requireValue(key);
        if (!value.hasStringValue()) {
            throw new SigletError("Attribute is not a string: " + key);
        }
        return value.getStringValue();
    }

    @Override
    public boolean getAsBoolean(String key) {
        io.opentelemetry.proto.common.v1.AnyValue value = requireValue(key);
        if (!value.hasBoolValue()) {
            throw new SigletError("Attribute is not a boolean: " + key);
        }
        return value.getBoolValue();
    }

    @Override
    public long getAsLong(String key) {
        io.opentelemetry.proto.common.v1.AnyValue value = requireValue(key);
        if (!value.hasIntValue()) {
            throw new SigletError("Attribute is not a long: " + key);
        }
        return value.getIntValue();
    }

    @Override
    public double getAsDouble(String key) {
        io.opentelemetry.proto.common.v1.AnyValue value = requireValue(key);
        if (!value.hasDoubleValue()) {
            throw new SigletError("Attribute is not a double: " + key);
        }
        return value.getDoubleValue();
    }

    @Override
    public Object[] getAsArray(String key) {
        io.opentelemetry.proto.common.v1.AnyValue value = requireValue(key);
        if (!value.hasArrayValue()) {
            throw new SigletError("Attribute is not an array: " + key);
        }
        return (Object[]) AdapterUtils.anyValueToObject(value);
    }

    @Override
    public List<Map.Entry<String, Object>> getAsKeyValueList(String key) {
        io.opentelemetry.proto.common.v1.AnyValue value = requireValue(key);
        if (!value.hasKvlistValue()) {
            throw new SigletError("Attribute is not a key-value list: " + key);
        }
        return (List<Map.Entry<String, Object>>) AdapterUtils.anyValueToObject(value);
    }

    @Override
    public AttributesAdapter set(String key, String value) {
        setAny(key, io.opentelemetry.proto.common.v1.AnyValue.newBuilder().setStringValue(Objects.requireNonNull(value, "value")).build());
        return this;
    }

    @Override
    public AttributesAdapter set(String key, boolean value) {
        setAny(key, io.opentelemetry.proto.common.v1.AnyValue.newBuilder().setBoolValue(value).build());
        return this;
    }

    @Override
    public AttributesAdapter set(String key, long value) {
        setAny(key, io.opentelemetry.proto.common.v1.AnyValue.newBuilder().setIntValue(value).build());
        return this;
    }

    @Override
    public AttributesAdapter set(String key, double value) {
        setAny(key, io.opentelemetry.proto.common.v1.AnyValue.newBuilder().setDoubleValue(value).build());
        return this;
    }

    @Override
    public AttributesAdapter set(String key, Object[] value) {
        Objects.requireNonNull(value, "value");
        io.opentelemetry.proto.common.v1.ArrayValue.Builder array = io.opentelemetry.proto.common.v1.ArrayValue.newBuilder();
        for (Object item : value) {
            array.addValues(ProtoUtil.toAnyValue(item));
        }
        setAny(key, io.opentelemetry.proto.common.v1.AnyValue.newBuilder().setArrayValue(array).build());
        return this;
    }

    @Override
    public AttributesAdapter set(String key, List<? extends Map.Entry<String, Object>> value) {
        Objects.requireNonNull(value, "value");
        io.opentelemetry.proto.common.v1.KeyValueList.Builder list = io.opentelemetry.proto.common.v1.KeyValueList.newBuilder();
        for (Map.Entry<String, Object> entry : value) {
            list.addValues(io.opentelemetry.proto.common.v1.KeyValue.newBuilder()
                .setKey(entry.getKey())
                .setValue(ProtoUtil.toAnyValue(entry.getValue())));
        }
        setAny(key, io.opentelemetry.proto.common.v1.AnyValue.newBuilder().setKvlistValue(list).build());
        return this;
    }

    @Override
    public AttributesAdapter set(String key, byte[] value) {
        setAny(key, io.opentelemetry.proto.common.v1.AnyValue.newBuilder().setBytesValue(ByteString.copyFrom(Objects.requireNonNull(value, "value"))).build());
        return this;
    }

    @Override
    public AttributesAdapter remove(String key) {
        boolean removed = attributes.removeIf(kv -> kv.getKey().equals(key));
        if (!removed) {
            throw new SigletError("Attribute not found: " + key);
        }
        changed();
        return this;
    }

    public AttributesAdapter minus(String key) {
        remove(key);
        return this;
    }

    public int getSize() {
        return attributes.size();
    }

    public void putAt(String key, Object value) {
        if (value == null) {
            throw new SigletError("Value cannot be null for key: " + key);
        }
        setAny(key, AdapterUtils.objectToAnyValue(value));
    }

    public Object getAt(String key) {
        return find(key).map(kv -> AdapterUtils.anyValueToObject(kv.getValue())).orElse(null);
    }

    public boolean isCase(String key) {
        return find(key).isPresent();
    }

 
}
