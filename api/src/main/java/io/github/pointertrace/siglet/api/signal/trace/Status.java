package io.github.pointertrace.siglet.api.signal.trace;

/**
 * Represents the status of a span, indicating whether it completed successfully or with an error.
 */
public interface Status {

    /**
     * Returns the status code.
     *
     * @return status code.
     */
    StatusCode getCode();

    /**
     * Sets the status code.
     *
     * @param code status code.
     * @return current instance of {@code Status}.
     */
    Status setCode(StatusCode code);

    /**
     * Returns the status message.
     *
     * @return status message.
     */
    String getStatusMessage();

    /**
     * Sets the status message.
     *
     * @param statusMessage status message.
     * @return current instance of {@code Status}.
     */
    Status setStatusMessage(String statusMessage);

}
