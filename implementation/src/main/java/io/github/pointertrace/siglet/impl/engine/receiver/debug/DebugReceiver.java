package io.github.pointertrace.siglet.impl.engine.receiver.debug;

import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSource;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSourceImpl;
import io.github.pointertrace.siglet.impl.engine.receiver.BaseReceiver;

public class DebugReceiver extends BaseReceiver {

    private SignalEmitterFunction signalEmitter;

    public DebugReceiver(SigletContext sigletContext, ReceiverNode node) {
        super(sigletContext, node);
        DebugReceivers.INSTANCE.add(this);
    }

    @Override
    public void doStart() {
    }

    @Override
    public void doStop() {
    }

    public void receive(Object signal) {
        signalEmitter.emit(signal, SignalDestination.ALL);
    }

    @Override
    public SignalSource getSignalSource() {
        SignalSource signalSource = new SignalSourceImpl(this);
        this.signalEmitter = signalSource.getSignalEmitterFunction();
        return signalSource;
    }
}
