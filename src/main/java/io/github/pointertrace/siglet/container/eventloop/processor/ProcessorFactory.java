package io.github.pointertrace.siglet.container.eventloop.processor;

import io.github.pointertrace.siglet.api.ProcessorContext;

public interface ProcessorFactory<T> {

    BaseEventloopProcessor<T> create(ProcessorContext<T> context);

}
