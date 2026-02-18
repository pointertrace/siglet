package io.github.pointertrace.siglet.api;

/**
 * Exception thrown when a siglet encounters an error during processing.
 */
public class SigletError extends RuntimeException {

    /**
     * Constructs a new SigletError with the specified detail message.
     *
     * @param message the detail message.
     */
    public SigletError(String message) {
        super(message);
    }

    /**
     * Constructs a new SigletError with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause the cause of the exception.
     */
    public SigletError(String message, Throwable cause) {
        super(message, cause);
    }
}
