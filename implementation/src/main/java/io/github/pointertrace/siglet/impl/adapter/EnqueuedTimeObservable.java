package io.github.pointertrace.siglet.impl.adapter;

public interface EnqueuedTimeObservable {

    void markEnqueued();

    long getQueuedTimeNanos();

}
