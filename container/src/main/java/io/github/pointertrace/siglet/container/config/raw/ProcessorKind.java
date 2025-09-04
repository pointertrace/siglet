package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.container.config.graph.SignalType;

public enum ProcessorKind {

    SPANLET(SignalType.SPAN),

    TRACELET(SignalType.TRACE),

    TRACE_AGGREGATOR(SignalType.SPAN),

    METRICLET(SignalType.METRIC);

    private final SignalType signalType;

    ProcessorKind(SignalType signalType) {
        this.signalType = signalType;
    }

    public SignalType getSignalType() {
        return signalType;
    }


}
