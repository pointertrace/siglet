package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class BufferTest {

    private ArrayList<String> result;

    private final EmitterFunction<String> signalEmitterFunction = (String in) -> {
        result.add(in);
    };

    private final Function<String[], String> transformerFunction = (String[] in) -> String.join(",", in);

    @BeforeEach
    void setUp() {

        result = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Capacity-triggered dispatch
    // -------------------------------------------------------------------------

    @Test
    void add_fillsCapacity_dispatchesAggregatedSignal() {

        Buffer<String, String> buffer = new Buffer<>(2, new NeverExpireDeadline(),String.class, transformerFunction, signalEmitterFunction);

        buffer.add("1");
        buffer.add("2");

        assertEquals(1, result.size());
        assertEquals("1,2", result.getFirst());
    }

    @Test
    void add_fillsCapacity_resetsIndexForNextBatch() {

        Buffer<String,String> buffer = new Buffer<>(2, new NeverExpireDeadline(),String.class, transformerFunction, signalEmitterFunction);

        buffer.add("1");
        buffer.add("2");  // first dispatch
        buffer.add("3");  // starts new batch
        buffer.add("4");  // second dispatch

        assertEquals(2, result.size());
        assertEquals("1,2", result.get(0));
        assertEquals("3,4", result.get(1));
    }

    @Test
    void add_fillsCapacity_callsDeadlineReset() {
        SpyDeadline deadline = new SpyDeadline(false);

        Buffer<String,String> buffer = new Buffer<>(2, deadline,String.class, transformerFunction, signalEmitterFunction);
        buffer.add("1");
        buffer.add("2");

        assertEquals(1, deadline.resetCalls);
    }

    // -------------------------------------------------------------------------
    // Deadline-triggered dispatch
    // -------------------------------------------------------------------------

    @Test
    void add_deadlineExpiredAfterFirstSignal_dispatchesImmediately() {
        Buffer<String,String> buffer = new Buffer<>(10, new AlwaysExpiredDeadline(),String.class, transformerFunction, signalEmitterFunction);

        buffer.add("1");

        assertEquals(1, result.size());
        assertEquals("1", result.getFirst());
    }

    @Test
    void add_deadlineExpiredMidBatch_dispatchesPartialBatch() {
        SpyDeadline deadline = new SpyDeadline(false);

        Buffer<String,String> buffer = new Buffer<>(10, deadline,String.class, transformerFunction, signalEmitterFunction);
        buffer.add("1");
        buffer.add("2");
        // now simulate deadline expiry
        deadline.expired = true;
        buffer.add("3");

        assertEquals("1,2,3", result.getFirst());
    }

    // -------------------------------------------------------------------------
    // Deadline start
    // -------------------------------------------------------------------------

    @Test
    void add_firstSignal_startsDeadline() {
        SpyDeadline deadline = new SpyDeadline(false);
        Buffer<String,String> buffer = new Buffer<>(10, deadline, String.class, transformerFunction, signalEmitterFunction);

        buffer.add("1");

        assertEquals(1, deadline.startCalls);
    }

    @Test
    void add_secondSignal_doesNotStartDeadlineAgain() {
        SpyDeadline deadline = new SpyDeadline(false);
        Buffer<String,String> buffer = new Buffer<>(10, deadline, String.class, transformerFunction, signalEmitterFunction);

        buffer.add("1");
        buffer.add("2");

        assertEquals(1, deadline.startCalls);
    }

    // -------------------------------------------------------------------------
    // No dispatch when buffer is partial and deadline is not expired
    // -------------------------------------------------------------------------

    @Test
    void add_belowCapacity_notExpired_doesNotDispatch() {

        Buffer<String,String> buffer = new Buffer<>(5, new NeverExpireDeadline(), String.class, transformerFunction, signalEmitterFunction);

        buffer.add("1");
        buffer.add("2");

        assertEquals(0, result.size());
    }

    // -------------------------------------------------------------------------
    // flush()
    // -------------------------------------------------------------------------

    @Test
    void flush_withPendingSignals_dispatchesAggregatedSignal() {

        Buffer<String,String> buffer = new Buffer<>(10, new NeverExpireDeadline(), String.class, transformerFunction, signalEmitterFunction);

        buffer.add("1");
        buffer.add("2");
        buffer.flush();

        assertEquals(1, result.size());
        assertEquals("1,2", result.getFirst());
    }

    @Test
    void flush_emptyBuffer_doesNotDispatch() {
        Buffer<String,String> buffer = new Buffer<>(10, new NeverExpireDeadline(), String.class, transformerFunction, signalEmitterFunction);

        buffer.flush();

        assertEquals(0, result.size());
    }

    @Test
    void flush_callsDeadlineReset() {
        SpyDeadline deadline = new SpyDeadline(false);
        Buffer<String,String> buffer = new Buffer<>(10, deadline, String.class, transformerFunction, signalEmitterFunction);

        buffer.add("1");
        buffer.flush();

        assertEquals(1, deadline.resetCalls);
    }

    @Test
    void flush_afterFlush_bufferIsEmpty_secondFlushDoesNothing() {
        Buffer<String,String> buffer = new Buffer<>(10, new NeverExpireDeadline(), String.class, transformerFunction, signalEmitterFunction);

        buffer.add("1");
        buffer.flush();
        buffer.flush(); // second flush - buffer should be empty

        assertEquals(1, result.size());
    }

    // -------------------------------------------------------------------------
    // Multiple destinations
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // Delegation: remainingNanos() and hasActiveDeadline()
    // -------------------------------------------------------------------------

    @Test
    void remainingNanos_delegatesToDeadline() {
        SpyDeadline deadline = new SpyDeadline(false);
        deadline.remainingNanos = 42_000L;
        Buffer<String,String> buffer = new Buffer<>(10, deadline, String.class, transformerFunction, signalEmitterFunction);

        assertEquals(42_000L, buffer.remainingNanos());
    }

    @Test
    void hasActiveDeadline_whenDeadlineActive_returnsTrue() {
        SpyDeadline deadline = new SpyDeadline(false);
        deadline.active = true;
        Buffer<String,String> buffer = new Buffer<>(10, deadline, String.class, transformerFunction, signalEmitterFunction);

        assertTrue(buffer.hasActiveDeadline());
    }

    @Test
    void hasActiveDeadline_whenDeadlineInactive_returnsFalse() {
        SpyDeadline deadline = new SpyDeadline(false);
        deadline.active = false;
        Buffer<String,String> buffer = new Buffer<>(10, deadline, String.class, transformerFunction, signalEmitterFunction);

        assertFalse(buffer.hasActiveDeadline());
    }

    // =========================================================================
    // Helpers
    // =========================================================================



    // =========================================================================
    // Mocks / Stubs
    // =========================================================================


    /** Deadline that never expires. */
    static class NeverExpireDeadline implements Deadline {
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




