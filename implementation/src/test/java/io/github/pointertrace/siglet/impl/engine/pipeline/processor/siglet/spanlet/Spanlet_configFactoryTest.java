package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.ResultFactory;
import io.github.pointertrace.siglet.api.SigletConfigFactory;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.siglet.configfile.SigletConfigFile;
import io.github.pointertrace.siglet.impl.config.siglet.fatjar.FatJarSigletDefinition;
import io.github.pointertrace.siglet.impl.engine.Component;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Spanlet_configFactoryTest {

    private SigletConfigFileDefinitionMock fatJarSigletDefinitionConfigFactoryClass;

    private FatJarSigletDefinition fatJarSigletDefinition;

    private SpanletProcessorType spanletProcessorType;

    private MockSignalDestination defaultSignalDestination;

    private MockSignalDestination otherSignalDestination;

    private ProtoSpanAdapter spanAdapter;

    @BeforeEach
    public void setUp() {

        defaultSignalDestination = new MockSignalDestination("default", SignalCapabilities.of(Span.class));

        otherSignalDestination = new MockSignalDestination("other", SignalCapabilities.of(Span.class));

        io.opentelemetry.proto.trace.v1.Span span = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setName("span-name")
                .setSpanId(AdapterUtils.spanId(1))
                .setTraceId(AdapterUtils.traceId(0, 1))
                .build();


        spanAdapter = new ProtoSpanAdapter().recycle(span, null, null);

    }



    @Test
    public void process() {

        setUpSpanlet(PrefixSpanlet.class);

        String script = """
                prefix: prefix-
                """;

        ConfigurationFactory<Config> configurationFactory = spanletProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        Config actionConfig = factory.create(Config.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("action"));
        processorDescriptor.setConfig(actionConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> actionProcessor = spanletProcessorType.getComponentCreator().create(null, processorNode);

        SpanletProcessor spanletProcessor = assertInstanceOf(SpanletProcessor.class, actionProcessor);

        spanletProcessor.connect(defaultSignalDestination);

        spanletProcessor.start();

        spanletProcessor.send(spanAdapter);

        spanletProcessor.stop();

        assertEquals(1, defaultSignalDestination.getSize());

        Span processedSpan = defaultSignalDestination.get("Span(traceId:00000000000000000000000000000001,spanId:0000000000000001)", Span.class);

        assertEquals("prefix-span-name", processedSpan.getName());
        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    public void process_drop() {

        setUpSpanlet(PrefixSpanletDrop.class);

        String script = """
                prefix: prefix-
                """;

        ConfigurationFactory<Config> configurationFactory = spanletProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        Config actionConfig = factory.create(Config.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("action"));
        processorDescriptor.setConfig(actionConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> actionProcessor = spanletProcessorType.getComponentCreator().create(null, processorNode);

        SpanletProcessor spanletProcessor = assertInstanceOf(SpanletProcessor.class, actionProcessor);

        spanletProcessor.connect(defaultSignalDestination);

        spanletProcessor.start();

        spanletProcessor.send(spanAdapter);

        spanletProcessor.stop();

        assertEquals(0, defaultSignalDestination.getSize());

        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    @Test
    public void process_proceedToDestination() {

        setUpSpanlet(PrefixSpanletProceedToDestination.class);

        String script = """
                prefix: prefix-
                """;

        ConfigurationFactory<Config> configurationFactory = spanletProcessorType.getConfigurationFactory();

        assertTrue(configurationFactory.createConfigSchema().isPresent());

        Schema schema = configurationFactory.createConfigSchema().get().build();

        Node node = Parser.DEFAULT.parse(script);

        Factory factory = schema.validate(node);

        Config actionConfig = factory.create(Config.class);

        ProcessorDescriptorMock processorDescriptor = new ProcessorDescriptorMock();
        processorDescriptor.setName(new StringValue("action"));
        processorDescriptor.setConfig(actionConfig);
        processorDescriptor.setQueueSize(new IntegerValue(1));
        processorDescriptor.setThreadPoolSize(new IntegerValue(1));

        ProcessorNode processorNode = new ProcessorNode(processorDescriptor);

        Component<ProcessorNode> actionProcessor = spanletProcessorType.getComponentCreator().create(null, processorNode);

        SpanletProcessor spanletProcessor = assertInstanceOf(SpanletProcessor.class, actionProcessor);

        spanletProcessor.connect(defaultSignalDestination);
        spanletProcessor.connect(otherSignalDestination);

        spanletProcessor.start();

        spanletProcessor.send(spanAdapter);

        spanletProcessor.stop();

        assertEquals(0, defaultSignalDestination.getSize());
        assertEquals(1, otherSignalDestination.getSize());

        assertTrue(spanletProcessor.getContext().getAttributes().containsKey("new-name"));
        assertEquals("prefix-span-name", spanletProcessor.getContext().getAttributes().get("new-name"));
    }

    public static class SigletConfigFileDefinitionMock extends SigletConfigFile.SigletConfigFileDefinition {

        @Override
        protected void setConfigFactoryClassName(StringValue configFactoryClassName) {
            super.setConfigFactoryClassName(configFactoryClassName);
        }

        @Override
        protected void setConfigParserFactoryClassName(StringValue configParserFactoryClassName) {
            super.setConfigParserFactoryClassName(configParserFactoryClassName);
        }

        @Override
        protected void setDestinations(List<StringValue> destinations) {
            super.setDestinations(destinations);
        }

        protected void setSigletClassName(StringValue sigletClassName) {
            super.setSigletClassName(sigletClassName);
        }
    }

    public static class SigletConfigFactoryMock implements SigletConfigFactory<Config> {

        @Override
        public Config createConfig(String yaml) {
            if (yaml == null || !yaml.startsWith("prefix: ")) {
                throw new IllegalArgumentException("Invalid yaml");
            }
            Config config = new Config();
            config.setPrefix(yaml.substring("prefix: ".length()));
            return config;
        }
    }

    public static class PrefixSpanlet implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.proceed();
        }
    }

    public static class PrefixSpanletDrop implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.drop();
        }
    }

    public static class PrefixSpanletProceedToDestination implements Spanlet<Config> {

        @Override
        public Result span(Span span, Context<Config> context, ResultFactory resultFactory) {
            String newName = context.getConfig().getPrefix() + span.getName();
            span.setName(newName);
            context.getAttributes().put("new-name", newName);
            return resultFactory.proceed("other");
        }
    }

    public static class Config {

        private String prefix;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

    private void setUpSpanlet(Class<? extends Spanlet<Config>> spanletClass) {

        fatJarSigletDefinitionConfigFactoryClass = new SigletConfigFileDefinitionMock();
        fatJarSigletDefinitionConfigFactoryClass.setSigletClassName(new StringValue(spanletClass.getName()));
        fatJarSigletDefinitionConfigFactoryClass.setConfigFactoryClassName(new StringValue(SigletConfigFactoryMock.class.getName()));
        fatJarSigletDefinitionConfigFactoryClass.setDestinations(List.of(new StringValue("default")));

        fatJarSigletDefinition = new FatJarSigletDefinition(Spanlet_configFactoryTest.class.getClassLoader(),
                fatJarSigletDefinitionConfigFactoryClass);


        spanletProcessorType = new SpanletProcessorType(fatJarSigletDefinition);
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