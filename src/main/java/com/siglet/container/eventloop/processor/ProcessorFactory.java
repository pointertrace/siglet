package com.siglet.container.eventloop.processor;

import com.siglet.api.ProcessorContext;

public interface ProcessorFactory<T> {

    BaseEventloopProcessor<T> create(ProcessorContext<T> context);

}
