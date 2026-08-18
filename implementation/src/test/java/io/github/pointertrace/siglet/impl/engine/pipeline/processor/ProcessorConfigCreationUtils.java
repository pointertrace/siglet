package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.config.Config;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.event.NoopEventBus;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet.SpanletProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultFactoryImpl;
import io.github.pointertrace.siglet.parser.Factory;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.Parser;
import io.github.pointertrace.siglet.parser.Schema;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.string;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProcessorConfigCreationUtils {

    private ProcessorConfigCreationUtils() {
    }


    public static SigletContext creteSigletContext(ProcessorDescriptor processorDescriptor) {

        Config configMock = mock(Config.class);
        SigletContext sigletContextMock = mock(SigletContext.class);
        when(sigletContextMock.getEventBus()).thenReturn(new NoopEventBus());
        when(sigletContextMock.getConfig()).thenReturn(configMock);
        when(configMock.getQueueSize(processorDescriptor)).thenReturn(1);
        when(configMock.getThreadPoolSize(processorDescriptor)).thenReturn(1);

        ResultFactoryImpl.init();

        return sigletContextMock;
    }


    public static ProcessorDescriptor createProcessorDescriptor(String config) {
        return createProcessorDescriptor(config, null, null, null);
    }

    public static ProcessorDescriptor createProcessorDescriptor(String config, String sigletName, Spanlet<?> processor,
                                                                ConfigurationFactory<?> configurationFactory) {

        ProcessorTypeRegistry processorTypeRegistry = new ProcessorTypeRegistry();

        if (sigletName != null) {
            processorTypeRegistry.register(new SpanletProcessorType<>(
                    new SigletDefinitionMock(sigletName, processor, configurationFactory)));
        }

        Schema schema = ProcessorDescriptor.descriptorSchemaBuilder(processorTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        return factory.create(ProcessorDescriptor.class);
    }

    public static ProcessorNode createProcessorNode(ProcessorDescriptor processorDescriptor) {

        return new ProcessorNode(processorDescriptor);
    }

    private static class SigletDefinitionMock implements SigletDefinition {

        private final String name;

        private final Spanlet<?> processor;

        private final ConfigurationFactory<?> configurationFactory;

        private SigletDefinitionMock(String name, Spanlet<?> processor, ConfigurationFactory<?> configurationFactory) {
            this.name = name;
            this.processor = processor;
            this.configurationFactory = configurationFactory;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Spanlet<?> createProcessor() {
            return processor;
        }

        @Override
        public ConfigurationFactory<?> createConfigurationFactory() {
            return configurationFactory;
        }
    }
}
