package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.eventloop.EventLoopError;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeoutAccumulatorEventLoopTest {

    @Test
    void send_whenNotRunning_throwsEventLoopError() {
        TimeoutAccumulatorEventLoop eventLoop = new TimeoutAccumulatorEventLoop(
                "acc",
                10,
                100,
                2,
                passThroughAccumulator(),
                (maxSize, accumulator, destinations, deadline) -> new TrackingBuffer(0)
        );

        assertThrows(EventLoopError.class, () -> eventLoop.send(new SignalMock("1")));
        assertEquals(State.CREATED, eventLoop.getState());
    }

    @Test
    void connect_whenRunning_throwsSigletError() {
        TimeoutAccumulatorEventLoop eventLoop = new TimeoutAccumulatorEventLoop(
                "acc",
                10,
                100,
                2,
                passThroughAccumulator(),
                (maxSize, accumulator, destinations, deadline) -> new TrackingBuffer(0)
        );

        assertTimeout(Duration.ofSeconds(1), () -> {
            eventLoop.start();
            assertEquals(State.RUNNING, eventLoop.getState());
            assertThrows(SigletError.class, () -> eventLoop.connect(new NoopDestination("dest")));
            eventLoop.stop();
        });

        assertEquals(State.STOPPED, eventLoop.getState());
    }

    @Test
    void start_send_stop_addsSignalsToBufferAndFlushes() {
        TrackingBuffer trackingBuffer = new TrackingBuffer(2);

        TimeoutAccumulatorEventLoop eventLoop = new TimeoutAccumulatorEventLoop(
                "acc",
                10,
                100,
                2,
                passThroughAccumulator(),
                (maxSize, accumulator, destinations, deadline) -> trackingBuffer
        );

        assertTimeout(Duration.ofSeconds(1), () -> {
            eventLoop.start();

            assertTrue(eventLoop.send(new SignalMock("1")));
            assertTrue(eventLoop.send(new SignalMock("2")));

            assertTrue(trackingBuffer.awaitAdds(500));

            eventLoop.stop();
        });

        assertEquals(List.of("1", "2"), trackingBuffer.addedSignalIds);
        assertEquals(1, trackingBuffer.flushCalls);
        assertEquals(State.STOPPED, eventLoop.getState());
    }

    @Test
    void stop_withoutSignals_stillFlushesBuffer() {
        TrackingBuffer trackingBuffer = new TrackingBuffer(0);

        TimeoutAccumulatorEventLoop eventLoop = new TimeoutAccumulatorEventLoop(
                "acc",
                10,
                100,
                2,
                passThroughAccumulator(),
                (maxSize, accumulator, destinations, deadline) -> trackingBuffer
        );

        assertTimeout(Duration.ofSeconds(1), () -> {
            eventLoop.start();
            eventLoop.stop();
        });

        assertTrue(trackingBuffer.addedSignalIds.isEmpty());
        assertEquals(1, trackingBuffer.flushCalls);
    }

    private Function<Signal[], Signal> passThroughAccumulator() {
        return signals -> signals[0];
    }

    private static final class SignalMock implements Signal {
        private final String id;

        private SignalMock(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    private static final class NoopDestination implements SignalDestination {
        private final String name;

        private NoopDestination(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean send(Signal signal) {
            return true;
        }

        @Override
        public SignalCapabilities getIncomingCapabilities() {
            return SignalCapabilities.of(SignalMock.class);
        }
    }

    private static final class TrackingBuffer extends Buffer {

        private final CountDownLatch addsLatch;
        private final List<String> addedSignalIds = Collections.synchronizedList(new ArrayList<>());
        private volatile int flushCalls;

        private TrackingBuffer(int expectedAdds) {
            super(1, passThroughSignal(), List.of(new NoopDestination("ignored")), new PassiveDeadline());
            this.addsLatch = new CountDownLatch(expectedAdds);
        }

        @Override
        public void add(Signal signal) {
            addedSignalIds.add(signal.getId());
            addsLatch.countDown();
        }

        @Override
        public void flush() {
            flushCalls++;
        }

        private boolean awaitAdds(long timeoutMillis) throws InterruptedException {
            return addsLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        }

        private static Function<Signal[], Signal> passThroughSignal() {
            return signals -> signals.length == 0 ? new SignalMock("empty") : signals[0];
        }
    }

    private static final class PassiveDeadline implements Deadline {
        @Override
        public void start() {
        }

        @Override
        public void reset() {
        }

        @Override
        public boolean isExpired() {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public long remainingNanos() {
            return Long.MAX_VALUE;
        }
    }
}

