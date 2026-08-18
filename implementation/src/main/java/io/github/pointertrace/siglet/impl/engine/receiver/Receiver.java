package io.github.pointertrace.siglet.impl.engine.receiver;

import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSourceProvider;

public interface Receiver extends GraphComponent<ReceiverNode>, SignalSourceProvider {

}
