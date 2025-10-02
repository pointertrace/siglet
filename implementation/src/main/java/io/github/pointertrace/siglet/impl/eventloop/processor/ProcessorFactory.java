package io.github.pointertrace.siglet.impl.eventloop.processor;

import io.github.pointertrace.siglet.api.Context;

public interface ProcessorFactory<T> {

    BaseEventloopProcessor<T> create(Context<T> context);

}
