package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action;

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

class SpanletGroovyActionProcessorTest {

    private SpanletGroovyActionProcessorType spanletGroovyActionProcessorType;

    private MockSignalDestination defaultSignalDestination;

    private MockSignalDestination otherSignalDestination;

    private ProtoSpanAdapter spanAdapter;

    @BeforeEach
    public void setUp() {

        spanletGroovyActionProcessorType = new SpanletGroovyActionProcessorType();

        defaultSignalDestination = new MockSignalDestination("default", SignalCapabilities.of(Span.class));

        otherSignalDestination = new MockSignalDestination("other", SignalCapabilities.of(Span.class));

        io.opentelemetry.proto.trace.v1.Span span = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setName("span-name")
                .setSpanId(AdapterUtils.spanId(1))
                .setTraceId(AdapterUtils.traceId(0, 1))
                .build();


         spanAdapter =new ProtoSpanAdapter().recycle(span, null, null);

    }

    @Test
    void process() {

        String script = """
                action: |
                  signal.name = "prefix-" + signal.name
                  context.attributes["new-name"] = signal.name
                """;

        ConfigurationFactory<GroovyActionConfig> configurationFactory = spanletGroovyActionProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyActionConfig actionConfig = factory.create(GroovyActionConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("action"));
        processorDescriptor.setConfig(actionConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> actionProcessor = spanletGroovyActionProcessorType.getComponentCreator().create(null, processorNode);

        GroovyActionProcessor groovyActionProcessor = assertInstanceOf(GroovyActionProcessor.class, actionProcessor);

        groovyActionProcessor.connect(defaultSignalDestination);

        groovyActionProcessor.start();

        groovyActionProcessor.send(spanAdapter);

        groovyActionProcessor.stop();

        assertEquals(1, defaultSignalDestination.getSize());

        Span processedSpan = defaultSignalDestination.get("Span(traceId:00000000000000000000000000000001,spanId:0000000000000001)", Span.class);

        assertEquals("prefix-span-name", processedSpan.getName());
        assertTrue(groovyActionProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name",groovyActionProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    void process_drop() {

        String script = """
                action: |
                  signal.name = "prefix-" + signal.name
                  context.attributes["new-name"] = signal.name
                  drop()
                """;

        ConfigurationFactory<GroovyActionConfig> configurationFactory = spanletGroovyActionProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyActionConfig actionConfig = factory.create(GroovyActionConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock() ;
        processorDescriptor.setName(new StringValue("action"));
        processorDescriptor.setConfig(actionConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> actionProcessor = spanletGroovyActionProcessorType.getComponentCreator().create(null, processorNode);

        GroovyActionProcessor groovyActionProcessor = assertInstanceOf(GroovyActionProcessor.class, actionProcessor);

        groovyActionProcessor.connect(defaultSignalDestination);

        groovyActionProcessor.start();

        groovyActionProcessor.send(spanAdapter);

        groovyActionProcessor.stop();

        assertEquals(0, defaultSignalDestination.getSize());

        assertEquals("prefix-span-name", spanAdapter.getName());
        assertTrue(groovyActionProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name",groovyActionProcessor.getContext().getAttributes().get("new-name"));
    }
    @Test
    void process_proceedToDestination() {

        String script = """
                action: |
                  signal.name = "prefix-" + signal.name
                  context.attributes["new-name"] = signal.name
                  proceed("other")
                """;

        ConfigurationFactory<GroovyActionConfig> configurationFactory = spanletGroovyActionProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyActionConfig actionConfig = factory.create(GroovyActionConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("action"));
        processorDescriptor.setConfig(actionConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> actionProcessor = spanletGroovyActionProcessorType.getComponentCreator().create(null, processorNode);

        GroovyActionProcessor groovyActionProcessor = assertInstanceOf(GroovyActionProcessor.class, actionProcessor);

        groovyActionProcessor.connect(defaultSignalDestination);
        groovyActionProcessor.connect(otherSignalDestination);

        groovyActionProcessor.start();

        groovyActionProcessor.send(spanAdapter);

        groovyActionProcessor.stop();

        assertEquals(0, defaultSignalDestination.getSize());
        assertEquals(1, otherSignalDestination.getSize());

        Span processedSpan = otherSignalDestination.get("Span(traceId:00000000000000000000000000000001,spanId:0000000000000001)", Span.class);

        assertEquals("prefix-span-name", processedSpan.getName());
        assertTrue(groovyActionProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name",groovyActionProcessor.getContext().getAttributes().get("new-name"));

    }

    @Test
    void checkCompatibility() {

        String script = """
                action: |
                  signal.name = "prefix-" + signal.name
                  context.attributes["new-name"] = signal.name
                  proceed("other")
                """;

        ConfigurationFactory<GroovyActionConfig> configurationFactory = spanletGroovyActionProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        GroovyActionConfig actionConfig = factory.create(GroovyActionConfig.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("action"));
        processorDescriptor.setConfig(actionConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> actionProcessor = spanletGroovyActionProcessorType.getComponentCreator().create(null, processorNode);

        GroovyActionProcessor groovyActionProcessor = assertInstanceOf(GroovyActionProcessor.class, actionProcessor);

        SigletError ex = assertThrows(SigletError.class, () ->
                groovyActionProcessor.connect(new MockSignalDestination("mock", SignalCapabilities.of(Metric.class))));

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