package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siget.parser.Node;
import io.github.pointertrace.siget.parser.YamlParser;
import io.github.pointertrace.siget.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static io.github.pointertrace.siglet.container.config.ConfigCheckFactory.grpcExporterChecker;
import static org.junit.jupiter.api.Assertions.*;

class GrpcExporterConfigTest {

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void describe() {

        var config = """
                grpc: first
                address: localhost:8080
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
                queue-size: 3
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Object value = node.getValue();
        GrpcExporterConfig grpcExporterConfig = assertInstanceOf(GrpcExporterConfig.class, value);

        assertNotNull(grpcExporterConfig);

        String expected = """
                (1:1)  GrpcExporterConfig
                  (1:7)  name: first
                  (2:10)  address: localhost/127.0.0.1:8080
                  (3:24)  batch-size-in-signal: 1
                  (4:26)  batch-timeout-in-millis: 2
                  (5:13)  queue-size: 3
                """;

        assertEquals(expected, grpcExporterConfig.describe());
    }

    @Test
    void describe_required() {

        var config = """
                grpc: first
                address: localhost:8080
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Object value = node.getValue();
        GrpcExporterConfig grpcExporterConfig = assertInstanceOf(GrpcExporterConfig.class, value);

        assertNotNull(grpcExporterConfig);

        String expected = """
                (1:1)  GrpcExporterConfig
                  (1:7)  name: first
                  (2:10)  address: localhost/127.0.0.1:8080
                """;

        assertEquals(expected, grpcExporterConfig.describe());
    }

    @Test
    void getValue() {

        var config = """
                grpc: first
                address: localhost:8080
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
                queue-size: 3
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Object value = node.getValue();

        GrpcExporterConfig grpcExporterConfig = assertInstanceOf(GrpcExporterConfig.class, value);

        assertEquals("first",grpcExporterConfig.getName());
        assertEquals(Location.of(1,7), grpcExporterConfig.getNameLocation());

        assertEquals(new InetSocketAddress("localhost",8080),grpcExporterConfig.getAddress());
        assertEquals(Location.of(2,10),grpcExporterConfig.getAddressLocation());

        assertEquals(1,grpcExporterConfig.getBatchSizeInSignals());
        assertEquals(Location.of(3,24), grpcExporterConfig.getBatchSizeInSignalsLocation());

        assertEquals(2,grpcExporterConfig.getBatchTimeoutInMillis());
        assertEquals(Location.of(4,26), grpcExporterConfig.getBatchTimeoutInMillisLocation());

        assertEquals(3,grpcExporterConfig.getQueueSize());
        assertEquals(Location.of(5,13), grpcExporterConfig.getQueueSizeLocation());
    }

    @Test
    void getValue_required() {

        var config = """
                grpc: first
                address: localhost:8080
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Object value = node.getValue();

        GrpcExporterConfig grpcExporterConfig = assertInstanceOf(GrpcExporterConfig.class, value);

        assertEquals("first",grpcExporterConfig.getName());
        assertEquals(Location.of(1,7), grpcExporterConfig.getNameLocation());

        assertEquals(new InetSocketAddress("localhost",8080),grpcExporterConfig.getAddress());
        assertEquals(Location.of(2,10),grpcExporterConfig.getAddressLocation());

        assertEquals(1,grpcExporterConfig.getBatchSizeInSignals());
        assertEquals(0,grpcExporterConfig.getBatchTimeoutInMillis());

        assertNull(grpcExporterConfig.getQueueSize());
    }
    @Test
    void getQueueSize_rawConfigNull() {

        String config = """
                grpc: first
                address: localhost:8080
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Object value = node.getValue();

        GrpcExporterConfig grpcExporterConfig = assertInstanceOf(GrpcExporterConfig.class, value);

        SigletError e = assertThrows(SigletError.class, grpcExporterConfig::getQueueSizeConfig);
        assertEquals("rawConfig is null", e.getMessage());

    }

    @Test
    void getQueueSize_rawConfigGlobalConfigNull() {


        String config = """
                grpc: first
                address: localhost:8080
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Object value = node.getValue();

        GrpcExporterConfig grpcExporterConfig = assertInstanceOf(GrpcExporterConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        grpcExporterConfig.setRawConfig(rawConfig);

        assertEquals(QueueSizeConfig.defaultConfig().getQueueSize(), grpcExporterConfig.getQueueSizeConfig().getQueueSize());

    }

    @Test
    void getQueueSize_rawConfigNotNullGlobalConfigEmpty() {

        String config = """
                grpc: first
                address: localhost:8080
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Object value = node.getValue();

        GrpcExporterConfig grpcExporterConfig = assertInstanceOf(GrpcExporterConfig.class, value);


        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        rawConfig.setGlobalConfig(globalConfig);
        grpcExporterConfig.setRawConfig(rawConfig);

        assertEquals(QueueSizeConfig.defaultConfig().getQueueSize(), grpcExporterConfig.getQueueSizeConfig().getQueueSize());

    }

    @Test
    void getQueueSize_rawConfigNotNullGlobalConfigDefined() {

        String config = """
                grpc: first
                address: localhost:8080
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Object value = node.getValue();

        GrpcExporterConfig grpcExporterConfig = assertInstanceOf(GrpcExporterConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setQueueSize(10);
        rawConfig.setGlobalConfig(globalConfig);
        grpcExporterConfig.setRawConfig(rawConfig);

        assertEquals(10, grpcExporterConfig.getQueueSizeConfig().getQueueSize());

    }

    @Test
    void getQueueSize_rawConfigNotNullGlobalConfigDefinedProcessorDefined() {

        String config = """
                grpc: first
                address: localhost:8080
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
                queue-size: 20
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Object value = node.getValue();

        GrpcExporterConfig grpcExporterConfig = assertInstanceOf(GrpcExporterConfig.class, value);


        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setQueueSize(10);
        rawConfig.setGlobalConfig(globalConfig);
        grpcExporterConfig.setRawConfig(rawConfig);

        assertEquals(20, grpcExporterConfig.getQueueSizeConfig().getQueueSize());

    }
}