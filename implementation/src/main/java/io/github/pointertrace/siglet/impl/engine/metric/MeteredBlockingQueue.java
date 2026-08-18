package io.github.pointertrace.siglet.impl.engine.metric;


import io.github.pointertrace.siglet.impl.BaseSignal;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MeteredBlockingQueue<E>
        extends AbstractQueue<E>
        implements BlockingQueue<E> {

    private final BlockingQueue<E> delegate;

    private final Timer queueWaitTimer;

    private final Counter received;

    private final Counter accepted;

    private final Counter missed;

    private final Counter dropped;
    /**
     * Maximum queue size observed since the last metric collection.
     * Reset to current size after each scrape to track the next collection period.
     */
    private final AtomicInteger maxSizeSinceLastCollection =
            new AtomicInteger();


    public MeteredBlockingQueue(BlockingQueue<E> delegate, Timer queueWaitTimer, Counter received, Counter accepted,
                                Counter missed, Counter dropped) {

        this.delegate = Objects.requireNonNull(delegate);
        this.queueWaitTimer = Objects.requireNonNull(queueWaitTimer);
        this.received = received;
        this.accepted = accepted;
        this.missed = missed;
        this.dropped = dropped;
    }

    public double getAndResetMaxSize() {
        return maxSizeSinceLastCollection.getAndSet(0);
    }

    private void updateMax() {
        int size = delegate.size();
        maxSizeSinceLastCollection.accumulateAndGet(size, Math::max);
    }

    private void markEnqueuedIfBaseSignal(Object element) {
        if (element instanceof BaseSignal baseSignal) {
            baseSignal.markEnqueued();
        }
    }

    private void recordQueueWaitIfBaseSignal(Object element) {
        if (element instanceof BaseSignal baseSignal) {
            queueWaitTimer.record(baseSignal.getQueuedTimeNanos(), TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public boolean offer(E e) {
        received.increment();
        boolean result = delegate.offer(e);

        if (result) {
            markEnqueuedIfBaseSignal(e);
            updateMax();
            accepted.increment();
        } else {
            dropped.increment();
        }

        return result;
    }

    @Override
    public E poll() {
        E result = delegate.poll();
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
        updateMax();
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
            updateMax();
            markEnqueuedIfBaseSignal(e);
            accepted.increment();
        } else {
            missed.increment();
        }

        return result;
    }

    @Override
    public E take() throws InterruptedException {
        E result = delegate.take();
        recordQueueWaitIfBaseSignal(result);
        return result;
    }

    @Override
    public E poll(long timeout,
                  TimeUnit unit)
            throws InterruptedException {

        E result = delegate.poll(timeout, unit);
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
            updateMax();
            markEnqueuedIfBaseSignal(e);
            accepted.increment();
        } else {
            dropped.increment();
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
            updateMax();
            accepted.increment(c.size());
        } else {
            missed.increment(c.size());
        }

        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = delegate.remove(o);
        if (removed) {
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
        while ((element = delegate.poll()) != null) {
            recordQueueWaitIfBaseSignal(element);
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

        Iterator<E> iterator = delegate.iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();
            if (c.contains(element)) {
                iterator.remove();
                recordQueueWaitIfBaseSignal(element);
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;

        Iterator<E> iterator = delegate.iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();
            if (!c.contains(element)) {
                iterator.remove();
                recordQueueWaitIfBaseSignal(element);
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
