package io.github.pointertrace.siglet.api.signal;

/**
 * Represents an event that occurred during the lifetime of a span.
 */
public interface Event {

    /**
     * Returns the name of this event.
     *
     * @return event name.
     */
    String getName();

    /**
     * Sets the name of this event.
     *
     * @param name event name.
     * @return current instance of {@code Event}.
     */
    Event setName(String name);

    /**
     * Returns the time in Unix nanoseconds when this event occurred.
     *
     * @return time in Unix nanoseconds.
     */
    long getTimeUnixNano();

    /**
     * Sets the time in Unix nanoseconds when this event occurred.
     *
     * @param timeUnixNano timestamp in nanoseconds.
     * @return current instance of {@code Event}.
     */
    Event setTimeUnixNano(long timeUnixNano);

    /**
     * Returns the attribute set describing this event.
     *
     * @return attribute set.
     */
    Attributes getAttributes();

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

}
