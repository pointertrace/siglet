package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.Component;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.SignalSource;

public interface Processor extends Component<ProcessorNode>, SignalSource, SignalDestination {

}
