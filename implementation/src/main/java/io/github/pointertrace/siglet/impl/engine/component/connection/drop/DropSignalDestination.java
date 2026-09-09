package io.github.pointertrace.siglet.impl.engine.component.connection.drop;

import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.metric.LongCounter;

public class DropSignalDestination implements SignalDestination {

    private final LongCounter droppedSignalsCounter;
    private final DropComponent dropComponent;

    public DropSignalDestination(SigletContext sigletContext) {
        this.droppedSignalsCounter = sigletContext.getMetrics().createDroppedSignalsCounter("DROP");
        this.dropComponent = new DropComponent(sigletContext);
    }

    @Override
    public GraphComponent<?> getComponent() {
        return dropComponent;
    }


    @Override
    public boolean is(String destination) {
        return "DROP".equals(destination);
    }

    @Override
    public SignalReceiverFunction getSignalReceiverFunction() {
        return (signal) -> {
            droppedSignalsCounter.increment();
            return true;
        };
    }


}
