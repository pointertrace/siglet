package com.siglet.config.parser.node;

import com.siglet.config.item.Item;
import com.siglet.config.parser.locatednode.Located;

import java.util.function.Supplier;

@FunctionalInterface
public interface ValueCreator {

    Item create();

    static <T extends Item> ValueCreator of(Supplier<T> valueCreator) {
        return valueCreator::get;
    }
}
