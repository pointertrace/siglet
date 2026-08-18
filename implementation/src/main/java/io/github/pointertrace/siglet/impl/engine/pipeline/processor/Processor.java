package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestinationProvider;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSourceProvider;

public interface Processor extends GraphComponent<ProcessorNode>, SignalSourceProvider, SignalDestinationProvider {
}
