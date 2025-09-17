package io.github.pointertrace.siglet.api.signal;


/**
 * Defines a Resource associated with { @Link Signal }
 */
public interface Resource {

    /**
     * Returns the number of dropped attributes
     *
     * @return Number of dropped attributes
     */
    int getDroppedAttributesCount();


    /**
     * Sets the number of dropped attributes
     *
     * @param droppedAttributesCount Number of dropped attributes
     * @return current instance of {@code Resource}
     */
    Resource setDroppedAttributesCount(int droppedAttributesCount);

    /**
     * Returns the attribute list
     *
     * @return Attribute list
     */
    Attributes getAttributes();
}
