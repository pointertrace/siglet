package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.parser.Location;
import io.github.pointertrace.siglet.parser.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroovyRouterConfigTest {

    private GroovyRouterConfig config;
    private ProcessorDescriptorMock descriptor;

    @BeforeEach
    void setUp() {
        config = new GroovyRouterConfig();
        descriptor = new ProcessorDescriptorMock();
        descriptor.setName(new StringValue("test-processor"));
        descriptor.setLocation(Location.of(1, 1));
    }

    @Test
    void validate() {

        descriptor.setTo(List.of(
                new StringValue("destination1"),
                new StringValue("destination2"),
                new StringValue("destination3")
        ));

        RouteConfig route1 = new RouteConfig();
        route1.setWhen(new StringValue("signal.name == 'test'"));
        route1.setTo(new StringValue("destination1"));

        RouteConfig route2 = new RouteConfig();
        route2.setWhen(new StringValue("signal.name == 'other'"));
        route2.setTo(new StringValue("destination2"));

        config.setRoutes(List.of(route1, route2));
        config.setDefaultRoute(new StringValue("destination3"));

        assertDoesNotThrow(() -> config.validate(descriptor));
    }

    @Test
    void validate_moreDestinationsThanRoutes() {

        descriptor.setTo(List.of(
                new StringValue("destination1"),
                new StringValue("destination2"),
                new StringValue("destination3")
        ));

        RouteConfig destination2 = new RouteConfig();
        destination2.setTo(new StringValue("destination2"));

        config.setRoutes(List.of(destination2));
        config.setDefaultRoute(new StringValue("destination1"));

        SigletError sigletError = assertThrows(SigletError.class, () -> config.validate(descriptor));

        assertEquals("The processor 'test-processor' at (1:1) has some destinations defined in 'to' " +
                "(destination3) that are not defined as a route", sigletError.getMessage());
    }


    @Test
    void validate_moreRoutesThanDestinations() {

        descriptor.setTo(List.of(
                new StringValue("destination1"),
                new StringValue("destination2")
        ));

        RouteConfig destination2 = new RouteConfig();
        RouteConfig destination3 = new RouteConfig();

        destination2.setTo(new StringValue("destination2"));
        destination3.setTo(new StringValue("destination3"));

        config.setRoutes(List.of(destination2, destination3));
        config.setDefaultRoute(new StringValue("destination1"));

        SigletError sigletError = assertThrows(SigletError.class, () -> config.validate(descriptor));

        assertEquals("The processor 'test-processor' at (1:1) has some routes (destination3) that are not " +
                "defined as processor destination defined in 'to'", sigletError.getMessage());
    }

    @Test
    void validate_routesNotInDestinationsAndDestinationsNotInRoutes() {

        descriptor.setTo(List.of(
                new StringValue("destination1"),
                new StringValue("destination2"),
                new StringValue("destination3")
        ));

        RouteConfig destination2 = new RouteConfig();
        RouteConfig destination4 = new RouteConfig();

        destination2.setTo(new StringValue("destination2"));
        destination4.setTo(new StringValue("destination4"));

        config.setRoutes(List.of(destination2, destination4));
        config.setDefaultRoute(new StringValue("destination1"));

        SigletError sigletError = assertThrows(SigletError.class, () -> config.validate(descriptor));

        assertEquals("The processor 'test-processor' at (1:1) has some destinations defined in 'to' " +
                "(destination3) that are not defined as a route and some routes (destination4) that are not defined as " +
                "processor destination defined in 'to'", sigletError.getMessage());
    }

    public static class ProcessorDescriptorMock extends ProcessorDescriptor {

        @Override
        public void setTo(List<StringValue> to) {
            super.setTo(to);
        }
    }

}