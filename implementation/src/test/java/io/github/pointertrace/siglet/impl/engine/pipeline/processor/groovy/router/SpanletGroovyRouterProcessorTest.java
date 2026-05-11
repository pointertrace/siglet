package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.Component;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpanletGroovyRouterProcessorTest {

    private SpanletGroovyRouterProcessorType spanletGroovyRouterProcessorType;

    private MockSignalDestination defaultSignalDestination;

    private MockSignalDestination route1SignalDestination;

    private ProtoSpanAdapter spanAdapter;

    @BeforeEach
    public void setUp() {

        spanletGroovyRouterProcessorType = new SpanletGroovyRouterProcessorType();

        defaultSignalDestination = new MockSignalDestination("default", SignalCapabilities.of(Span.class));

        route1SignalDestination = new MockSignalDestination("route1", SignalCapabilities.of(Span.class));

        io.opentelemetry.proto.trace.v1.Span span = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setName("span-name")
                .setSpanId(AdapterUtils.spanId(1))
                .setTraceId(AdapterUtils.traceId(0, 1))
                .build();


        spanAdapter = new ProtoSpanAdapter().recycle(span, null, null);

    }

    @Test
    void process_match() {

        String script = """
                default: default
                routes:
                  - when: signal.name == "span-name"
                    to: route1
                """;

        ConfigurationFactory<GroovyRouterConfig> configurationFactory = spanletGroovyRouterProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyRouterConfig routerConfig = factory.create(GroovyRouterConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("router"));
        processorDescriptor.setConfig(routerConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> routerProcessor = spanletGroovyRouterProcessorType.getComponentCreator().create(null, processorNode);

        GroovyRouterProcessor groovyRouterProcessor = assertInstanceOf(GroovyRouterProcessor.class, routerProcessor);

        groovyRouterProcessor.connect(defaultSignalDestination);
        groovyRouterProcessor.connect(route1SignalDestination);

        groovyRouterProcessor.start();

        groovyRouterProcessor.send(spanAdapter);

        groovyRouterProcessor.stop();

        assertEquals(0, defaultSignalDestination.getSize());
        assertEquals(1, route1SignalDestination.getSize());

        Span processedSpan = route1SignalDestination.get("Span(traceId:00000000000000000000000000000001,spanId:0000000000000001)", Span.class);

        assertEquals("span-name", processedSpan.getName());
    }

    @Test
    void process_default() {

        String script = """
                default: default
                routes:
                  - when: signal.name == "other-name"
                    to: route1
                """;

        ConfigurationFactory<GroovyRouterConfig> configurationFactory = spanletGroovyRouterProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyRouterConfig routerConfig = factory.create(GroovyRouterConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("router"));
        processorDescriptor.setConfig(routerConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> routerProcessor = spanletGroovyRouterProcessorType.getComponentCreator().create(null, processorNode);

        GroovyRouterProcessor groovyRouterProcessor = assertInstanceOf(GroovyRouterProcessor.class, routerProcessor);

        groovyRouterProcessor.connect(defaultSignalDestination);
        groovyRouterProcessor.connect(route1SignalDestination);

        groovyRouterProcessor.start();

        groovyRouterProcessor.send(spanAdapter);

        groovyRouterProcessor.stop();

        assertEquals(1, defaultSignalDestination.getSize());
        assertEquals(0, route1SignalDestination.getSize());

        Span processedSpan = defaultSignalDestination.get("Span(traceId:00000000000000000000000000000001,spanId:0000000000000001)", Span.class);

        assertEquals("span-name", processedSpan.getName());
    }

    @Test
    void checkCompatibility() {

        String script = """
                default: default
                routes: []
                """;

        ConfigurationFactory<GroovyRouterConfig> configurationFactory = spanletGroovyRouterProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyRouterConfig routerConfig = factory.create(GroovyRouterConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("router"));
        processorDescriptor.setConfig(routerConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> routerProcessor = spanletGroovyRouterProcessorType.getComponentCreator().create(null, processorNode);

        GroovyRouterProcessor groovyRouterProcessor = assertInstanceOf(GroovyRouterProcessor.class, routerProcessor);

        // This check is currently missing in GroovyRouterProcessor.connect, let's see if it fails or if I need to add it.
        // Looking at GroovyRouterProcessor.connect(SignalDestination destination) { eventloop.connect(destination); }
        // And Eventloop.connect checks compatibility if I recall correctly from GroovyFilterProcessor.
        // Wait, GroovyFilterProcessor had:
        // eventLoop.getOutgoingCapabilities().checkCompatibility(destination.getIncomingCapabilities());
        // eventLoop.connect(destination);
        // GroovyRouterProcessor only has:
        // eventloop.connect(destination);
        
        // Let's check Eventloop.connect implementation.
        
        SigletError ex = assertThrows(SigletError.class, () ->
                groovyRouterProcessor.connect(new MockSignalDestination("mock", SignalCapabilities.of(Metric.class))));

        assertEquals("The two components are not compatible because there is no intersection between them (Span,Metric)",
                ex.getMessage());
    }

    public static class ProcessorDescriptorMock extends ProcessorDescriptor {

        @Override
        public void setQueueSize(IntegerValue queueSize) {
            super.setQueueSize(queueSize);
        }

        @Override
        public void setThreadPoolSize(IntegerValue threadPoolSize) {
            super.setThreadPoolSize(threadPoolSize);
        }

        @Override
        public void setConfig(Object config) {
            super.setConfig(config);
        }
    }
}
