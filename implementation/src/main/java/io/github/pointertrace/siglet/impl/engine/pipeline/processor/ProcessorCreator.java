package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.Context;

public interface ProcessorCreator {

    Processor create(Context context, ProcessorNode processorNode);

}
