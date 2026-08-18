package io.github.pointertrace.siglet.impl;

import io.github.pointertrace.siglet.api.Signal;

public interface BaseSignal extends Signal {

    void markEnqueued();

    long getQueuedTimeNanos();

}
