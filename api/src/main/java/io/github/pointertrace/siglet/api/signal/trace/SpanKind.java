package io.github.pointertrace.siglet.api.signal.trace;


/**
 * Defines the kind of a span, indicating the relationship between the span and its parent.
 */
public enum SpanKind {

    /**
     * Indicates that the span kind is unspecified.
     */
    UNSPECIFIED,

    /**
     * Indicates that the span kind is unrecognized.
     */
    UNRECOGNIZED,

    /**
     * Indicates that the span represents an internal operation.
     */
    INTERNAL,

    /**
     * Indicates that the span represents a server-side operation handling a request.
     */
    SERVER,

    /**
     * Indicates that the span represents a client-side operation making a request.
     */
    CLIENT,

    /**
     * Indicates that the span represents a producer sending a message.
     */
    PRODUCER,

    /**
     * Indicates that the span represents a consumer receiving a message.
     */
    CONSUMER;

}
