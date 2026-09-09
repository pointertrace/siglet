package io.github.pointertrace.siglet.impl.eventloop;

import io.github.pointertrace.siglet.impl.adapter.EnqueuedTimeObservable;
import io.github.pointertrace.siglet.impl.engine.metric.LongCounter;
import io.github.pointertrace.siglet.impl.engine.metric.LongTimer;
import io.github.pointertrace.siglet.impl.engine.metric.MeteredBlockingQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class MeteredBlockingQueueSignalMetricsTest {

    public static final int CAPACITY = 100;
    private MeteredBlockingQueue<Object> queue;
    private LongTimerSpy queueWaitTimer;
    private CounterSpy receivedCounter;
    private CounterSpy acceptedCounter;
    private CounterSpy missedCounter;
    private CounterFromSupplierProviderSpy queueSizeCounter;
    private CounterFromSupplierProviderSpy queueCapacityCounter;
    private CounterFromSupplierProviderSpy queueSizeMaxCounter;

    @BeforeEach
    void setUp() {
        queueWaitTimer = new LongTimerSpy();
        receivedCounter = new CounterSpy();
        acceptedCounter = new CounterSpy();
        missedCounter = new CounterSpy();
        queue = new MeteredBlockingQueue<>(
                new LinkedBlockingQueue<>(CAPACITY),
                queueWaitTimer,
                receivedCounter,
                acceptedCounter,
                missedCounter
        );
        queueSizeCounter = new CounterFromSupplierProviderSpy(() -> (long) queue.size());
        queueCapacityCounter = new CounterFromSupplierProviderSpy(() -> (long) (queue.size() + queue.remainingCapacity()));
        queueSizeMaxCounter = new CounterFromSupplierProviderSpy(() -> (long) queue.getAndResetMaxSize());
    }

    @Test
    void enqueueMethods_markEnqueuedForBaseSignal() throws InterruptedException {
        TestSignal addSignal = new TestSignal("add", 10);
        TestSignal offerSignal = new TestSignal("offer", 11);
        TestSignal offerTimeoutSignal = new TestSignal("offer-timeout", 12);
        TestSignal putSignal = new TestSignal("put", 13);
        TestSignal addAllSignal1 = new TestSignal("add-all-1", 14);
        TestSignal addAllSignal2 = new TestSignal("add-all-2", 15);

        assertTrue(queue.add(addSignal));
        assertTrue(queue.offer(offerSignal));
        assertTrue(queue.offer(offerTimeoutSignal, 1, TimeUnit.SECONDS));
        queue.put(putSignal);
        assertTrue(queue.addAll(List.of(addAllSignal1, addAllSignal2)));

        assertEquals(1, addSignal.enqueuedMarks);
        assertEquals(1, offerSignal.enqueuedMarks);
        assertEquals(1, offerTimeoutSignal.enqueuedMarks);
        assertEquals(1, putSignal.enqueuedMarks);
        assertEquals(1, addAllSignal1.enqueuedMarks);
        assertEquals(1, addAllSignal2.enqueuedMarks);

        assertEquals(6.0, receivedCounter.getValue());
        assertEquals(6.0, acceptedCounter.getValue());
        assertEquals(0.0, missedCounter.getValue());
    }

    @Test
    void enqueueFailureMethods_incrementMissedAndDroppedCounters() throws InterruptedException {
        queue = new MeteredBlockingQueue<>(
                new LinkedBlockingQueue<>(1),
                queueWaitTimer,
                receivedCounter,
                acceptedCounter,
                missedCounter
        );

        assertTrue(queue.offer(new TestSignal("first", 10)));
        assertFalse(queue.offer(new TestSignal("missed", 12), 1, TimeUnit.MILLISECONDS));

        assertEquals(2.0, receivedCounter.getValue());
        assertEquals(1.0, acceptedCounter.getValue());
        assertEquals(1.0, missedCounter.getValue());
    }

    @Test
    void removalMethods_recordQueueWaitForBaseSignal() throws InterruptedException {
        TestSignal pollSignal = new TestSignal("poll", 100);
        TestSignal takeSignal = new TestSignal("take", 200);
        TestSignal pollTimeoutSignal = new TestSignal("poll-timeout", 300);
        TestSignal removeSignal = new TestSignal("remove", 400);

        queue.add(pollSignal);
        queue.add(takeSignal);
        queue.add(pollTimeoutSignal);
        queue.add(removeSignal);

        assertEquals(pollSignal, queue.poll());
        assertEquals(takeSignal, queue.take());
        assertEquals(pollTimeoutSignal, queue.poll(1, TimeUnit.SECONDS));
        assertTrue(queue.remove(removeSignal));

        assertEquals(4, queueWaitTimer.getCount());
        assertEquals(1000.0, queueWaitTimer.getTotalTime(TimeUnit.NANOSECONDS));
    }

    @Test
    void drainTo_recordsQueueWaitForEachRemovedSignal() {
        TestSignal signal1 = new TestSignal("drain-1", 500);
        TestSignal signal2 = new TestSignal("drain-2", 700);
        queue.add(signal1);
        queue.add(signal2);

        List<Object> drained = new ArrayList<>();
        int drainedCount = queue.drainTo(drained);

        assertEquals(2, drainedCount);
        assertEquals(2, drained.size());
        assertEquals(2L, queueWaitTimer.getCount());
        assertEquals(1200.0, queueWaitTimer.getTotalTime(TimeUnit.NANOSECONDS));
    }

    private static final class TestSignal implements EnqueuedTimeObservable {

        private final String id;
        private final long queuedTimeNanos;
        private int enqueuedMarks;

        private TestSignal(String id, long queuedTimeNanos) {
            this.id = id;
            this.queuedTimeNanos = queuedTimeNanos;
        }

        @Override
        public void markEnqueued() {
            enqueuedMarks++;
        }

        @Override
        public long getQueuedTimeNanos() {
            return queuedTimeNanos;
        }

        public String getId() {
            return id;
        }
    }

    /**
     * Test that max size is tracked correctly when queue grows.
     */
    @Test
    void maxSizeMetric_tracksQueueGrowth() {
        // Add elements to reach a peak
        for (int i = 0; i < 50; i++) {
            queue.add("element-" + i);
        }

        // Verify the gauge value reflects the peak
        assertEquals(50.0, getMaxSizeMetric());
    }

    /**
     * Test that max size is reset after scrape and begins tracking new period.
     */
    @Test
    void maxSizeMetric_resetsAfterCollection() {
        // First period: queue grows to 50
        for (int i = 0; i < 50; i++) {
            queue.add("element-" + i);
        }
        double firstScrape = getMaxSizeMetric();
        assertEquals(50.0, firstScrape, "First scrape should report 50");


        // grows to 90
        for (int i = 0; i < 40; i++) {
            queue.add("new-" + i);
        }

        // Queue shrinks to 60
        for (int i = 0; i < 30; i++) {
            queue.poll();
        }

        // Second period: queue grows from 60 to 80
        for (int i = 0; i < 20; i++) {
            queue.add("element-new-" + i);
        }

        // Second scrape should report 80 (max observed since reset)
        double secondScrape = getMaxSizeMetric();
        assertEquals(80.0, secondScrape, "Second scrape should report 80 (max observed since last scrape)");
    }

    /**
     * Test that max size tracks multiple additions after reset.
     */
    @Test
    void maxSizeMetric_tracksNewPeriodAfterReset() {
        // First period: add 30 items
        for (int i = 0; i < 30; i++) {
            queue.add("item-" + i);
        }

        // Scrape (and reset)
        double firstScrape = getMaxSizeMetric();
        assertEquals(30.0, firstScrape);

        // Remove all items
        queue.clear();

        // Second period: add only 10 items
        for (int i = 0; i < 10; i++) {
            queue.add("new-item-" + i);
        }

        // Second scrape should report 10, not 30
        double secondScrape = getMaxSizeMetric();
        assertEquals(10.0, secondScrape, "Should track new period after reset");
    }

    /**
     * Test that size metric returns current queue size.
     */
    @Test
    void sizeMetric_returnsCurrent() {
        queue.add("item1");
        queue.add("item2");

        assertEquals(2, queue.size());
    }

    /**
     * Test that capacity metric returns total capacity.
     */
    @Test
    void capacityMetric_returnsCapacity() {
        // LinkedBlockingQueue with capacity 100
        queue.add("item");

        // capacity = size + remainingCapacity
        // capacity = 1 + 99 = 100
        assertEquals(CAPACITY, queue.size() + queue.remainingCapacity());
    }

    private double getMaxSizeMetric() {
        return queueSizeMaxCounter.getValue();
    }

    private static class CounterSpy implements LongCounter {

        AtomicInteger counter = new AtomicInteger(0);


        @Override
        public void increment() {
            counter.incrementAndGet();
        }

        @Override
        public void increment(long amount) {
            counter.addAndGet((int) amount);
        }

        public int getValue() {
            return counter.get();
        }
    }

    private static class CounterFromSupplierProviderSpy implements  LongCounter {

        private final Supplier<Long> supplier;

        public CounterFromSupplierProviderSpy(Supplier<Long> supplier) {
            this.supplier = supplier;
        }

        @Override
        public void increment() {
            throw new IllegalStateException("should not be called");
        }

        @Override
        public void increment(long amount) {
            throw new IllegalStateException("should not be called");
        }

        public int getValue() {
            return supplier.get().intValue();
        }
    }

    private static class LongTimerSpy implements LongTimer {

        AtomicInteger count = new AtomicInteger(0);
        AtomicLong duration = new AtomicLong(0);
        AtomicLong totalTimeNanos = new AtomicLong(0);
        TimeUnit timeUnit;

        @Override
        public void record(long duration, TimeUnit unit) {
            this.duration.set(duration);
            this.timeUnit = unit;
            this.count.incrementAndGet();
            this.totalTimeNanos.addAndGet(Duration.ofNanos(unit.toNanos(duration)).toNanos());
        }

        public long getLastValue(TimeUnit timeUnit) {
            return timeUnit.convert(duration.get(), this.timeUnit);
        }

        public long getTotalTime(TimeUnit timeUnit) {
            return timeUnit.convert(totalTimeNanos.get(), this.timeUnit);
        }

        public int getCount() {
            return count.get();
        }
    }
}
