package io.github.pointertrace.siglet.impl.eventloop;

public class EventLoopError extends RuntimeException {
    public EventLoopError(String message) {
        super(message);
    }

    public EventLoopError(String message, Throwable cause) {
       super(message, cause);
    }
}
