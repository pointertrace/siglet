package com.siglet.container.eventloop.result;

import com.siglet.api.Signal;
import com.siglet.container.eventloop.EventLoopError;
import com.siglet.container.eventloop.MapSignalDestination;
import com.siglet.container.eventloop.processor.result.SignalRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignalRouteConfigTest {

    private MapSignalDestination<SignalMock> firstDestination;

    private MapSignalDestination<SignalMock> secondDestination;

    private SignalMock processSignal;

    private SignalMock otherSignal;

    private SignalRoute<SignalMock> signalRoute;

    @BeforeEach
    void setUp() {

        firstDestination = new MapSignalDestination<SignalMock>("first", SignalMock.class);

        secondDestination = new MapSignalDestination<SignalMock>("second", SignalMock.class);

        processSignal = new SignalMock(1);

        otherSignal = new SignalMock(2);

    }

    @Test
    void dispatch_drop() {
        signalRoute = SignalRoute.drop();

        signalRoute.dispatch(processSignal, List.of(firstDestination, secondDestination));

        assertEquals(0, firstDestination.getSize());
        assertEquals(0, secondDestination.getSize());
    }

    @Test
    void dispatch_proceed() {
        signalRoute = SignalRoute.proceed();

        signalRoute.dispatch(processSignal, List.of(firstDestination, secondDestination));

        assertEquals(1, firstDestination.getSize());
        assertTrue(firstDestination.has("1"));
        assertEquals(processSignal, firstDestination.get("1"));

        assertEquals(1, secondDestination.getSize());
        assertTrue(secondDestination.has("1"));
        assertEquals(processSignal, secondDestination.get("1"));
    }

    @Test
    void dispatch_proceedSpecificDestination() {
        signalRoute = SignalRoute.proceed("second");

        signalRoute.dispatch(processSignal, List.of(firstDestination, secondDestination));

        assertEquals(0, firstDestination.getSize());

        assertEquals(1, secondDestination.getSize());
        assertTrue(secondDestination.has("1"));
        assertEquals(processSignal, secondDestination.get("1"));
    }

    @Test
    void dispatch_proceedSpecificDestinationNotFound() {

        signalRoute = SignalRoute.proceed("non-existing-destination");


        EventLoopError e = assertThrowsExactly(EventLoopError.class, () -> {
            signalRoute.dispatch(processSignal, List.of(firstDestination, secondDestination));
        });

        assertEquals("Signal 1 to be sent to non-existing-destination which is not available", e.getMessage());
    }

    @Test
    void dispatch_route() {
        signalRoute = SignalRoute.route(otherSignal, "second");

        signalRoute.dispatch(processSignal, List.of(firstDestination, secondDestination));

        assertEquals(0, firstDestination.getSize());

        assertEquals(1, secondDestination.getSize());
        assertTrue(secondDestination.has("2"));
        assertEquals(otherSignal, secondDestination.get("2"));
    }

    @Test
    void dispatch_routeDestinationNotFound() {
        signalRoute = SignalRoute.route(otherSignal, "non-existing-destination");


        EventLoopError e = assertThrowsExactly(EventLoopError.class, () -> {
            signalRoute.dispatch(processSignal, List.of(firstDestination, secondDestination));
        });

        assertEquals("Signal 2 to be sent to non-existing-destination which is not available", e.getMessage());
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