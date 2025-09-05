package io.github.pointertrace.siglet.parser;

public interface Parser<T> {

    T parse(Node node);

}
