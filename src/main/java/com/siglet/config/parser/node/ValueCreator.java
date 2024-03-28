package com.siglet.config.parser.node;

import java.util.function.Supplier;

@FunctionalInterface
public interface ValueCreator {

    Object create();

    static <T> ValueCreator of(Supplier<T> valueCreator) {
        return valueCreator::get;
    }
}
