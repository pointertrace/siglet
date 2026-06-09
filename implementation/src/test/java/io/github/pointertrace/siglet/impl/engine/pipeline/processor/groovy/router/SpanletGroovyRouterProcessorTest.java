package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.Component;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.SigletMetrics;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import io.github.pointertrace.siglet.parser.*;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpanletGroovyRouterProcessorTest {

    private MockSignalDestination defaultSignalDestination;

    private MockSignalDestination route1SignalDestination;

    private SpanAdapter spanAdapter;

    private SigletMetrics sigletMetrics;

    @BeforeEach
    public void setUp() {

        defaultSignalDestination = new MockSignalDestination("default", SignalCapabilities.of(Span.class));

        route1SignalDestination = new MockSignalDestination("route1", SignalCapabilities.of(Span.class));

        io.opentelemetry.proto.trace.v1.Span span = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setName("span-name")
                .setSpanId(AdapterUtils.spanId(1))
                .setTraceId(AdapterUtils.traceId(0, 1))
                .build();

        Resource resource = Resource.newBuilder().build();

        InstrumentationScope scope = InstrumentationScope.newBuilder().setName("scope").build();


        spanAdapter = new SpanAdapter(span, resource, scope);

        sigletMetrics = new SigletMetrics();

    }

    @Test
    void process_match() {

        RouteConfig routeConfig = new RouteConfig();
        routeConfig.setTo(new StringValue("route1"));
        routeConfig.setWhen(new StringValue("signal.name == 'span-name'"));
        List<RouteConfig> routes = List.of(routeConfig);

        GroovyRouterProcessor groovyRouterProcessor = new GroovyRouterProcessor("route", "default",
                routes, SignalCapabilities.of(Span.class), 1, 1, sigletMetrics);

        groovyRouterProcessor.connect(defaultSignalDestination);
        groovyRouterProcessor.connect(route1SignalDestination);

        groovyRouterProcessor.start();

        groovyRouterProcessor.send(spanAdapter);

        groovyRouterProcessor.stop();

        assertEquals(0, defaultSignalDestination.getSize());
        assertEquals(1, route1SignalDestination.getSize());

        Span processedSpan = route1SignalDestination.get(0, Span.class);

        assertEquals("span-name", processedSpan.getName());
    }

    @Test
    void process_default() {

        RouteConfig routeConfig = new RouteConfig();
        routeConfig.setTo(new StringValue("route1"));
        routeConfig.setWhen(new StringValue("signal.name == 'other-name'"));
        List<RouteConfig> routes = List.of(routeConfig);

        GroovyRouterProcessor groovyRouterProcessor = new GroovyRouterProcessor("route", "default",
                routes, SignalCapabilities.of(Span.class), 1, 1, sigletMetrics);

        groovyRouterProcessor.connect(defaultSignalDestination);
        groovyRouterProcessor.connect(route1SignalDestination);

        groovyRouterProcessor.start();

        groovyRouterProcessor.send(spanAdapter);

        groovyRouterProcessor.stop();

        assertEquals(1, defaultSignalDestination.getSize());
        assertEquals(0, route1SignalDestination.getSize());

        Span processedSpan = defaultSignalDestination.get(0, Span.class);

        assertEquals("span-name", processedSpan.getName());
    }

    @Test
    void checkCompatibility() {


        RouteConfig routeConfig = new RouteConfig();
        routeConfig.setTo(new StringValue("route1"));
        routeConfig.setWhen(new StringValue("signal.name == 'span-name'"));
        List<RouteConfig> routes = List.of(routeConfig);

        GroovyRouterProcessor groovyRouterProcessor = new GroovyRouterProcessor("route", "default",
                routes, SignalCapabilities.of(Span.class), 1, 1, sigletMetrics);


        SigletError ex = assertThrows(SigletError.class, () ->
                groovyRouterProcessor.connect(new MockSignalDestination("mock", SignalCapabilities.of(Metric.class))));

        assertEquals("Cannot connect processor [route] to [mock] because they have incompatible signal " +
                        "capabilities. Processor generates [io.github.pointertrace.siglet.api.signal.trace.Span] and " +
                        "destination expects [io.github.pointertrace.siglet.api.signal.metric.Metric]",
                ex.getMessage());
    }

}
