package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BufferTest {

    private SignalMock signal1;
    private SignalMock signal2;
    private SignalMock signal3;

    @BeforeEach
    void setUp() {
        signal1 = new SignalMock("1");
        signal2 = new SignalMock("2");
        signal3 = new SignalMock("3");
    }

    // -------------------------------------------------------------------------
    // Capacity-triggered dispatch
    // -------------------------------------------------------------------------

    @Test
    void add_fillsCapacity_dispatchesAggregatedSignal() {
        MockSignalDestination destination = destination();
        Buffer buffer = new Buffer(2, joinAccumulator(), List.of(destination), new NeverExpiredDeadline());

        buffer.add(signal1);
        buffer.add(signal2);  // reaches capacity -> dispatch

        assertEquals(1, destination.getSize());
        assertTrue(destination.has("1,2"));
    }

    @Test
    void add_fillsCapacity_resetsIndexForNextBatch() {
        MockSignalDestination destination = destination();
        Buffer buffer = new Buffer(2, joinAccumulator(), List.of(destination), new NeverExpiredDeadline());

        buffer.add(signal1);
        buffer.add(signal2);  // first dispatch
        buffer.add(signal3);  // starts new batch
        buffer.add(new SignalMock("4"));  // second dispatch

        assertEquals(2, destination.getSize());
        assertTrue(destination.has("1,2"));
        assertTrue(destination.has("3,4"));
    }

    @Test
    void add_fillsCapacity_callsDeadlineReset() {
        SpyDeadline deadline = new SpyDeadline(false);
        Buffer buffer = new Buffer(2, joinAccumulator(), List.of(destination()), deadline);

        buffer.add(signal1);
        buffer.add(signal2);

        assertEquals(1, deadline.resetCalls);
    }

    // -------------------------------------------------------------------------
    // Deadline-triggered dispatch
    // -------------------------------------------------------------------------

    @Test
    void add_deadlineExpiredAfterFirstSignal_dispatchesImmediately() {
        MockSignalDestination destination = destination();
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination), new AlwaysExpiredDeadline());

        buffer.add(signal1);

        assertEquals(1, destination.getSize());
        assertTrue(destination.has("1"));
    }

    @Test
    void add_deadlineExpiredMidBatch_dispatchesPartialBatch() {
        MockSignalDestination destination = destination();
        SpyDeadline deadline = new SpyDeadline(false);
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination), deadline);

        buffer.add(signal1);
        buffer.add(signal2);
        // now simulate deadline expiry
        deadline.expired = true;
        buffer.add(signal3);

        assertEquals(1, destination.getSize());
        assertTrue(destination.has("1,2,3"));
    }

    // -------------------------------------------------------------------------
    // Deadline start
    // -------------------------------------------------------------------------

    @Test
    void add_firstSignal_startsDeadline() {
        SpyDeadline deadline = new SpyDeadline(false);
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination()), deadline);

        buffer.add(signal1);

        assertEquals(1, deadline.startCalls);
    }

    @Test
    void add_secondSignal_doesNotStartDeadlineAgain() {
        SpyDeadline deadline = new SpyDeadline(false);
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination()), deadline);

        buffer.add(signal1);
        buffer.add(signal2);

        assertEquals(1, deadline.startCalls);
    }

    // -------------------------------------------------------------------------
    // No dispatch when buffer is partial and deadline is not expired
    // -------------------------------------------------------------------------

    @Test
    void add_belowCapacity_notExpired_doesNotDispatch() {
        MockSignalDestination destination = destination();
        Buffer buffer = new Buffer(5, joinAccumulator(), List.of(destination), new NeverExpiredDeadline());

        buffer.add(signal1);
        buffer.add(signal2);

        assertEquals(0, destination.getSize());
    }

    // -------------------------------------------------------------------------
    // flush()
    // -------------------------------------------------------------------------

    @Test
    void flush_withPendingSignals_dispatchesAggregatedSignal() {
        MockSignalDestination destination = destination();
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination), new NeverExpiredDeadline());

        buffer.add(signal1);
        buffer.add(signal2);
        buffer.flush();

        assertEquals(1, destination.getSize());
        assertTrue(destination.has("1,2"));
    }

    @Test
    void flush_emptyBuffer_doesNotDispatch() {
        MockSignalDestination destination = destination();
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination), new NeverExpiredDeadline());

        buffer.flush();

        assertEquals(0, destination.getSize());
    }

    @Test
    void flush_callsDeadlineReset() {
        SpyDeadline deadline = new SpyDeadline(false);
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination()), deadline);

        buffer.add(signal1);
        buffer.flush();

        assertEquals(1, deadline.resetCalls);
    }

    @Test
    void flush_afterFlush_bufferIsEmpty_secondFlushDoesNothing() {
        MockSignalDestination destination = destination();
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination), new NeverExpiredDeadline());

        buffer.add(signal1);
        buffer.flush();
        buffer.flush(); // second flush - buffer should be empty

        assertEquals(1, destination.getSize());
    }

    // -------------------------------------------------------------------------
    // Multiple destinations
    // -------------------------------------------------------------------------

    @Test
    void add_multipleDestinations_allReceiveAggregatedSignal() {
        MockSignalDestination dest1 = destination();
        MockSignalDestination dest2 = destination();
        Buffer buffer = new Buffer(2, joinAccumulator(), List.of(dest1, dest2), new NeverExpiredDeadline());

        buffer.add(signal1);
        buffer.add(signal2);

        assertEquals(1, dest1.getSize());
        assertEquals(1, dest2.getSize());
        assertTrue(dest1.has("1,2"));
        assertTrue(dest2.has("1,2"));
    }

    // -------------------------------------------------------------------------
    // Delegation: remainingNanos() and hasActiveDeadline()
    // -------------------------------------------------------------------------

    @Test
    void remainingNanos_delegatesToDeadline() {
        SpyDeadline deadline = new SpyDeadline(false);
        deadline.remainingNanos = 42_000L;
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination()), deadline);

        assertEquals(42_000L, buffer.remainingNanos());
    }

    @Test
    void hasActiveDeadline_whenDeadlineActive_returnsTrue() {
        SpyDeadline deadline = new SpyDeadline(false);
        deadline.active = true;
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination()), deadline);

        assertTrue(buffer.hasActiveDeadline());
    }

    @Test
    void hasActiveDeadline_whenDeadlineInactive_returnsFalse() {
        SpyDeadline deadline = new SpyDeadline(false);
        deadline.active = false;
        Buffer buffer = new Buffer(10, joinAccumulator(), List.of(destination()), deadline);

        assertFalse(buffer.hasActiveDeadline());
    }

    // -------------------------------------------------------------------------
    // Accumulator receives correct signals
    // -------------------------------------------------------------------------

    @Test
    void add_accumulatorReceivesExactlyTheSignalsAdded() {
        Signal[] captured = new Signal[1];
        Buffer buffer = new Buffer(3, signals -> {
            captured[0] = new SignalMock(
                    Arrays.stream(signals).map(Signal::getId).collect(Collectors.joining(",")));
            return captured[0];
        }, List.of(destination()), new NeverExpiredDeadline());

        buffer.add(signal1);
        buffer.add(signal2);
        buffer.add(signal3); // capacity -> dispatch

        assertNotNull(captured[0]);
        assertEquals("1,2,3", captured[0].getId());
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private MockSignalDestination destination() {
        return new MockSignalDestination("dest", SignalCapabilities.of(SignalMock.class));
    }

    /** Accumulator that joins signal IDs with comma. */
    private java.util.function.Function<Signal[], Signal> joinAccumulator() {
        return signals -> new SignalMock(
                Arrays.stream(signals).map(Signal::getId).collect(Collectors.joining(",")));
    }

    // =========================================================================
    // Mocks / Stubs
    // =========================================================================

    static class SignalMock implements Signal {
        private final String id;

        SignalMock(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Signal[" + id + "]";
        }
    }

    /** Deadline that never expires. */
    static class NeverExpiredDeadline implements Deadline {
        @Override public void start() {}
        @Override public void reset() {}
        @Override public boolean isExpired() { return false; }
        @Override public boolean isActive() { return false; }
        @Override public long remainingNanos() { return Long.MAX_VALUE; }
    }

    /** Deadline that always reports expired. */
    static class AlwaysExpiredDeadline implements Deadline {
        @Override public void start() {}
        @Override public void reset() {}
        @Override public boolean isExpired() { return true; }
        @Override public boolean isActive() { return true; }
        @Override public long remainingNanos() { return 0; }
    }

    /** Spy deadline with controllable state and call counters. */
    static class SpyDeadline implements Deadline {
        boolean expired;
        boolean active = false;
        long remainingNanos = 0;
        int startCalls = 0;
        int resetCalls = 0;

        SpyDeadline(boolean expired) {
            this.expired = expired;
        }

        @Override public void start() { startCalls++; }
        @Override public void reset() { resetCalls++; }
        @Override public boolean isExpired() { return expired; }
        @Override public boolean isActive() { return active; }
        @Override public long remainingNanos() { return remainingNanos; }
    }
}




