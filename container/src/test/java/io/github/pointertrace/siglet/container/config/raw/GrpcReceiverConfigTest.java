package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static io.github.pointertrace.siglet.container.config.ConfigCheckFactory.grpcReceiverChecker;
import static org.junit.jupiter.api.Assertions.*;

class GrpcReceiverConfigTest {

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
                """;


        Node node = parser.parse(config);

        grpcReceiverChecker().check(node);

        Object value = node.getValue();

        GrpcReceiverConfig grpcReceiverConfig = assertInstanceOf(GrpcReceiverConfig.class, value);

        assertNotNull(grpcReceiverConfig);

        String expected = """
                (1:1)  GrpcReceiverConfig
                  (1:7)  name: first
                  (2:10)  address: localhost/127.0.0.1:8080
                """;

        assertEquals(expected, grpcReceiverConfig.describe());
    }

    @Test
    void getValue() {

        var config = """
                grpc: first
                address: localhost:8080
                """;


        Node node = parser.parse(config);

        grpcReceiverChecker().check(node);

        Object value = node.getValue();

        GrpcReceiverConfig grpcReceiverConfig = assertInstanceOf(GrpcReceiverConfig.class, value);

        assertNotNull(grpcReceiverConfig);
        assertEquals(Location.of(1, 1), grpcReceiverConfig.getLocation());

        assertEquals("first", grpcReceiverConfig.getName());
        assertEquals(Location.of(1, 7), grpcReceiverConfig.getNameLocation());

        assertEquals(new InetSocketAddress("localhost", 8080), grpcReceiverConfig.getAddress());
        assertEquals(Location.of(2, 10), grpcReceiverConfig.getAddressLocation());

    }
}