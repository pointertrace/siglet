package io.github.pointertrace.siglet.container.eventloop.processor;

import io.github.pointertrace.siglet.api.ProcessorContext;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

public class ProcessorContextImpl<T> implements ProcessorContext<T> {

    private final T config;

    private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

    public ProcessorContextImpl(T config) {
        this.config = config;
    }


    @Override
    public ConcurrentMap<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public T getConfig() {
        return config;
    }
}
