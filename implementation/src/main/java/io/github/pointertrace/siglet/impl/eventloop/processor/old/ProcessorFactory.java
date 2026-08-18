package io.github.pointertrace.siglet.impl.eventloop.processor.old;

import io.github.pointertrace.siglet.api.Context;

public interface ProcessorFactory<T> {

    BaseProcessor<T> create(Context<T> context);

}
