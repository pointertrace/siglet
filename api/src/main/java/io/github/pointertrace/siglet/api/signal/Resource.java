package io.github.pointertrace.siglet.api.signal;

import io.github.pointertrace.siglet.api.Signal;

/**
 * Defines a Resource associated with a {@link Signal}.
 */
public interface Resource {

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
     * @return current instance of {@code Resource}.
     */
    Resource setDroppedAttributesCount(int droppedAttributesCount);

    /**
     * Returns the attribute list.
     *
     * @return attribute list.
     */
    Attributes getAttributes();
}
