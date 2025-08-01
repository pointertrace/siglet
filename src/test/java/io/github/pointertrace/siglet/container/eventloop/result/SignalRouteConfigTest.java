package io.github.pointertrace.siglet.container.eventloop.result;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.eventloop.EventLoopError;
import io.github.pointertrace.siglet.container.eventloop.MapSignalDestination;
import io.github.pointertrace.siglet.container.eventloop.processor.result.SignalRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SignalRouteConfigTest {

    private MapSignalDestination firstDestination;

    private MapSignalDestination secondDestination;

    private MapSignalDestination mappedFirstDestination;

    private MapSignalDestination mappedSecondDestination;

    private SignalMock processSignal;

    private SignalMock otherSignal;

    private SignalRoute signalRoute;

    @BeforeEach
    void setUp() {

        firstDestination = new MapSignalDestination("first");

        secondDestination = new MapSignalDestination("second");

        mappedFirstDestination = new MapSignalDestination("mapped-first");

        mappedSecondDestination = new MapSignalDestination("mapped-second");

        processSignal = new SignalMock(1);

        otherSignal = new SignalMock(2);

    }

    @Test
    void dispatch_drop() {
        signalRoute = SignalRoute.drop();

        signalRoute.dispatch(Map.of(), processSignal, List.of(firstDestination, secondDestination));

        assertEquals(0, firstDestination.getSize());
        assertEquals(0, secondDestination.getSize());
    }

    @Test
    void dispatch_proceed() {
        signalRoute = SignalRoute.proceed();

        signalRoute.dispatch(Map.of(), processSignal, List.of(firstDestination, secondDestination));

        assertEquals(1, firstDestination.getSize());
        assertTrue(firstDestination.has("1"));
        assertEquals(processSignal, firstDestination.get("1", SignalMock.class));

        assertEquals(1, secondDestination.getSize());
        assertTrue(secondDestination.has("1"));
        assertEquals(processSignal, secondDestination.get("1", SignalMock.class ));
    }

    @Test
    void dispatch_proceedSpecificDestination() {
        signalRoute = SignalRoute.proceed("second");

        signalRoute.dispatch(Map.of(), processSignal, List.of(firstDestination, secondDestination));

        assertEquals(0, firstDestination.getSize());

        assertEquals(1, secondDestination.getSize());
        assertTrue(secondDestination.has("1"));
        assertEquals(processSignal, secondDestination.get("1", SignalMock.class));
    }

    @Test
    void dispatch_proceedSpecificMappedDestination() {

        signalRoute = SignalRoute.proceed("second");

        signalRoute.dispatch(Map.of("first", "mapped-first", "second", "mapped-second"), processSignal,
                List.of(mappedFirstDestination, mappedSecondDestination));

        assertEquals(0, mappedFirstDestination.getSize());

        assertEquals(1, mappedSecondDestination.getSize());
        assertTrue(mappedSecondDestination.has("1"));
        assertEquals(processSignal, mappedSecondDestination.get("1", SignalMock.class));
    }

    @Test
    void dispatch_proceedSpecificMappedDestinationNotFound() {

        signalRoute = SignalRoute.proceed("second");

        EventLoopError e = assertThrowsExactly(EventLoopError.class, () -> {
            signalRoute.dispatch(Map.of("first", "mapped-first", "second", "non-existent"),
                    processSignal,
                    List.of(mappedFirstDestination, mappedSecondDestination));
        });

        assertEquals("Signal 1 to be sent to second which is not available (first:mapped-first,second:non-existent)",
                e.getMessage());

    }

    @Test
    void dispatch_proceedSpecificDestinationNotFound() {

        signalRoute = SignalRoute.proceed("non-existing-destination");


        EventLoopError e = assertThrowsExactly(EventLoopError.class, () -> {
            signalRoute.dispatch(Map.of(), processSignal, List.of(firstDestination, secondDestination));
        });

        assertEquals("Signal 1 to be sent to non-existing-destination which is not available (first,second)",
                e.getMessage());
    }

    @Test
    void dispatch_route() {
        signalRoute = SignalRoute.route(otherSignal, "second");

        signalRoute.dispatch(Map.of(), processSignal, List.of(firstDestination, secondDestination));

        assertEquals(0, firstDestination.getSize());

        assertEquals(1, secondDestination.getSize());
        assertTrue(secondDestination.has("2"));
        assertEquals(otherSignal, secondDestination.get("2", SignalMock.class));
    }

    @Test
    void dispatch_routeDestinationNotFound() {
        signalRoute = SignalRoute.route(otherSignal, "non-existing-destination");


        EventLoopError e = assertThrowsExactly(EventLoopError.class, () -> {
            signalRoute.dispatch(Map.of(), processSignal, List.of(firstDestination, secondDestination));
        });

        assertEquals("Signal 2 to be sent to non-existing-destination which is not available (first,second)",
                e.getMessage());
    }


    public static class SignalMock implements Signal {

        private final int id;

        public SignalMock(int id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return "" + id;
        }
    }

}