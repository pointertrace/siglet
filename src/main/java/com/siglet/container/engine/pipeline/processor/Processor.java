package com.siglet.container.engine.pipeline.processor;

import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.engine.EngineElement;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.SignalSource;

public interface Processor extends EngineElement, SignalSource, SignalDestination {

    ProcessorNode getNode();
}
