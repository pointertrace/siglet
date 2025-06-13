package com.siglet.container.eventloop.processor;




import com.siglet.api.ProcessorContext;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ProcessorContextImpl<T> implements ProcessorContext<T> {

    private final T config;

    private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

    public ProcessorContextImpl(T config) {
        this.config = config;
    }

    @Override
    public synchronized Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public synchronized <E> E getAttribute(String name, Class<E> type) {
        return type.cast(attributes.get(name));
    }

    @Override
    public synchronized Collection<String> getAttributeNames() {
        return Collections.unmodifiableCollection(attributes.keySet());
    }

    @Override
    public synchronized void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public Object mergeAttributeValue(String name, Object value, BiFunction<Object, Object, Object> merger) {
        return attributes.merge(name, value, merger);
    }

    @Override
    public <E> E mergeAttributeValue(String name, E value, BiFunction<E, E, E> merger, Class<E> type) {
        return type.cast(attributes.merge(name, value, (oldValue, newValue) ->
                merger.apply(type.cast(oldValue), type.cast(newValue))));
    }

    @Override
    public T getConfig() {
        return config;
    }
}
