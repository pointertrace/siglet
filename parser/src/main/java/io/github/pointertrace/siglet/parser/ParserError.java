package io.github.pointertrace.siglet.parser;

public class ParserError extends RuntimeException {
    public ParserError(String message) {
        super(message);
    }
}
