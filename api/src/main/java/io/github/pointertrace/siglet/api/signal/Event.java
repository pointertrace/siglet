package io.github.pointertrace.siglet.api.signal;

/**
 * Defines an event
 */
public interface Event {

    /**
     * Returns the event timestamp in nanoseconds.
     *
     * @return event timestamp in nanoseconds.
     */
    long getTimeUnixNano();

    /**
     * Sets the event timestamp in nanoseconds.
     *
     * @param timeUnixNano timestamp in nanoseconds.
     * @return current event
     */
    Event setTimeUnixNano(long timeUnixNano);

    /**
     * Returns event name.
     *
     * @return event name.
     */
    String getName();

    /**
     * Sets the event name.
     *
     * @param name event name.
     * @return current event.
     */
    Event setName(String name);

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
     * @return current instance of {@code Event}.
     */
    Event setDroppedAttributesCount(int droppedAttributesCount);

    /**
     * Returns the attribute list.
     * @return attribute list.
     */
    Attributes getAttributes();

}
