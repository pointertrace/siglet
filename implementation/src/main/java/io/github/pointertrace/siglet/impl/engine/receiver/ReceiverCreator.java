package io.github.pointertrace.siglet.impl.engine.receiver;

import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.Context;

public interface ReceiverCreator {

    Receiver create(Context context, ReceiverNode receiverNode);
}
