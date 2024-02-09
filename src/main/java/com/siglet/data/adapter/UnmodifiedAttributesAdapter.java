package com.siglet.data.adapter;

import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnmodifiedAttributesAdapter {

    private final List<KeyValue> protoAttributes;

    private Map<String,Object> attributesAsMap;


    public UnmodifiedAttributesAdapter(List<KeyValue> protoAttributes) {
        this.protoAttributes = protoAttributes;
    }

    public boolean hasKey(String key) {
        if (attributesAsMap == null) {
            attributesAsMap = new HashMap<>();
        }
        return false;
    }

    private void convertAttributesIfNeeded() {
//        if (attributesAsMap == null) {
//            attributesAsMap = new HashMap<>();
//        }
//        for(KeyValue keyValue: protoAttributes) {
//            attributesAsMap.put(keyValue.getKey(), keyValue.getValue().)
//
//        }
//
    }

    private Object anyValueToObject(AnyValue anyValue) {
        return switch (anyValue.getValueCase()) {
            case STRING_VALUE -> anyValue.getStringValue();
            case BOOL_VALUE -> anyValue.getBoolValue();
            case INT_VALUE -> anyValue.getIntValue();
            case DOUBLE_VALUE -> anyValue.getDoubleValue();
            case ARRAY_VALUE -> anyValue.getArrayValue();
            case KVLIST_VALUE -> null;
            case BYTES_VALUE -> anyValue.getBytesValue().toByteArray();
            case VALUE_NOT_SET -> null;
        };
    }








}
