package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Buffer {

    private final int capacity;

    private int index;

    private final Signal[] signals;

    private final Function<Signal[], Signal> accumulator;

    private final List<SignalDestination> destinations;

    private final Deadline deadline;

    public Buffer(int capacity, Function<Signal[], Signal> accumulator, List<SignalDestination> destinations, Deadline deadline) {
        this.capacity = capacity;
        this.index = 0;
        this.signals = new Signal[capacity];
        this.accumulator = accumulator;
        this.destinations = destinations;
        this.deadline = deadline;
    }

    public void add(Signal signal) {
        signals[index++] = signal;
        if (index == 1) {
            deadline.start();
        }
        if (index == capacity || deadline.isExpired()) {
            aggregateAndDispatch();
        }
    }

    public long remainingNanos() {
        return deadline.remainingNanos();
    }

    public boolean hasActiveDeadline() {
        return deadline.isActive();
    }

    public void flush() {
        if (index > 0) {
            aggregateAndDispatch();
        }
    }

    private void aggregateAndDispatch() {
        Signal aggregatedSignal = accumulator.apply(Arrays.copyOf(signals, index));
        for (SignalDestination destination : destinations) {
            destination.send(aggregatedSignal);
        }
        index = 0;
        deadline.reset();
    }

}