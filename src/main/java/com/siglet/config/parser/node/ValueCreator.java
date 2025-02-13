package com.siglet.config.parser.node;

import com.siglet.config.item.Item;

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
