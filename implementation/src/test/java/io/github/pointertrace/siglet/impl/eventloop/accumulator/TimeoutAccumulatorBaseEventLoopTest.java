package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptors;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.Processor;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TimeoutAccumulatorBaseEventLoopTest {

    private Processor parent;

    private List<String> result;

    private final Function<String[], String> transformerFunction = ins -> String.join(",", ins);

    private final EmitterFunction<String> signalEmitterFunction = (String out) -> result.add(out);


    @BeforeEach
    void setUp() {
        parent = mock(Processor.class);
        result = new ArrayList<>();
    }


    @Test
    void send_emmitSizeReached() {

        TimeoutAccumulatorEventLoop<String,String> eventLoop = new TimeoutAccumulatorEventLoop<>(
                parent,
                "acc",
                10,
                100,
                2,
                String.class,
                transformerFunction,
                signalEmitterFunction,
                new Interceptors()
        );

        assertTimeout(Duration.ofSeconds(1), () -> {

            eventLoop.start();

            assertTrue(eventLoop.getReceiver().receive("1"));
            assertTrue(eventLoop.getReceiver().receive("2"));

            Thread.sleep(10);

            eventLoop.stop();
        });

        assertEquals(1, result.size());
        assertEquals("1,2", result.getFirst());
    }

    @Test
    void send_emmitTimeoutReached() {

        TimeoutAccumulatorEventLoop<String,String> eventLoop = new TimeoutAccumulatorEventLoop<>(
                parent,
                "acc",
                10,
                100,
                3,
                String.class,
                transformerFunction,
                signalEmitterFunction,
                new Interceptors()
        );

        assertTimeout(Duration.ofSeconds(1), () -> {
            eventLoop.start();
            assertTrue(eventLoop.getReceiver().receive("1"));
            assertTrue(eventLoop.getReceiver().receive("2"));
            Thread.sleep(200);
            eventLoop.stop();
        });

        assertEquals(1, result.size());
        assertEquals("1,2", result.getFirst());
    }

    @Test
    void send_emmitRemaining() {

        TimeoutAccumulatorEventLoop<String,String> eventLoop = new TimeoutAccumulatorEventLoop<>(
                parent,
                "acc",
                10,
                1000,
                3,
                String.class,
                transformerFunction,
                signalEmitterFunction,
                new Interceptors()
        );

        assertTimeout(Duration.ofSeconds(1), () -> {
            eventLoop.start();
            assertTrue(eventLoop.getReceiver().receive("1"));
            assertTrue(eventLoop.getReceiver().receive("2"));
            Thread.sleep(20);
            eventLoop.stop();
        });

        assertEquals(1, result.size());
        assertEquals("1,2", result.getFirst());
    }


 }

