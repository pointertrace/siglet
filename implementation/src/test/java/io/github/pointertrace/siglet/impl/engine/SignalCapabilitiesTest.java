package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignalCapabilitiesTest {


    @Test
    void isAbleToSend() {

        assertTrue(SignalCapabilities.of(Span.class).isAbleToSend(SignalCapabilities.of(Signal.class)));
        assertFalse(SignalCapabilities.of(Signal.class).isAbleToSend(SignalCapabilities.of(Span.class)));

    }

    @Test
    void isAbleToHandle() {

        assertTrue(SignalCapabilities.of(Span.class).isAbleToHandle(Span.class));
        assertTrue(SignalCapabilities.of(Signal.class).isAbleToHandle(Span.class));


        assertFalse(SignalCapabilities.of(Span.class).isAbleToHandle(Signal.class));
        assertFalse(SignalCapabilities.of(Span.class).isAbleToHandle(Metric.class));

    }


    @Test
    void checkIsAbleToHandle() {

        SignalCapabilities.of(Span.class).checkIsAbleToHandle(Span.class);

        SignalCapabilities.of(Signal.class).checkIsAbleToHandle(Span.class);

        SigletError e = assertThrows(SigletError.class, () ->
                SignalCapabilities.of(Span.class).checkIsAbleToHandle(Signal.class));
        assertEquals("Signal type [io.github.pointertrace.siglet.api.Signal] cannot be handled! Can only " +
                "handle signal types [io.github.pointertrace.siglet.api.signal.trace.Span]", e.getMessage());

        e = assertThrows(SigletError.class, () ->
                SignalCapabilities.of(Span.class).checkIsAbleToHandle(Metric.class));
        assertEquals("Signal type [io.github.pointertrace.siglet.api.signal.metric.Metric] cannot be handled! " +
                "Can only handle signal types [io.github.pointertrace.siglet.api.signal.trace.Span]", e.getMessage());

    }


}