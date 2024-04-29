package com.siglet.config.parser.node;

import com.siglet.config.item.Item;
import com.siglet.config.parser.locatednode.Located;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ValueSetter {

    ValueSetter EMPTY = (Object a, Object b) -> {
    };

    void set(Object obj, Object value);

    static <T, E> ValueSetter of(BiConsumer<T, E> valueSetter) {
        return (Object target, Object value) -> valueSetter.accept((T) target, (E) value);
    }
}
