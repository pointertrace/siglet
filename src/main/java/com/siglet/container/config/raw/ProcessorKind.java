package com.siglet.container.config.raw;

public enum ProcessorKind {
    SPANLET(SignalType.TRACE),
    TRACELET(SignalType.TRACE),
    TRACE_AGGREGATOR(SignalType.TRACE),
    METRICLET(SignalType.METRIC);

    private final SignalType signalType;

    private ProcessorKind(SignalType signalType) {
        this.signalType = signalType;
    }

    public SignalType getSignal() {
        return signalType;
    }


}
