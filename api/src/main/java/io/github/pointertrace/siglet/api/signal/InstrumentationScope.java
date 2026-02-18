package io.github.pointertrace.siglet.api.signal;

import io.github.pointertrace.siglet.api.Signal;

/**
 * Defines the instrumentation scope associated with a {@link Signal}.
 */
public interface InstrumentationScope {

    /**
     * Returns the instrumentation scope name.
     *
     * @return instrumentation scope name.
     */
    String getName();

    /**
     * Sets the instrumentation scope name.
     *
     * @param name instrumentation scope name.
     * @return current instance of {@code InstrumentationScope}.
     */
    InstrumentationScope setName(String name);

    /**
     * Returns the instrumentation scope version.
     *
     * @return instrumentation scope version.
     */
    String getVersion();

    /**
     * Sets the instrumentation scope version.
     *
     * @param version instrumentation scope version.
     * @return current instance of {@code InstrumentationScope}.
     */
    InstrumentationScope setVersion(String version);

    /**
     * Returns the number of dropped attributes.
     *
     * @return number of dropped attributes.
     */
    int getDroppedAttributesCount();

    /**
     * Sets the number of dropped attributes.
     *
     * @param droppedAttributesCount number of dropped attributes.
     * @return current instance of {@code InstrumentationScope}.
     */
    InstrumentationScope setDroppedAttributesCount(int droppedAttributesCount);

    /**
     * Returns the attribute list.
     *
     * @return attribute list.
     */
    Attributes getAttributes();

}
