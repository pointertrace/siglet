package io.github.pointertrace.siglet.api.signal;

import io.github.pointertrace.siglet.api.SigletError;

import java.util.List;
import java.util.Map;

/**
 * Represents a collection of key-value attributes associated with a signal.
 */
public interface Attributes {

    /**
     * Checks if the attribute set contains {@code key}.
     *
     * @param key key to be checked.
     * @return {@code true} if the set contains key, {@code false} otherwise.
     */
    boolean containsKey(String key);

    /**
     * Checks if the attribute set contains {@code key} and value is a {@code String}.
     *
     * @param key key to be checked.
     * @return {@code true} if the value is a {@code String}, {@code false} otherwise.
     * @throws SigletError if the set does not contain {@code key}.
     */
    boolean isString(String key);

    /**
     * Checks if the attribute set contains {@code key} and value is a {@code boolean}.
     *
     * @param key key to be checked.
     * @return {@code true} if the value is a {@code boolean}, {@code false} otherwise.
     * @throws SigletError if the set does not contain {@code key}.
     */
    boolean isBoolean(String key);

    /**
     * Checks if the attribute set contains {@code key} and value is a {@code long}.
     *
     * @param key key to be checked.
     * @return {@code true} if the value is a {@code long}, {@code false} otherwise.
     * @throws SigletError if the set does not contain {@code key}.
     */
    boolean isLong(String key);

    /**
     * Checks if the attribute set contains {@code key} and value is a {@code double}.
     *
     * @param key key to be checked.
     * @return {@code true} if the value is a {@code double}, {@code false} otherwise.
     * @throws SigletError if the set does not contain {@code key}.
     */
    boolean isDouble(String key);

    /**
     * Checks if the attribute set contains {@code key} and value is an array.
     *
     * @param key key to be checked.
     * @return {@code true} if the value is an array, {@code false} otherwise.
     * @throws SigletError if the set does not contain {@code key}.
     */
    boolean isArray(String key);

    /**
     * Checks if the attribute set contains {@code key} and value is a {@code KeyValueList}.
     *
     * @param key key to be checked.
     * @return {@code true} if the value is a {@code KeyValueList}, {@code false} otherwise.
     * @throws SigletError if the set does not contain {@code key}.
     */
    boolean isKeyValueList(String key);

    /**
     * Checks if the attribute set contains {@code key} and value is a byte array.
     *
     * @param key key to be checked.
     * @return {@code true} if the value is a byte array, {@code false} otherwise.
     * @throws SigletError if the set does not contain {@code key}.
     */
    boolean isByteArray(String key);

    /**
     * Gets a {@code String} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if the set does not contain {@code key} or it is not a {@code String}.
     */
    String getAsString(String key);

    /**
     * Gets a {@code boolean} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if the set does not contain {@code key} or it is not a {@code boolean}.
     */
    boolean getAsBoolean(String key);

    /**
     * Gets a {@code long} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if the set does not contain {@code key} or it is not a {@code long}.
     */
    long getAsLong(String key);

    /**
     * Gets a {@code double} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if the set does not contain {@code key} or it is not a {@code double}.
     */
    double getAsDouble(String key);

    /**
     * Gets an array attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if the set does not contain {@code key} or it is not an array.
     */
    Object[] getAsArray(String key);

    /**
     * Gets a {@code KeyValueList} attribute.
     *
     * @param key attribute key.
     * @return attribute value.
     * @throws SigletError if the set does not contain {@code key} or it is not a {@code KeyValueList}.
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
     * Sets or substitutes an array attribute.
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
     * Sets or substitutes a byte array attribute.
     *
     * @param key attribute key.
     * @param value attribute value.
     * @return current instance of {@code Attributes}.
     */
    Attributes set(String key, byte[] value);

    /**
     * Removes an attribute by its key.
     *
     * @param key attribute key.
     * @return current instance of {@code Attributes}.
     * @throws SigletError if the attribute does not exist with {@code key}.
     */
    Attributes remove(String key);
}
