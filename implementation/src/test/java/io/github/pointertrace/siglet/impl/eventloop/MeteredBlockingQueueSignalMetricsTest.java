package io.github.pointertrace.siglet.impl.eventloop;

import io.github.pointertrace.siglet.impl.BaseSignal;
import io.github.pointertrace.siglet.impl.engine.metric.MetricEventListener;
import io.micrometer.core.instrument.Counter;
import io.github.pointertrace.siglet.impl.engine.metric.MeteredBlockingQueue;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static io.github.pointertrace.siglet.impl.engine.metric.MetricEventListener.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeteredBlockingQueueSignalMetricsTest {

    public static final int CAPACITY = 100;
    private MeteredBlockingQueue<Object> queue;
    private MeterRegistry registry;
    private Timer queueWaitTimer;
    private Counter receivedCounter;
    private Counter acceptedCounter;
    private Counter missedCounter;
    private Counter droppedCounter;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        queueWaitTimer = Timer.builder(EVENT_LOOP_QUEUE_WAIT)
                .register(registry);
        receivedCounter = Counter.builder(SIGNALS_RECEIVED).register(registry);
        acceptedCounter = Counter.builder(SIGNALS_ACCEPTED).register(registry);
        missedCounter = Counter.builder(SIGNALS_MISSED).register(registry);
        droppedCounter = Counter.builder(SIGNALS_DROPPED).register(registry);
        queue = new MeteredBlockingQueue<>(
                new LinkedBlockingQueue<>(CAPACITY),
                queueWaitTimer,
                receivedCounter,
                acceptedCounter,
                missedCounter,
                droppedCounter
        );
        Gauge.builder(MetricEventListener.EVENT_LOOP_QUEUE_SIZE,
                        queue,
                        BlockingQueue::size)
                .description("Current queue size")
                .register(registry);

        Gauge.builder(MetricEventListener.EVENT_LOOP_QUEUE_CAPACITY,
                        queue,
                        q -> q.size() + q.remainingCapacity())
                .description("Queue capacity")
                .register(registry);

        Gauge.builder(MetricEventListener.EVENT_LOOP_QUEUE_SIZE_MAX,
                        queue,
                        MeteredBlockingQueue::getAndResetMaxSize)
                .description("Maximum queue size observed since last scrape")
                .register(registry);
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

        assertEquals(6.0, receivedCounter.count());
        assertEquals(6.0, acceptedCounter.count());
        assertEquals(0.0, missedCounter.count());
        assertEquals(0.0, droppedCounter.count());
    }

    @Test
    void enqueueFailureMethods_incrementMissedAndDroppedCounters() throws InterruptedException {
        queue = new MeteredBlockingQueue<>(
                new LinkedBlockingQueue<>(1),
                queueWaitTimer,
                receivedCounter,
                acceptedCounter,
                missedCounter,
                droppedCounter
        );

        assertTrue(queue.offer(new TestSignal("first", 10)));
        assertFalse(queue.offer(new TestSignal("dropped", 11)));
        assertFalse(queue.offer(new TestSignal("missed", 12), 1, TimeUnit.MILLISECONDS));

        assertEquals(3.0, receivedCounter.count());
        assertEquals(1.0, acceptedCounter.count());
        assertEquals(1.0, droppedCounter.count());
        assertEquals(1.0, missedCounter.count());
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

        assertEquals(4L, queueWaitTimer.count());
        assertEquals(1000.0, queueWaitTimer.totalTime(TimeUnit.NANOSECONDS));
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
        assertEquals(2L, queueWaitTimer.count());
        assertEquals(1200.0, queueWaitTimer.totalTime(TimeUnit.NANOSECONDS));
    }

    private static final class TestSignal implements BaseSignal {

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

        @Override
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

        // Queue shrinks to 20
        for (int i = 0; i < 30; i++) {
            queue.poll();
        }

        // Second period: queue grows from 20 to 40
        for (int i = 0; i < 20; i++) {
            queue.add("element-new-" + i);
        }

        // Second scrape should report 40 (max observed since reset)
        double secondScrape = getMaxSizeMetric();
        assertEquals(40.0, secondScrape, "Second scrape should report 40 (max observed since last scrape)");
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
        return registry.find(EVENT_LOOP_QUEUE_SIZE)
                .gauges()
                .stream()
                .findFirst()
                .map(io.micrometer.core.instrument.Gauge::value)
                .orElseThrow(() -> new AssertionError("Gauge not found"));
    }
}

