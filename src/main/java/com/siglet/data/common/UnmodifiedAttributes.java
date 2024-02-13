package com.siglet.data.common;

import java.util.List;
import java.util.Map;

public interface UnmodifiedAttributes {

    Map<String, Object> getAsMap();

    boolean hasAttribute(String key);

    boolean isAttributeString(String key);

    boolean isAttributeBoolean(String key);

    boolean isAttributeLong(String key);

    boolean isAttributeDouble(String key);

    boolean isAttributeArray(String key);

    boolean isAttributeKeyValueList(String key);

    boolean isAttributeByteArray(String key);

    String getAttributeAsString(String key);

    boolean getAttributeAsBoolean(String key);

    long getAttributeAsLong(String key);

    double getAttributeAsDouble(String key);

    Object[] getAttributeAsArray(String key);

    List<Map.Entry<String,Object>> getAttributeAsKeyValueList(String key);


}
