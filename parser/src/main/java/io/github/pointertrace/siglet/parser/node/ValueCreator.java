package io.github.pointertrace.siglet.parser.node;

import java.util.function.Supplier;

@FunctionalInterface
public interface ValueCreator {

    Object create();

    static ValueCreator of(Supplier<? extends Object> valueCreator) {
        if (valueCreator != null) {
            return valueCreator::get;
        } else {
            return null;
        }
    }
}
