package io.github.pointertrace.siglet.parser;

public interface NodeChecker {

    void check(Node node) throws SchemaValidationError;

    String describe();

    ValueTransformer getValueTransformer();

    String describe(int ident);
}
