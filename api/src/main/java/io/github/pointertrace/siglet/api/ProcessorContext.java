package io.github.pointertrace.siglet.api;

import java.util.Collection;
import java.util.function.BiFunction;

public interface ProcessorContext<T> {

    Object getAttribute(String name);

    <E> E getAttribute(String name, Class<E> type);

    Collection<String> getAttributeNames();

    void setAttribute(String name, Object value);

    Object mergeAttributeValue(String name, Object value, BiFunction<Object, Object, Object> merger);

    <E> E mergeAttributeValue(String name, E value, BiFunction<E, E, E> merger, Class<E> type);

    T getConfig();
}
