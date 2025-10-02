package io.github.pointertrace.siglet.impl.config.raw;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.OtelGrpcExporterConfig;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static io.github.pointertrace.siglet.impl.config.ConfigCheckFactory.exporterChecker;
import static org.junit.jupiter.api.Assertions.*;

class ExporterConfigTest {

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void describe_grpc() {

        var config = """
                grpc: first
                config:
                  address: localhost:8080
                  batch-size-in-signals: 1
                  batch-timeout-in-millis: 2
                  queue-size: 3
                """;


        Node node = parser.parse(config);

        exporterChecker(new ExporterTypeRegistry()).check(node);

        Object value = node.getValue();
        ExporterConfig exporterConfig = assertInstanceOf(ExporterConfig.class, value);
        assertNotNull(exporterConfig);


        String expected = """
                (1:1)  exporterConfig:
                  (1:7)  name: first
                  (1:1)  type: grpc
                  (2:1)  config: (OtelGrpcExporterConfig)
                    (3:12)  address: localhost/127.0.0.1:8080
                    (4:26)  batch-size-in-signal: 1
                    (5:28)  batch-timeout-in-millis: 2
                    (6:15)  queue-size: 3
                """;

        assertEquals(expected, exporterConfig.describe());
    }

    @Test
    void describe_grpcRequired() {

        var config = """
                grpc: first
                config:
                  address: localhost:8080
                """;


        Node node = parser.parse(config);

        exporterChecker(new ExporterTypeRegistry()).check(node);

        Object value = node.getValue();
        ExporterConfig grpcExporterConfig = assertInstanceOf(ExporterConfig.class, value);

        assertNotNull(grpcExporterConfig);

        String expected = """
                (1:1)  exporterConfig:
                  (1:7)  name: first
                  (1:1)  type: grpc
                  (2:1)  config: (OtelGrpcExporterConfig)
                    (3:12)  address: localhost/127.0.0.1:8080
                """;

        assertEquals(expected, grpcExporterConfig.describe());
    }

    @Test
    void getValue_grpc() {

        var config = """
                grpc: first
                config:
                  address: localhost:8080
                  batch-size-in-signals: 1
                  batch-timeout-in-millis: 2
                  queue-size: 3
                """;


        Node node = parser.parse(config);

        exporterChecker(new ExporterTypeRegistry()).check(node);

        Object value = node.getValue();

        ExporterConfig grpcExporterConfig = assertInstanceOf(ExporterConfig.class, value);

        assertEquals("first", grpcExporterConfig.getName());
        assertEquals(Location.of(1, 7), grpcExporterConfig.getNameLocation());
        assertEquals("grpc", grpcExporterConfig.getType());

        OtelGrpcExporterConfig otelGrpcExporterConfig = assertInstanceOf(OtelGrpcExporterConfig.class, grpcExporterConfig.getConfig());
        assertEquals(new InetSocketAddress("localhost",8080),otelGrpcExporterConfig.getAddress());
        assertEquals(Location.of(3,12),otelGrpcExporterConfig.getAddressLocation());

        assertEquals(1,otelGrpcExporterConfig.getBatchSizeInSignals());
        assertEquals(Location.of(4,26), otelGrpcExporterConfig.getBatchSizeInSignalsLocation());

        assertEquals(2,otelGrpcExporterConfig.getBatchTimeoutInMillis());
        assertEquals(Location.of(5,28), otelGrpcExporterConfig.getBatchTimeoutInMillisLocation());

        assertEquals(3,otelGrpcExporterConfig.getQueueSize());
        assertEquals(Location.of(6,15), otelGrpcExporterConfig.getQueueSizeLocation());
    }

    @Test
    void getValue_grpcRequired() {

        var config = """
                grpc: first
                config:
                  address: localhost:8080
                """;


        Node node = parser.parse(config);

        exporterChecker(new ExporterTypeRegistry()).check(node);

        Object value = node.getValue();

        ExporterConfig grpcExporterConfig = assertInstanceOf(ExporterConfig.class, value);

        assertEquals("first", grpcExporterConfig.getName());
        assertEquals(Location.of(1, 7), grpcExporterConfig.getNameLocation());

        OtelGrpcExporterConfig otelGrpcExporterConfig = assertInstanceOf(OtelGrpcExporterConfig.class,
                grpcExporterConfig.getConfig());
        assertEquals(new InetSocketAddress("localhost",8080),otelGrpcExporterConfig.getAddress());
        assertEquals(Location.of(3,12),otelGrpcExporterConfig.getAddressLocation());

        assertEquals(1,otelGrpcExporterConfig.getBatchSizeInSignals());
        assertEquals(0,otelGrpcExporterConfig.getBatchTimeoutInMillis());

        assertNull(otelGrpcExporterConfig.getQueueSize());
    }

