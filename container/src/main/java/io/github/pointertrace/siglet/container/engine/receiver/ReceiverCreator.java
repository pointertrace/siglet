package io.github.pointertrace.siglet.container.engine.receiver;

import io.github.pointertrace.siglet.container.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.container.engine.Context;

public interface ReceiverCreator {

    Receiver create(Context context, ReceiverNode receiverNode);
}
