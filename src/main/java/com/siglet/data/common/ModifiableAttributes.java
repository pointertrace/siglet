package com.siglet.data.common;

import java.util.List;
import java.util.Map;

public interface ModifiableAttributes extends UnmodifiedAttributes{

    void setAttribute(String key, String value);

    void setAttribute(String key, boolean value);

    void setAttribute(String key, long value);

    void setAttribute(String key, double value);

    void setAttribute(String key, Object[] value);

    void setAttribute(String key, List<Map.Entry<String, Object>> value);

    void setAttribute(String key, byte[] value);
}
