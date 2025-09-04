package io.github.pointertrace.siglet.container.engine.pipeline.processor;

import io.github.pointertrace.siglet.container.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.engine.EngineElement;
import io.github.pointertrace.siglet.container.engine.SignalDestination;
import io.github.pointertrace.siglet.container.engine.SignalSource;

public interface Processor extends EngineElement, SignalSource, SignalDestination {

    ProcessorNode getNode();

    SignalType getSignalType();

}
