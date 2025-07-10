package com.siglet.container.engine.pipeline.processor;

import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.engine.Context;

public interface ProcessorCreator {

    Processor create(Context context, ProcessorNode node);

}
