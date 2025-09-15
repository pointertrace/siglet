package io.github.pointertrace.siglet.api.signal;

import io.github.pointertrace.siglet.api.SigletError;

import java.util.List;
import java.util.Map;

/**
 * Defines an attribute list.
 */
public interface Attributes {


    /**
     * Checks if the attribute list contains {@code key}.
     *
     * @param key to be checked.
     * @return {@code true} if the list contains key and {@code false} if not.
     */
    boolean containsKey(String key);

    /**
     * Checks if the attribute list contains {@code key} and value
     * is a {@code String}.
     *
     * @param key to be checked.
     * @return {@code true} if the value is {@code String} and {@code false} if not.
     * @throws SigletError if list does not contain {@code key}.
     */
    boolean isString(String key);

    /**
     * Checks if the attribute list contains {@code key} and value
     * is a {@code boolean}.
     *
     * @param key to be checked.
     * @return {@code true} if the value is {@code boolean} and {@code false} if not.
     * @throws SigletError if list does not contain {@code key}.
     */
    boolean isBoolean(String key);

    /**
     * Checks if the attribute list contains {@code key} and value
     * is a {@code long}.
     *
     * @param key to be checked.
     * @return {@code true} if the value is {@code long} and {@code false} if not.
     * @throws SigletError if list does not contain {@code key}.
     */
    boolean isLong(String key);

    /**
     * Checks if the attribute list contains {@code key} and value
     * is a {@code double}.
     *
     * @param key to be checked.
     * @return {@code true} if the value is {@code double} and {@code false} if not.
     * @throws SigletError if list does not contain {@code key}.
     */
    boolean isDouble(String key);

    /**
     * Checks if the attribute list contains {@code key} and value
     * is a {@code array}.
     *
     * @param key to be checked.
     * @return {@code true} if the value is {@code array} and {@code false} if not.
     * @throws SigletError if list does not contain {@code key}.
     */
    boolean isArray(String key);

    /**
     * Checks if the attribute list contains {@code key} and value
     * is a {@code KeyValueList}.
     *
     * @param key to be checked.
     * @return {@code true} if the value is {@code KeyValueList} and {@code false} if not.
     * @throws SigletError if list does not contain {@code key}.
     */
    boolean isKeyValueList(String key);

    /**
     * Checks if the attribute list contains {@code key} and value
     * is a {@code ByteArray}.
     *
     * @param key to be checked.
     * @return {@code true} if the value is {@code ByteArray} and {@code false} if not.
     * @throws SigletError if list does not contain {@code key}.
     */
    boolean isByteArray(String key);

    /**
     * Gets a {@code String} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if list does not contain {@code key} or it is not a {@code String}.
     */
    String getAsString(String key);

    /**
     * Gets a {@code boolean} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if list does not contain {@code key} or it is not a {@code boolean}.
     */
    boolean getAsBoolean(String key);

    /**
     * Gets a {@code long} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if list does not contain {@code key} or it is not a {@code long}.
     */
    long getAsLong(String key);

    /**
     * Gets a {@code dobule} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if list does not contain {@code key} or it is not a {@code double}.
     */
    double getAsDouble(String key);

    /**
     * Gets a {@code array} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if list does not contain {@code key} or it
     * is not a {@code array}.
     */
    Object[] getAsArray(String key);

    /**
     * Gets a {@code KeyValueList} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if list does not contain {@code key} or it is not a {@code KeyValueList}.
     */
    List<Map.Entry<String,Object>> getAsKeyValueList(String key);

    /**
     * Sets or substitutes a {@code String} attribute.
     *
     * @param key attribute key.
     * @param value attribute value.
     * @return current instance of {@code Attributes}.
     */
    Attributes set(String key, String value);

    /**
     * Sets or substitutes a {@code boolean} attribute.
     *
     * @param key attribute key.
     * @param value attribute value.
     * @return current instance of {@code Attributes}.
     */
    Attributes set(String key, boolean value);

    /**
     * Sets or substitutes a {@code long} attribute.
     *
     * @param key attribute key.
     * @param value attribute value.
     * @return current instance of {@code Attributes}.
     */
    Attributes set(String key, long value);

    /**
     * Sets or substitutes a {@code double} attribute.
     *
     * @param key attribute key.
     * @param value attribute value.
     * @return current instance of {@code Attributes}.
     */
    Attributes set(String key, double value);

    /**
     * Sets or substitutes a {@code array} attribute.
     *
     * @param key attribute key.
     * @param value attribute value.
     * @return current instance of {@code Attributes}.
     */
    Attributes set(String key, Object[] value);

    /**
     * Sets or substitutes a {@code KeyValueList} attribute.
     *
     * @param key attribute key.
     * @param value attribute value.
     * @return current instance of {@code Attributes}.
     */
    Attributes set(String key, List<? extends Map.Entry<String, Object>> value);

    /**
     * Sets or substitutes a {@code byteArray} attribute.
     *
     * @param key attribute key.
     * @param value attribute value.
     * @return current instance of {@code Attributes}.
     */
    Attributes set(String key, byte[] value);

    /**
     * Remove an attribute by its key.
     *
     * @param key attribute key.
     * @return current instance of {@code Attributes}.
     * @throws SigletError if attribute does not exist with {@code key}.
     */
    Attributes remove(String key);
}
