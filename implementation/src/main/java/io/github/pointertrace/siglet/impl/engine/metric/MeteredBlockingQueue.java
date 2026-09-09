package io.github.pointertrace.siglet.impl.engine.metric;


import io.github.pointertrace.siglet.impl.adapter.EnqueuedTimeObservable;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class MeteredBlockingQueue<E>
        extends AbstractQueue<E>
        implements BlockingQueue<E> {

    private final BlockingQueue<E> delegate;

    private final LongTimer queueWaitLongTimer;

    private final LongCounter received;

    private final LongCounter accepted;

    private final LongCounter missed;

    private final LongAdder successfulEnqueues = new LongAdder();

    private final LongAdder successfulDequeues = new LongAdder();

    /**
     * Maximum queue size observed since the last metric collection.
     * Reset to current size after each scrape to track the next collection period.
     */
    private final AtomicLong maxSizeSinceLastCollection =
            new AtomicLong();


    public MeteredBlockingQueue(BlockingQueue<E> delegate, LongTimer queueWaitLongTimer, LongCounter received, LongCounter accepted,
                                LongCounter missed) {

        this.delegate = Objects.requireNonNull(delegate);
        this.queueWaitLongTimer = Objects.requireNonNull(queueWaitLongTimer);
        this.received = received;
        this.accepted = accepted;
        this.missed = missed;

        int initialSize = delegate.size();
        successfulEnqueues.add(initialSize);
        maxSizeSinceLastCollection.set(initialSize);
    }

    public int getAndResetMaxSize() {
        long currentSize = currentSize();
        long observedMax = maxSizeSinceLastCollection.getAndSet(currentSize);
        return clampSizeToInt(observedMax);
    }

    private int clampSizeToInt(long size) {
        if (size <= 0) {
            return 0;
        }
        if (size >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) size;
    }

    private long currentSize() {
        long current = successfulEnqueues.sum() - successfulDequeues.sum();
        return Math.max(0L, current);
    }

    private void updateMax(long candidateSize) {
        long previous = maxSizeSinceLastCollection.get();
        while (candidateSize > previous && !maxSizeSinceLastCollection.compareAndSet(previous, candidateSize)) {
            previous = maxSizeSinceLastCollection.get();
        }
    }

    private void onEnqueueSuccess(int addedCount) {
        if (addedCount <= 0) {
            return;
        }
        successfulEnqueues.add(addedCount);
        updateMax(currentSize());
    }

    private void onDequeueSuccess(int removedCount) {
        if (removedCount <= 0) {
            return;
        }
        successfulDequeues.add(removedCount);
        updateMaxOnDequeue();
    }

    private void updateMaxOnDequeue() {
        long currentSize = currentSize();
        long previous = maxSizeSinceLastCollection.get();
        if (currentSize < previous) {
            maxSizeSinceLastCollection.compareAndSet(previous, currentSize);
        }
    }

    private void markEnqueuedIfBaseSignal(Object element) {
        if (element instanceof EnqueuedTimeObservable enqueuedTimeObservable) {
            enqueuedTimeObservable.markEnqueued();
        }
    }

    private void recordQueueWaitIfBaseSignal(Object element) {
        if (element instanceof EnqueuedTimeObservable enqueuedTimeObservable) {
            queueWaitLongTimer.record(enqueuedTimeObservable.getQueuedTimeNanos(), TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public boolean offer(E e) {
        received.increment();
        boolean result = delegate.offer(e);

        if (result) {
            markEnqueuedIfBaseSignal(e);
            onEnqueueSuccess(1);
            accepted.increment();
        } else {
            missed.increment();
        }

        return result;
    }

    @Override
    public E poll() {
        E result = delegate.poll();
        if (result != null) {
            onDequeueSuccess(1);
        }
        recordQueueWaitIfBaseSignal(result);
        return result;
    }

    @Override
    public E peek() {
        return delegate.peek();
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public void put(E e) throws InterruptedException {
        received.increment();
        delegate.put(e);
        markEnqueuedIfBaseSignal(e);
        onEnqueueSuccess(1);
        accepted.increment();
    }

    @Override
    public boolean offer(E e,
                         long timeout,
                         TimeUnit unit)
            throws InterruptedException {

        received.increment();

        boolean result =
                delegate.offer(e, timeout, unit);

        if (result) {
            markEnqueuedIfBaseSignal(e);
            onEnqueueSuccess(1);
            accepted.increment();
        } else {
            missed.increment();
        }

        return result;
    }

    @Override
    public E take() throws InterruptedException {
        E result = delegate.take();
        onDequeueSuccess(1);
        recordQueueWaitIfBaseSignal(result);
        return result;
    }

    @Override
    public E poll(long timeout,
                  TimeUnit unit)
            throws InterruptedException {

        E result = delegate.poll(timeout, unit);
        if (result != null) {
            onDequeueSuccess(1);
        }
        recordQueueWaitIfBaseSignal(result);
        return result;
    }

    @Override
    public int remainingCapacity() {
        return delegate.remainingCapacity();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(
            Collection<? super E> c,
            int maxElements) {
        Objects.requireNonNull(c);

        if (c == this) {
            throw new IllegalArgumentException("Collection cannot be this queue");
        }
        if (maxElements <= 0) {
            return 0;
        }

        List<E> drained = new ArrayList<>();
        int drainedCount = delegate.drainTo(drained, maxElements);
        if (drainedCount > 0) {
            onDequeueSuccess(drainedCount);
        }
        for (E element : drained) {
            recordQueueWaitIfBaseSignal(element);
            c.add(element);
        }
        return drainedCount;
    }

    @Override
    public boolean add(E e) {
        received.increment();
        boolean result = delegate.add(e);

        if (result) {
            markEnqueuedIfBaseSignal(e);
            onEnqueueSuccess(1);
            accepted.increment();
        } else {
            missed.increment();
        }

        return result;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        received.increment(c.size());
        boolean result = delegate.addAll(c);

        if (result) {
            for (E element : c) {
                markEnqueuedIfBaseSignal(element);
            }
            int addedCount = c.size();
            onEnqueueSuccess(addedCount);
            accepted.increment(addedCount);
        } else {
            missed.increment(c.size());
        }

        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = delegate.remove(o);
        if (removed) {
            onDequeueSuccess(1);
            recordQueueWaitIfBaseSignal(o);
        }
        return removed;
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public void clear() {
        E element;
        int removedCount = 0;
        while ((element = delegate.poll()) != null) {
            removedCount++;
            recordQueueWaitIfBaseSignal(element);
        }
        if (removedCount > 0) {
            onDequeueSuccess(removedCount);
        }
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;

        int removedCount = 0;
        Iterator<E> iterator = delegate.iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();
            if (c.contains(element)) {
                iterator.remove();
                removedCount++;
                recordQueueWaitIfBaseSignal(element);
                modified = true;
            }
        }

        if (removedCount > 0) {
            onDequeueSuccess(removedCount);
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;

        int removedCount = 0;
        Iterator<E> iterator = delegate.iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();
            if (!c.contains(element)) {
                iterator.remove();
                removedCount++;
                recordQueueWaitIfBaseSignal(element);
                modified = true;
            }
        }

        if (removedCount > 0) {
            onDequeueSuccess(removedCount);
        }

        return modified;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
