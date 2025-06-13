package com.siglet.processor.aggregator;

import com.siglet.api.Signal;
import com.siglet.container.eventloop.MapSignalDestination;
import com.siglet.container.eventloop.accumulator.TimeoutAccumulatorEventLoop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class TimeoutAccumulatorEventLoopTest {

    private SignalMock signal1;

    private SignalMock signal2;

    private SignalMock signal3;

    private SignalMock signal4;

    private TimeoutAccumulatorEventLoop<SignalMock, AggregatedSignalMock> timeoutAccumulatorEventLoop;

    public AtomicInteger aggregatorIdGenerator;

    private MapSignalDestination<AggregatedSignalMock> destination;

    @BeforeEach
    void setUp() {

        signal1 = new SignalMock(1);

        signal2 = new SignalMock(2);

        signal3 = new SignalMock(3);

        signal4 = new SignalMock(4);

        aggregatorIdGenerator = new AtomicInteger(1);

        destination = new MapSignalDestination<>("final", AggregatedSignalMock.class);

    }


    @Test
    void aggregate_oneByOneMaxSize() {

        AtomicInteger count = new AtomicInteger(1);

        timeoutAccumulatorEventLoop = new TimeoutAccumulatorEventLoop<>("test", 1000, 2, 1,
                AggregatedSignalMock::new);

        timeoutAccumulatorEventLoop.connect(destination);
        timeoutAccumulatorEventLoop.start();

        assertTrue(timeoutAccumulatorEventLoop.send(signal1));
        assertTrue(timeoutAccumulatorEventLoop.send(signal2));

        timeoutAccumulatorEventLoop.stop();

        assertEquals(2, destination.getSize());

        assertTrue(destination.has("1"));
        assertTrue(destination.get("1").has(1));

        assertTrue(destination.has("2"));
        assertTrue(destination.get("2").has(2));


    }
//
//    @Test
//    void aggregate_oneByOneTimeout() throws InterruptedException {
//
//        AtomicInteger count = new AtomicInteger(1);
//
//        timeoutAggregator = new TimeoutAggregator<>("test", 100, 2,
//                10, TimeoutAggregatorTest::aggregate, s -> {
//            List<SignalMock> ns = new ArrayList<>(s);
//            ns.sort(Comparator.comparingInt(a -> a.id));
//            aggregated.put(count.getAndIncrement(), ns);
//        });
//
//        timeoutAggregator.start();
//
//        assertTrue(timeoutAggregator.offer(signal1));
//        Thread.sleep(200);
//
//        assertTrue(timeoutAggregator.offer(signal2));
//        Thread.sleep(200);
//
//        timeoutAggregator.stop();
//
//        assertEquals(2, aggregated.size());
//
//        assertEquals(1, aggregated.get(1).size());
//        assertEquals(1, aggregated.get(1).getFirst().id);
//
//        assertEquals(1, aggregated.get(2).size());
//        assertEquals(2, aggregated.get(2).getFirst().id);
//
//    }
//
//    @Test
//    void aggregate_twoMaxSize() throws InterruptedException {
//
//        AtomicInteger count = new AtomicInteger(1);
//
//        timeoutAggregator = new TimeoutAggregator<>("test", 100, 4,
//                2, TimeoutAggregatorTest::aggregate, s -> {
//            List<SignalMock> ns = new ArrayList<>(s);
//            ns.sort(Comparator.comparingInt(a -> a.id));
//            aggregated.put(count.getAndIncrement(), ns);
//        });
//
//        timeoutAggregator.start();
//
//        assertTrue(timeoutAggregator.offer(signal1));
//        assertTrue(timeoutAggregator.offer(signal2));
//        assertTrue(timeoutAggregator.offer(signal3));
//        assertTrue(timeoutAggregator.offer(signal4));
//        timeoutAggregator.stop();
//
//        assertEquals(2, aggregated.size());
//        System.out.println(aggregated);
//
//        assertEquals(2, aggregated.get(1).size());
//        assertEquals(1, aggregated.get(1).getFirst().id);
//        assertEquals(2, aggregated.get(1).get(1).id);
//
//        assertEquals(2, aggregated.get(2).size());
//        assertEquals(3, aggregated.get(2).getFirst().id);
//        assertEquals(4, aggregated.get(2).get(1).id);
//
//    }
//
//    @Test
//    void aggregate_twoTimeout() throws InterruptedException {
//
//        AtomicInteger count = new AtomicInteger(1);
//
//        timeoutAggregator = new TimeoutAggregator<>("test", 100, 2,
//                5, TimeoutAggregatorTest::aggregate, AggregatedSignalMock::new s -> {
//            List<SignalMock> ns = new ArrayList<>(s);
//            ns.sort(Comparator.comparingInt(a -> a.id));
//            aggregated.put(count.getAndIncrement(), ns);
//        });
//
//        timeoutAggregator.start();
//
//        assertTrue(timeoutAggregator.offer(signal1));
//        Thread.sleep(60);
//
//
//        assertTrue(timeoutAggregator.offer(signal2));
//        Thread.sleep(60);
//
//        assertTrue(timeoutAggregator.offer(signal3));
//        Thread.sleep(60);
//
//        assertTrue(timeoutAggregator.offer(signal4));
//        Thread.sleep(60);
//
//        timeoutAggregator.stop();
//
//        assertEquals(2, aggregated.size());
//
//        assertEquals(2, aggregated.get(1).size());
//        assertEquals(1, aggregated.get(1).getFirst().id);
//        assertEquals(2, aggregated.get(1).get(1).id);
//
//        assertEquals(2, aggregated.get(2).size());
//        assertEquals(3, aggregated.get(2).getFirst().id);
//        assertEquals(4, aggregated.get(2).get(1).id);
//
//    }

    private static class SignalMock implements Signal {

        private final int id;

        private SignalMock(int id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return "" + id;
        }

        @Override
        public String toString() {
            return "SignalMock[" + id + "]";
        }
    }

    private class AggregatedSignalMock implements Signal {

        private final int id;

        private final List<SignalMock> signals;

        private AggregatedSignalMock(List<SignalMock> signals) {
            this.signals = signals;
            this.id = aggregatorIdGenerator.getAndIncrement();
        }

        public boolean has(int signalMockId) {
            return signals.stream()
                    .anyMatch(signalMock -> signalMock.id == signalMockId);
        }

        @Override
        public String getId() {
            return signals.stream()
                    .map(SignalMock::getId)
                    .collect(Collectors.joining(",", "[", "]"));
        }

        @Override
        public String toString() {
            return signals.stream()
                    .map(SignalMock::getId)
                    .collect(Collectors.joining(",", "AggregatedSignalMock[", "]"));
        }

    }

}