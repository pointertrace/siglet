package io.github.pointertrace.siglet.impl.eventloop.accumulator;

public interface Deadline {

    void start();

    void reset();

    boolean isExpired();

    boolean isActive();

    long remainingNanos();

    static Deadline of(long durationInMillis) {
        return new DeadlineImpl(durationInMillis);
    }
}


class DeadlineImpl implements Deadline {

    private long deadline = -1;

    private final long durationInMillis;

    public DeadlineImpl(long durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    public void start() {
        deadline = System.nanoTime() + durationInMillis * 1_000_000L;
    }

    public void reset() {
        deadline = -1;
    }

    public boolean isActive() {
        return deadline > 0;
    }

    public boolean isExpired() {
        return deadline > 0 && System.nanoTime() >= deadline;
    }

    public long remainingNanos() {
        return deadline - System.nanoTime();
    }
}
