package io.github.pointertrace.siglet.api.signal;

import java.util.List;
import java.util.Map;

public interface Attributes {

    boolean containsKey(String key);

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

    Attributes set(String key, String value);

    Attributes set(String key, boolean value);

    Attributes set(String key, long value);

    Attributes set(String key, double value);

    Attributes set(String key, Object[] value);

    Attributes set(String key, List<? extends Map.Entry<String, Object>> value);

    Attributes set(String key, byte[] value);

    Attributes remove(String key);
}