    @Test
    void getQueueSize_grpcRawConfigNull() {

        String config = """
                grpc: first
                config:
                  address: localhost:8080
                  batch-size-in-signals: 1
                  batch-timeout-in-millis: 2
                """;


        Node node = parser.parse(config);

        exporterChecker(new ExporterTypeRegistry()).check(node);

        Object value = node.getValue();

        ExporterConfig grpcExporterConfig = assertInstanceOf(ExporterConfig.class, value);

        SigletError e = assertThrows(SigletError.class, grpcExporterConfig::getQueueSizeConfig);
        assertEquals("rawConfig is null", e.getMessage());

    }

    @Test
    void getQueueSize_grpcRawConfigGlobalConfigNull() {


        String config = """
                grpc: first
                config:
                  address: localhost:8080
                  batch-size-in-signals: 1
                  batch-timeout-in-millis: 2
                """;


        Node node = parser.parse(config);

        exporterChecker(new ExporterTypeRegistry()).check(node);

        Object value = node.getValue();

        ExporterConfig grpcExporterConfig = assertInstanceOf(ExporterConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        grpcExporterConfig.setRawConfig(rawConfig);

        assertEquals(QueueSizeConfig.defaultConfig().getQueueSize(), grpcExporterConfig.getQueueSizeConfig().getQueueSize());

    }

    @Test
    void getQueueSize_grpcRawConfigNotNullGlobalConfigEmpty() {

        String config = """
                grpc: first
                config:
                  address: localhost:8080
                  batch-size-in-signals: 1
                  batch-timeout-in-millis: 2
                """;


        Node node = parser.parse(config);

        exporterChecker(new ExporterTypeRegistry()).check(node);

        Object value = node.getValue();

        ExporterConfig grpcExporterConfig = assertInstanceOf(ExporterConfig.class, value);


        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        rawConfig.setGlobalConfig(globalConfig);
        grpcExporterConfig.setRawConfig(rawConfig);

        assertEquals(QueueSizeConfig.defaultConfig().getQueueSize(), grpcExporterConfig.getQueueSizeConfig().getQueueSize());

    }

    @Test
    void getQueueSize_grpcRawConfigNotNullGlobalConfigDefined() {

        String config = """
                grpc: first
                config:
                  address: localhost:8080
                  batch-size-in-signals: 1
                  batch-timeout-in-millis: 2
                """;


        Node node = parser.parse(config);

        exporterChecker(new ExporterTypeRegistry()).check(node);
        Object value = node.getValue();

        ExporterConfig grpcExporterConfig = assertInstanceOf(ExporterConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setQueueSize(10);
        rawConfig.setGlobalConfig(globalConfig);
        grpcExporterConfig.setRawConfig(rawConfig);

        assertEquals(10, grpcExporterConfig.getQueueSizeConfig().getQueueSize());

    }

    @Test
    void getQueueSize_grpcRawConfigNotNullGlobalConfigDefinedProcessorDefined() {

        String config = """
                grpc: first
                config:
                  address: localhost:8080
                  batch-size-in-signals: 1
                  batch-timeout-in-millis: 2
                  queue-size: 20
                """;


        Node node = parser.parse(config);

        exporterChecker(new ExporterTypeRegistry()).check(node);

        Object value = node.getValue();

        ExporterConfig grpcExporterConfig = assertInstanceOf(ExporterConfig.class, value);
        OtelGrpcExporterConfig otelGrpcExporterConfig = assertInstanceOf(OtelGrpcExporterConfig.class, grpcExporterConfig.getConfig());


        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setQueueSize(10);
        rawConfig.setGlobalConfig(globalConfig);
        grpcExporterConfig.setRawConfig(rawConfig);

        assertEquals(20, otelGrpcExporterConfig.getQueueSizeConfig().getQueueSize());

    }
}