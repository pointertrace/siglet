package com.siglet.container.engine.pipeline.processor;

import com.siglet.container.config.graph.ProcessorNode;

public interface ProcessorCreator {

    Processor create(ProcessorNode node);

}
