package io.github.pointertrace.siglet.impl.engine.component.connection;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptors;
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

    private SignalSourceImpl signalSourceImpl;

    private List<String> firstDestinationSignals;

    private List<String> secondDestinationSignals;

    private final Interceptor interceptor = new Interceptors();

    @BeforeEach
    void setUp() {
        GraphComponent<?> firstDestinationGraphComponent = mock(GraphComponent.class);
        when(firstDestinationGraphComponent.getName()).thenReturn("first-destination");

        GraphComponent<?> secondDestinationGraphComponent = mock(GraphComponent.class);
        when(secondDestinationGraphComponent.getName()).thenReturn("second-destination");

        firstDestinationSignals = new ArrayList<>();

        secondDestinationSignals = new ArrayList<>();

        SignalDestination firstDestination = new SignalDestinationImpl(firstDestinationGraphComponent, (signal) -> {
            firstDestinationSignals.add((String) signal);
            return true;
        }, interceptor);

        SignalDestination secondDestination = new SignalDestinationImpl(secondDestinationGraphComponent, (signal) -> {
            secondDestinationSignals.add((String) signal);
            return true;
        }, interceptor);

        signalSourceImpl = new SignalSourceImpl(firstDestinationGraphComponent, interceptor);

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