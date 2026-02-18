package io.github.pointertrace.siglet.api.signal.trace;

/**
 * Defines the status codes for a span.
 */
public enum StatusCode {

    /**
     * Indicates that the span status is unset.
     */
    UNSET,

    /**
     * Indicates that the span completed successfully.
     */
    OK,

    /**
     * Indicates that the span completed with an error.
     */
    ERROR;

}
