package io.github.pointertrace.siglet.impl.engine.component.connection.drop;

import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestinationProvider;

public class DropSignalDestinationProvider implements SignalDestinationProvider {

    private final SigletContext sigletContext;

    public DropSignalDestinationProvider(SigletContext sigletContext) {
        this.sigletContext = sigletContext;
    }

    @Override
    public String getName() {
        return "DROP";
    }

    @Override
    public SignalDestination getSignalDestination() {
        return new DropSignalDestination(sigletContext);
    }
}
