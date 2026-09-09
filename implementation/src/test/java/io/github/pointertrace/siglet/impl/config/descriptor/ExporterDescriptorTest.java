package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.OtelGrpcExporterConfig;
import io.github.pointertrace.siglet.parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExporterDescriptorTest {

    ExporterTypeRegistry exporterTypeRegistry;

    @BeforeEach
    public void setUp() {

        exporterTypeRegistry = new ExporterTypeRegistry();
    }

    @Test
    void parseGrpcExporter_onlyRequired() {
        var config = """
                grpc: exporter-name
                config:
                  address: localhost:4317
                """;

        Schema schema = ExporterDescriptor.descriptorSchemaBuilder(exporterTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ExporterDescriptor exporterDescriptor = factory.create(ExporterDescriptor.class);

        assertEquals("grpc", exporterDescriptor.getType().getValue());
        assertEquals(Location.of(1, 1), exporterDescriptor.getType().getLocation());
        assertEquals("exporter-name", exporterDescriptor.getName().getValue());
        assertEquals(Location.of(1, 7), exporterDescriptor.getName().getLocation());

        OtelGrpcExporterConfig grpcConfig = assertInstanceOf(OtelGrpcExporterConfig.class, exporterDescriptor.getConfig());
        assertEquals("localhost", grpcConfig.getAddress().getInetSocketAddress().getHostString());
        assertEquals(4317, grpcConfig.getAddress().getInetSocketAddress().getPort());
        assertEquals(Location.of(3, 12), grpcConfig.getAddress().getLocation());
        assertEquals(1000,grpcConfig.getBatchSizeInSignals().getValue().intValue());
        assertEquals(1000,grpcConfig.getBatchTimeoutInMillis().getValue().intValue());
        assertNull(grpcConfig.getQueueSize());
    }

    @Test
    void parseGrpcExporter_all() {
        var config = """
                grpc: exporter-name
                config:
                  address: localhost:4317
                  batch-size-in-signals: 1
                  batch-timeout-in-millis: 2
                  queue-size: 3
                """;

        Schema schema = ExporterDescriptor.descriptorSchemaBuilder(exporterTypeRegistry).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ExporterDescriptor exporterDescriptor = factory.create(ExporterDescriptor.class);

        assertEquals("grpc", exporterDescriptor.getType().getValue());
        assertEquals(Location.of(1, 1), exporterDescriptor.getType().getLocation());
        assertEquals("exporter-name", exporterDescriptor.getName().getValue());
        assertEquals(Location.of(1, 7), exporterDescriptor.getName().getLocation());

        OtelGrpcExporterConfig grpcConfig = assertInstanceOf(OtelGrpcExporterConfig.class, exporterDescriptor.getConfig());
        assertEquals("localhost", grpcConfig.getAddress().getInetSocketAddress().getHostString());
        assertEquals(4317, grpcConfig.getAddress().getInetSocketAddress().getPort());
        assertEquals(Location.of(3, 12), grpcConfig.getAddress().getLocation());
        assertEquals(1,grpcConfig.getBatchSizeInSignals().getValue().intValue());
        assertEquals(Location.of(4, 26), grpcConfig.getBatchSizeInSignals().getLocation());
        assertEquals(2,grpcConfig.getBatchTimeoutInMillis().getValue().intValue());
        assertEquals(Location.of(5, 28), grpcConfig.getBatchTimeoutInMillis().getLocation());
        assertEquals(3,grpcConfig.getQueueSize().getValue().intValue());
        assertEquals(Location.of(6, 15), grpcConfig.getQueueSize().getLocation());
    }

    @Test
    void parseDebugExporter() {
        var config = """
                debug: exporter-name
                """;

        Schema schema = ExporterDescriptor.descriptorSchemaBuilder(new ExporterTypeRegistry()).build();

        Node node = Parser.DEFAULT.parse(config);

        Factory factory = schema.validate(node);

        ExporterDescriptor exporterDescriptor = factory.create(ExporterDescriptor.class);

        assertEquals("debug", exporterDescriptor.getType().getValue());
        assertEquals(Location.of(1, 1), exporterDescriptor.getType().getLocation());
        assertEquals("exporter-name", exporterDescriptor.getName().getValue());
        assertEquals(Location.of(1, 8), exporterDescriptor.getName().getLocation());
        assertNull(exporterDescriptor.getConfig());
    }

}