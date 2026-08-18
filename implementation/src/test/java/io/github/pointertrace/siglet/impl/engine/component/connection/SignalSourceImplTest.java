package io.github.pointertrace.siglet.impl.engine.component.connection;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SignalSourceImplTest {

    private final String firstSignal = "firstSignal";

    private final String secondSignal = "secondSignal";

    private GraphComponent<?> firstDestinationGraphComponent;

    private GraphComponent<?> secondDestinationGraphComponent;

    private SignalSourceImpl signalSourceImpl;

    private SignalDestinationImpl firstDestination;

    private SignalDestinationImpl secondDestination;

    private List<String> firstDestinationSignals;

    private List<String> secondDestinationSignals;

    @BeforeEach
    void setUp() {
        firstDestinationGraphComponent = mock(GraphComponent.class);
        when(firstDestinationGraphComponent.getName()).thenReturn("first-destination");

        secondDestinationGraphComponent = mock(GraphComponent.class);
        when(secondDestinationGraphComponent.getName()).thenReturn("second-destination");

        firstDestinationSignals = new ArrayList<>();

        secondDestinationSignals = new ArrayList<>();

        firstDestination = new SignalDestinationImpl(firstDestinationGraphComponent, (signal) -> {
            firstDestinationSignals.add((String) signal);
            return true;
        });

        secondDestination = new SignalDestinationImpl(secondDestinationGraphComponent, (signal) -> {
            secondDestinationSignals.add((String) signal);
            return true;
        });

        signalSourceImpl = new SignalSourceImpl(firstDestinationGraphComponent);

        signalSourceImpl.connect(firstDestination);
        signalSourceImpl.connect(secondDestination);

    }

    @Test
    void emmit_allDestinations() {

        signalSourceImpl.getSignalEmitterFunction().emit(firstSignal, SignalDestination.ALL);

        assertEquals(1, firstDestinationSignals.size());
        assertTrue(firstDestinationSignals.contains(firstSignal));

        assertEquals(1, secondDestinationSignals.size());
        assertTrue(secondDestinationSignals.contains(firstSignal));

        signalSourceImpl.getSignalEmitterFunction().emit(secondSignal, SignalDestination.ALL);

        assertEquals(2, firstDestinationSignals.size());
        assertTrue(firstDestinationSignals.contains(firstSignal));
        assertTrue(firstDestinationSignals.contains(secondSignal));

        assertEquals(2, secondDestinationSignals.size());
        assertTrue(secondDestinationSignals.contains(firstSignal));
        assertTrue(secondDestinationSignals.contains(secondSignal));
    }

    @Test
    void emmit_drop() {

        signalSourceImpl.getSignalEmitterFunction().emit(firstSignal, SignalDestination.DROP);

        assertTrue (firstDestinationSignals.isEmpty());
        assertTrue(secondDestinationSignals.isEmpty());

        signalSourceImpl.getSignalEmitterFunction().emit(secondSignal, SignalDestination.DROP);

        assertTrue (firstDestinationSignals.isEmpty());
        assertTrue(secondDestinationSignals.isEmpty());
    }

    @Test
    void emmit_specificDestination() {

        signalSourceImpl.getSignalEmitterFunction().emit(firstSignal, "first-destination");

        assertEquals(1, firstDestinationSignals.size());
        assertTrue(firstDestinationSignals.contains(firstSignal));
        assertTrue (secondDestinationSignals.isEmpty());

        signalSourceImpl.getSignalEmitterFunction().emit(secondSignal, "second-destination");

        assertEquals(1, firstDestinationSignals.size());
        assertTrue(firstDestinationSignals.contains(firstSignal));
        assertEquals(1, secondDestinationSignals.size());
        assertTrue(secondDestinationSignals.contains(secondSignal));
    }
}