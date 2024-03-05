package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableAttributes;

import java.util.List;
import java.util.Map;

public interface ModifiableAttributes extends UnmodifiableAttributes {

    void set(String key, String value);

    void set(String key, boolean value);

    void set(String key, long value);

    void set(String key, double value);

    void set(String key, Object[] value);

    void set(String key, List<? extends Map.Entry<String, Object>> value);

    void set(String key, byte[] value);

    void remove(String key);
}
