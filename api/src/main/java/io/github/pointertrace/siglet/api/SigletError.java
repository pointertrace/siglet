package io.github.pointertrace.siglet.api;

public class SigletError extends RuntimeException {

    public SigletError(String message) {
        super(message);
    }

    public SigletError(String message, Throwable cause) {
        super(message, cause);
    }
}
