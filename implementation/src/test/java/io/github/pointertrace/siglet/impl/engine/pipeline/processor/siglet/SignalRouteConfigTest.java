package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SignalRouteConfigTest {

    private SignalRoute signalRoute;

    private String processSignal;

    private String otherSignal;

    private String actualDestination;

    private String actualSignal;

    final SignalEmitterFunction signalEmitterFunction = (signal, destination) -> {
        actualSignal = (String) signal;
        actualDestination = destination;
    };

    @BeforeEach
    void setUp() {

        processSignal = "processSignal";

        otherSignal = "otherSignal";

        actualDestination = null;

        actualSignal = null;
    }

    @Test
    void emit_defaultSignalDrop() {
        signalRoute = SignalRoute.drop();

        signalRoute.emmit(Map.of(), signalEmitterFunction, processSignal);

        assertNotNull(actualSignal);
        assertTrue(SignalDestination.isDrop(actualDestination));

    }


    @Test
    void emit_defaultSignalProceed() {

        signalRoute = SignalRoute.proceed();

        signalRoute.emmit(Map.of(), signalEmitterFunction, processSignal);

        assertEquals(processSignal, actualSignal);
        assertTrue(SignalDestination.isAll(actualDestination));
    }

    @Test
    void emit_defaultSignalSpecificDestination() {

        signalRoute = SignalRoute.proceed("second");

        signalRoute.emmit(Map.of(), signalEmitterFunction, processSignal);

        assertEquals(processSignal, actualSignal);
        assertEquals("second", actualDestination);
    }

    @Test
    void emit_defaultSignalSpecificMappedDestination() {

        signalRoute = SignalRoute.proceed("second");

        signalRoute.emmit(Map.of("first", "mapped-first", "second", "mapped-second"), signalEmitterFunction, processSignal);


        assertEquals(processSignal, actualSignal);
        assertEquals("mapped-second", actualDestination);

    }


    @Test
    void emit_otherSignalDrop() {

        SigletError e = assertThrows(SigletError.class,
                () -> SignalRoute.route(otherSignal, SignalDestination.DROP));

        assertEquals("Non default signal cannot have drop destination",e.getMessage());

    }


    @Test
    void emit_otherSignalAll() {

        signalRoute = SignalRoute.route(otherSignal, SignalDestination.ALL);

        signalRoute.emmit(Map.of(), signalEmitterFunction, processSignal);

        assertEquals(otherSignal, actualSignal);
        assertTrue(SignalDestination.isAll(actualDestination));
    }

    @Test
    void emit_otherSignalSpecificDestination() {

        signalRoute = SignalRoute.route(otherSignal, "second");

        signalRoute.emmit(Map.of(), signalEmitterFunction, processSignal);

        assertEquals(otherSignal, actualSignal);
        assertEquals("second", actualDestination);
    }

    @Test
    void emit_otherSignalSpecificMappedDestination() {


        signalRoute = SignalRoute.route(otherSignal, "second");

        signalRoute.emmit(Map.of("first", "mapped-first", "second", "mapped-second"), signalEmitterFunction, processSignal);


        assertEquals(otherSignal, actualSignal);
        assertEquals("mapped-second", actualDestination);

    }

}