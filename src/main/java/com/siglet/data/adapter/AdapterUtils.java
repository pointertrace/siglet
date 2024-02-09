package com.siglet.data.adapter;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.ArrayValue;
import io.opentelemetry.proto.common.v1.KeyValueList;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdapterUtils {


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
}
