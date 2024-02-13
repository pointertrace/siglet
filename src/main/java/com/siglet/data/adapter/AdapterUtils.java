package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import io.micrometer.core.instrument.Gauge;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.ArrayValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.common.v1.KeyValueList;
import org.checkerframework.checker.units.qual.A;

import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public class AdapterUtils {

    public static byte[] traceId(long high, long low) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.putLong(high);
        buffer.putLong(low);
        return buffer.array();
    }

    public static byte[] spanId(long spanId) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(spanId);
        return buffer.array();
    }

    public static List<KeyValue> mapToKeyValueList(Map<String, Object> attributes) {
        List<KeyValue> result = new ArrayList<>();
        for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
            result.add(KeyValue.newBuilder()
                    .setKey(attribute.getKey())
                    .setValue(objectToAnyValue(attribute.getValue()))
                    .build());
        }
        return result;
    }


    public static Object anyValueToObject(AnyValue anyValue) {
        return switch (anyValue.getValueCase()) {
            case STRING_VALUE -> anyValue.getStringValue();
            case BOOL_VALUE -> anyValue.getBoolValue();
            case INT_VALUE -> anyValue.getIntValue();
            case DOUBLE_VALUE -> anyValue.getDoubleValue();
            case ARRAY_VALUE -> {
                ArrayValue v = anyValue.getArrayValue();
                Object[] result = new Object[v.getValuesCount()];
                for (int i = 0; i < v.getValuesCount(); i++) {
                    result[i] = anyValueToObject(v.getValues(i));
                }
                yield result;
            }
            case KVLIST_VALUE -> {
                KeyValueList kvList = anyValue.getKvlistValue();
                List<Map.Entry<String, Object>> result = new ArrayList<>(kvList.getValuesCount());
                kvList.getValuesList().forEach((kv) -> {
                    result.add(new AbstractMap.SimpleImmutableEntry<>(kv.getKey(), anyValueToObject(kv.getValue())));
                });
                yield result;
            }
            case BYTES_VALUE -> anyValue.getBytesValue().toByteArray();
            case VALUE_NOT_SET -> null;
        };
    }

    public static AnyValue objectToAnyValue(Object value) {
        return switch (value) {
            case String s -> AnyValue.newBuilder().setStringValue(s).build();
            case Boolean b -> AnyValue.newBuilder().setBoolValue(b).build();
            case Long l -> AnyValue.newBuilder().setIntValue(l).build();
            case Double d -> AnyValue.newBuilder().setDoubleValue(d).build();
            case Object[] a -> {
                ArrayValue.Builder avBuilder = ArrayValue.newBuilder();
                for (Object o : a) {
                    avBuilder.addValues(objectToAnyValue(o));
                }
                yield AnyValue.newBuilder().setArrayValue(avBuilder.build()).build();
            }
            case List<?> l -> {
                List<KeyValue> kvList = new ArrayList<>(l.size());
                KeyValueList.Builder kvLstBld = KeyValueList.newBuilder();
                for (Object o : l) {
                    if (o instanceof Map.Entry<?, ?> e) {
                        kvLstBld.addValues(KeyValue.newBuilder()
                                .setKey(e.getKey().toString())
                                .setValue(objectToAnyValue(e.getValue()))
                                .build());
                    } else {
                        throw new IllegalStateException("list item must be a Map.Entry!");
                    }
                }
                yield AnyValue.newBuilder().setKvlistValue(kvLstBld.build()).build();
            }
            case byte[] b -> AnyValue.newBuilder().setBytesValue(ByteString.copyFrom(b)).build();
            case null -> AnyValue.newBuilder().build();
            default ->
                    throw new IllegalStateException(value.getClass().getSimpleName() + "is not a valid attriute type!");
        };
    }

    ;

    public static <T, E> E read(T first, Supplier<E> firstSupplier, T second, Supplier<E> secondSupplier) {
        if (first != null) {
            return firstSupplier.get();
        } else {
            return secondSupplier.get();
        }
    }

    public static <T, E> void write(E builder, Runnable builderCreator, Consumer<T> spanBuilderSetter, T value) {
        if (builder == null) {
            builderCreator.run();
        }
        spanBuilderSetter.accept(value);
    }

}

