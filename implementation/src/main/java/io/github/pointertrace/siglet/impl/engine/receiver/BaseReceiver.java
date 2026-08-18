package io.github.pointertrace.siglet.impl.engine.receiver;

import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.BaseGraphComponent;


public abstract class BaseReceiver extends BaseGraphComponent<ReceiverNode> implements Receiver {

    public BaseReceiver(SigletContext sigletContext, ReceiverNode node) {
        super(sigletContext, node);
    }


}