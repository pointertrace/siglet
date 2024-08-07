package com.siglet.data.modifiable;

import com.siglet.data.unmodifiable.UnmodifiableAttributes;

import java.util.List;
import java.util.Map;

public interface ModifiableAttributes extends UnmodifiableAttributes {

    ModifiableAttributes set(String key, String value);

    ModifiableAttributes set(String key, boolean value);

    ModifiableAttributes set(String key, long value);

    ModifiableAttributes set(String key, double value);

    ModifiableAttributes  set(String key, Object[] value);

    ModifiableAttributes set(String key, List<? extends Map.Entry<String, Object>> value);

    ModifiableAttributes set(String key, byte[] value);

    ModifiableAttributes remove(String key);
}
