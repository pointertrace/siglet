package com.siglet.config.parser.node;

import java.util.List;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ValueSetter {

    ValueSetter EMPTY = (Object a, Object b) -> {
    };

    void set(Object obj, Object value);

    static <T, E> ValueSetter of(BiConsumer<T, E> valueSetter) {
        return (Object target, Object value) -> valueSetter.accept((T) target, (E) value);
    }

    static ValueSetter listAdd() {
        return of ((BiConsumer<List<Object>,Object>) List::add);
    }

}
