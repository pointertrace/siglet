package com.siglet.container.engine.receiver;

import com.siglet.container.config.graph.ReceiverNode;
import com.siglet.container.engine.EngineElement;
import com.siglet.container.engine.SignalSource;

public interface Receiver extends EngineElement, SignalSource {

    ReceiverNode getNode();
}
