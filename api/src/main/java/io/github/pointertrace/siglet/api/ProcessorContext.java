package io.github.pointertrace.siglet.api;

import java.util.concurrent.ConcurrentMap;

public interface ProcessorContext<T> {

    ConcurrentMap<String, Object> getAttributes();

    T getConfig();

}
