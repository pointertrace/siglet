package com.siglet.data.unmodifiable;

import java.util.List;
import java.util.Map;

public interface UnmodifiableAttributes {

    Map<String, Object> getAsMap();

    boolean has(String key);

    boolean isString(String key);

    boolean isBoolean(String key);

    boolean isLong(String key);

    boolean isDouble(String key);

    boolean isArray(String key);

    boolean isKeyValueList(String key);

    boolean isByteArray(String key);

    String getAsString(String key);

    boolean getAsBoolean(String key);

    long getAsLong(String key);

    double getAsDouble(String key);

    Object[] getAsArray(String key);

    List<Map.Entry<String,Object>> getAsKeyValueList(String key);

}
