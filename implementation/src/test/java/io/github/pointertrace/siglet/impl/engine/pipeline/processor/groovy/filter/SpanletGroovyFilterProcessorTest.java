package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

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

class SpanletGroovyFilterProcessorTest {

    private SpanletGroovyFilterProcessorType spanletGroovyFilterProcessorType;

    private MockSignalDestination defaultSignalDestination;

    private ProtoSpanAdapter spanAdapter;

    @BeforeEach
    public void setUp() {

        spanletGroovyFilterProcessorType = new SpanletGroovyFilterProcessorType();

        defaultSignalDestination = new MockSignalDestination("default", SignalCapabilities.of(Span.class));

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
                expression: |
                  signal.name == "span-name"
                """;

        ConfigurationFactory<GroovyFilterConfig> configurationFactory = spanletGroovyFilterProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyFilterConfig filterConfig = factory.create(GroovyFilterConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("filter"));
        processorDescriptor.setConfig(filterConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> filterProcessor = spanletGroovyFilterProcessorType.getComponentCreator().create(null, processorNode);

        GroovyFilterProcessor groovyFilterProcessor = assertInstanceOf(GroovyFilterProcessor.class, filterProcessor);

        groovyFilterProcessor.connect(defaultSignalDestination);

        groovyFilterProcessor.start();

        groovyFilterProcessor.send(spanAdapter);

        groovyFilterProcessor.stop();

        assertEquals(1, defaultSignalDestination.getSize());

        Span processedSpan = defaultSignalDestination.get("Span(traceId:00000000000000000000000000000001,spanId:0000000000000001)", Span.class);

        assertEquals("span-name", processedSpan.getName());
    }

    @Test
    void process_nonMatch() {

        String script = """
                expression: |
                  signal.name == "other-name"
                """;

        ConfigurationFactory<GroovyFilterConfig> configurationFactory = spanletGroovyFilterProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyFilterConfig filterConfig = factory.create(GroovyFilterConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("filter"));
        processorDescriptor.setConfig(filterConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> filterProcessor = spanletGroovyFilterProcessorType.getComponentCreator().create(null, processorNode);

        GroovyFilterProcessor groovyFilterProcessor = assertInstanceOf(GroovyFilterProcessor.class, filterProcessor);

        groovyFilterProcessor.connect(defaultSignalDestination);

        groovyFilterProcessor.start();

        groovyFilterProcessor.send(spanAdapter);

        groovyFilterProcessor.stop();

        assertEquals(0, defaultSignalDestination.getSize());
    }

    @Test
    void checkCompatibility() {

        String script = """
                expression: |
                  true
                """;

        ConfigurationFactory<GroovyFilterConfig> configurationFactory = spanletGroovyFilterProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyFilterConfig filterConfig = factory.create(GroovyFilterConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("filter"));
        processorDescriptor.setConfig(filterConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> filterProcessor = spanletGroovyFilterProcessorType.getComponentCreator().create(null, processorNode);

        GroovyFilterProcessor groovyFilterProcessor = assertInstanceOf(GroovyFilterProcessor.class, filterProcessor);

        SigletError ex = assertThrows(SigletError.class, () ->
                groovyFilterProcessor.connect(new MockSignalDestination("mock", SignalCapabilities.of(Metric.class))));

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
