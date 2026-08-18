package io.github.pointertrace.siglet.impl.eventloop.processor;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.BaseSignalAdapter;
import io.github.pointertrace.siglet.impl.engine.*;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.event.NoopEventBus;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class ProcessorBaseEventLoopTest {

    private Component parent;

    private List<Integer> result;


    @BeforeEach
    public void setUp() {

        parent = mock(Component.class);
        when(parent.getName()).thenReturn("name");

        ResultFactoryImpl.init();

        result = new ArrayList<>();
    }

    @Test
    void process() {

        ProcessorEventLoop<Integer, Integer> eventLoop = new ProcessorEventLoop<>(
                parent,
                3,
                1,
                new NoopEventBus(),
                () -> (signal) -> signal * 3,
                (signal) -> {
                    result.add(signal);
                }

        );

        assertEquals(State.CREATED, eventLoop.getState());


        assertTimeout(Duration.ofSeconds(1), () -> {

            eventLoop.start();

            assertEquals(State.RUNNING, eventLoop.getState());

            eventLoop.receive(1);
            eventLoop.receive(2);
            eventLoop.receive(3);

            eventLoop.stop();

        });

        assertEquals(State.STOPPED, eventLoop.getState());

        assertEquals(3, result.size());

        assertTrue(result.contains(3));
        assertTrue(result.contains(6));
        assertTrue(result.contains(9));
    }


}