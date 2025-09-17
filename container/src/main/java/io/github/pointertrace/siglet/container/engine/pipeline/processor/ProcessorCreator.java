package io.github.pointertrace.siglet.container.engine.pipeline.processor;

import io.github.pointertrace.siglet.container.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.container.engine.Context;

public interface ProcessorCreator {

    Processor create(Context context, ProcessorNode processorNode);

}
