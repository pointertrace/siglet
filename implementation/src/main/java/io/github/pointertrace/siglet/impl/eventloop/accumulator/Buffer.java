package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

public class Buffer<IN, OUT> {

    protected final int capacity;
    protected final IN[] ins;
    protected final Function<IN[], OUT> transformerFunction;
    protected final EmitterFunction<OUT> signalEmitterFunction;
    protected final Deadline deadline;
    protected int index;

    public Buffer(int capacity,Deadline deadline, Class<IN> inType, Function<IN[], OUT> transformerFunction, EmitterFunction<OUT> signalEmitterFunction) {
        this.capacity = capacity;
        this.index = 0;
        this.ins = (IN[]) Array.newInstance(inType, capacity);
        this.transformerFunction = transformerFunction;
        this.signalEmitterFunction = signalEmitterFunction;
        this.deadline = deadline;
    }

    public void add(IN in) {
        ins[index++] = in;
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

    public void aggregateAndDispatch() {
        IN[] copied = Arrays.copyOf(ins, index);
        OUT transformed = transformerFunction.apply(copied);
        signalEmitterFunction.emit(transformed);
        index = 0;
        deadline.reset();
    }

    public int size() {
        return index;
    }

}
