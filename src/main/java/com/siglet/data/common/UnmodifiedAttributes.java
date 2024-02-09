package com.siglet.data.common;

import java.util.Map;

public interface UnmodifiedAttributes {

    Map<String,Object> getAsMap();

    boolean isAttributeString(String key);

    boolean isAttributeBoolean(String key);

    boolean isAttributeLong(String key);

    boolean isAttributeDouble(String key);

    boolean isAttributeStringArray(String key);

    boolean isAttributeBooleanArray(String key);

    boolean isAttributeLongArray(String key);

    boolean isAttributeDoubleArray(String key);

    String getAttributeAsString(String key);

    boolean getAttributeAsBoolean(String key);

    long getAttributeAsLong(String key);

    double getAttributeAsDouble(String key);

    String[] getAttributeAsStringArray(String key);

    Boolean[] getAttributeAsBooleanArray(String key);

    Long[] getAttributeAsLongArray(String key);

    Double[] getAttributeAsDoubleArray(String key);
}
