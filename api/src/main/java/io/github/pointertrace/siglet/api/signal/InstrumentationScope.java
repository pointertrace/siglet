package io.github.pointertrace.siglet.api.signal;

/**
 * Defines the instrumentation scope associated the { @Link Signal }
 */
public interface InstrumentationScope {

    /**
     * Returns instrumentation scope name.
     *
     * @return instrumentation scope name.
     */
    String getName();

    /**
     * Sets instrumentation scope name.
     *
     * @param name instrumentation scope name.
     * @return current instance of {@code InstrumentationScope}.
     */
    InstrumentationScope setName(String name);

    /**
     * Returns instrumentation scope version.
     *
     * @return instrumentation scope version.
     */
    String getVersion();

    /**
     * Sets instrumentation scope version.
     *
     * @param version Instrumentation scope version.
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
     * Return the attribute list.
     *
     * @return attribute list.
     */
    Attributes getAttributes();

}
