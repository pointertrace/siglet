package io.github.pointertrace.siglet.impl.engine.receiver;

import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.EngineElement;
import io.github.pointertrace.siglet.impl.engine.SignalSource;

public interface Receiver extends EngineElement, SignalSource {

    ReceiverNode getNode();
}
