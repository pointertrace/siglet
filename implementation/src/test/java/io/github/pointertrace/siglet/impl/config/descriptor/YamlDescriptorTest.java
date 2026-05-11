package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.OtelGrpcExporterConfig;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action.GroovyActionConfig;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.grpc.OtelGrpcReceiverConfig;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class YamlDescriptorTest {

    private ReceiverTypeRegistry receiverTypeRegistry;

    private ProcessorTypeRegistry processorTypeRegistry;

    private ExporterTypeRegistry exporterTypeRegistry;

    @BeforeEach
    void setUp() {
        receiverTypeRegistry = new ReceiverTypeRegistry();
        processorTypeRegistry = new ProcessorTypeRegistry();
        exporterTypeRegistry = new ExporterTypeRegistry();
    }

    @Test
    void getValue() {

        String config = """
                global:
                  queue-size: 1
                  thread-pool-size: 2
                receivers:
                - grpc: first-receiver
                  config:
                    address: localhost:8080
                - grpc: second-receiver
                  config:
                    address: localhost:8081
                exporters:
                - grpc: first-exporter
                  config:
                    address: localhost:8080
                - grpc: second-exporter
                  config:
                    address: localhost:8081
                pipelines:
                - name: pipeline-name
                  from: first-receiver
                  start:
                  - spanlet-name
                  processors:
                  - spanlet-groovy-action: spanlet-name
                    to:
                    - first-exporter
                    config:
                      action: action-value
                """;

        Schema schema = YamlDescriptor.descriptorSchemaBuilder(receiverTypeRegistry, processorTypeRegistry,
                exporterTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        YamlDescriptor yamlDescriptor = factory.create(YamlDescriptor.class);

        // --- global config ---
        GlobalConfigDescriptor globalConfig = yamlDescriptor.getGlobalConfig();
        assertEquals(Location.of(2, 3), globalConfig.getLocation());
        assertEquals(1, globalConfig.getQueueSize().getValue().intValue());
        assertEquals(Location.of(2, 15), globalConfig.getQueueSize().getLocation());
        assertEquals(2, globalConfig.getThreadPoolSize().getValue().intValue());
        assertEquals(Location.of(3, 21), globalConfig.getThreadPoolSize().getLocation());

        // --- receivers ---
        assertEquals(2, yamlDescriptor.getReceivers().size());

        ReceiverDescriptor firstReceiver = yamlDescriptor.getReceivers().get(0);
        assertEquals("grpc", firstReceiver.getType().getValue());
        assertEquals(Location.of(5, 3), firstReceiver.getType().getLocation());
        assertEquals("first-receiver", firstReceiver.getName().getValue());
        assertEquals(Location.of(5, 9), firstReceiver.getName().getLocation());
        assertEquals(Location.of(5, 3), firstReceiver.getLocation());
        OtelGrpcReceiverConfig firstReceiverConfig = assertInstanceOf(OtelGrpcReceiverConfig.class, firstReceiver.getConfig());
        assertEquals("localhost", firstReceiverConfig.getAddress().getInetSocketAddress().getHostString());
        assertEquals(8080, firstReceiverConfig.getAddress().getInetSocketAddress().getPort());
        assertEquals(Location.of(7, 14), firstReceiverConfig.getAddress().getLocation());

        ReceiverDescriptor secondReceiver = yamlDescriptor.getReceivers().get(1);
        assertEquals("grpc", secondReceiver.getType().getValue());
        assertEquals(Location.of(8, 3), secondReceiver.getType().getLocation());
        assertEquals("second-receiver", secondReceiver.getName().getValue());
        assertEquals(Location.of(8, 9), secondReceiver.getName().getLocation());
        assertEquals(Location.of(8, 3), secondReceiver.getLocation());
        OtelGrpcReceiverConfig secondReceiverConfig = assertInstanceOf(OtelGrpcReceiverConfig.class, secondReceiver.getConfig());
        assertEquals("localhost", secondReceiverConfig.getAddress().getInetSocketAddress().getHostString());
        assertEquals(8081, secondReceiverConfig.getAddress().getInetSocketAddress().getPort());
        assertEquals(Location.of(10, 14), secondReceiverConfig.getAddress().getLocation());

        // --- exporters ---
        assertEquals(2, yamlDescriptor.getExporters().size());

        ExporterDescriptor firstExporter = yamlDescriptor.getExporters().get(0);
        assertEquals("grpc", firstExporter.getType().getValue());
        assertEquals(Location.of(12, 3), firstExporter.getType().getLocation());
        assertEquals("first-exporter", firstExporter.getName().getValue());
        assertEquals(Location.of(12, 9), firstExporter.getName().getLocation());
        assertEquals(Location.of(12, 3), firstExporter.getLocation());
        OtelGrpcExporterConfig firstExporterConfig = assertInstanceOf(OtelGrpcExporterConfig.class, firstExporter.getConfig());
        assertEquals("localhost", firstExporterConfig.getAddress().getInetSocketAddress().getHostString());
        assertEquals(8080, firstExporterConfig.getAddress().getInetSocketAddress().getPort());
        assertEquals(Location.of(14, 14), firstExporterConfig.getAddress().getLocation());

        ExporterDescriptor secondExporter = yamlDescriptor.getExporters().get(1);
        assertEquals("grpc", secondExporter.getType().getValue());
        assertEquals(Location.of(15, 3), secondExporter.getType().getLocation());
        assertEquals("second-exporter", secondExporter.getName().getValue());
        assertEquals(Location.of(15, 9), secondExporter.getName().getLocation());
        assertEquals(Location.of(15, 3), secondExporter.getLocation());
        OtelGrpcExporterConfig secondExporterConfig = assertInstanceOf(OtelGrpcExporterConfig.class, secondExporter.getConfig());
        assertEquals("localhost", secondExporterConfig.getAddress().getInetSocketAddress().getHostString());
        assertEquals(8081, secondExporterConfig.getAddress().getInetSocketAddress().getPort());
        assertEquals(Location.of(17, 14), secondExporterConfig.getAddress().getLocation());

        // --- pipelines ---
        assertEquals(1, yamlDescriptor.getPipelines().size());

        PipelineDescriptor pipeline = yamlDescriptor.getPipelines().get(0);
        assertEquals(Location.of(19, 3), pipeline.getLocation());
        assertEquals("pipeline-name", pipeline.getName().getValue());
        assertEquals(Location.of(19, 9), pipeline.getName().getLocation());
        assertEquals("first-receiver", pipeline.getFrom().getValue());
        assertEquals(Location.of(20, 9), pipeline.getFrom().getLocation());

        // start list
        assertEquals(1, pipeline.getStart().size());
        assertEquals("spanlet-name", pipeline.getStart().get(0).getValue());
        assertEquals(Location.of(22, 5), pipeline.getStart().get(0).getLocation());

        // processors
        assertEquals(1, pipeline.getProcessors().size());

        ProcessorDescriptor processor = pipeline.getProcessors().get(0);
        assertEquals(Location.of(24, 5), processor.getLocation());
        assertEquals("spanlet-groovy-action", processor.getType().getValue());
        assertEquals(Location.of(24, 5), processor.getType().getLocation());
        assertEquals("spanlet-name", processor.getName().getValue());
        assertEquals(Location.of(24, 28), processor.getName().getLocation());

        // processor "to" list
        assertEquals(1, processor.getTo().size());
        assertEquals("first-exporter", processor.getTo().get(0).getValue());
        assertEquals(Location.of(26, 7), processor.getTo().get(0).getLocation());

        // processor config
        GroovyActionConfig actionConfig = assertInstanceOf(GroovyActionConfig.class, processor.getConfig());
        assertEquals("action-value", actionConfig.getAction());
    }
}