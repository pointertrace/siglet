package io.github.pointertrace.siglet.container.engine.receiver;

import io.github.pointertrace.siglet.container.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.container.engine.EngineElement;
import io.github.pointertrace.siglet.container.engine.SignalSource;

public interface Receiver extends EngineElement, SignalSource {

    ReceiverNode getNode();
}
