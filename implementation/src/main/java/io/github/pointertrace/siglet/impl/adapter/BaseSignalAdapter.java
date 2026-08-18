package io.github.pointertrace.siglet.impl.adapter;

public class BaseSignalAdapter {

    private long enqueuedTimeNanos;

    public void markEnqueued() {
        enqueuedTimeNanos = System.nanoTime();
    }
    public long getQueuedTimeNanos() {
        return System.nanoTime() - enqueuedTimeNanos;
    }
}
