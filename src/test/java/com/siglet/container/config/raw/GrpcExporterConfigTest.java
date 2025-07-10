package com.siglet.container.config.raw;

import com.siglet.parser.Node;
import com.siglet.parser.located.Location;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static com.siglet.container.config.ConfigCheckFactory.grpcExporterChecker;
import static org.junit.jupiter.api.Assertions.*;

class GrpcExporterConfigTest {

    private YamlParser parser;

    @BeforeEach
    public void setUp() {
        parser = new YamlParser();
    }

    @Test
    void describe() {

        var config = """
                grpc: first
                address: localhost:8080
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
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

    }

}