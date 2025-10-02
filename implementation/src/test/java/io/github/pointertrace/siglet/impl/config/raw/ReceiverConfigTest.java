package io.github.pointertrace.siglet.impl.config.raw;

import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.grpc.OtelGrpcReceiverConfig;
import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static io.github.pointertrace.siglet.impl.config.ConfigCheckFactory.receiverChecker;
import static org.junit.jupiter.api.Assertions.*;

class ReceiverConfigTest {

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void getValue_debug() {

        var config = """
                debug: first
                """;

        Node node = parser.parse(config);

        receiverChecker(new ReceiverTypeRegistry()).check(node);

        Object value = node.getValue();

        ReceiverConfig receiverConfig = assertInstanceOf(ReceiverConfig.class, value);
        assertNull(receiverConfig.getConfig());


    }

    @Test
    void describe_debug() {

        var config = """
                debug: first
                """;

        Node node = parser.parse(config);

        receiverChecker(new ReceiverTypeRegistry()).check(node);

        Object value = node.getValue();

        Describable receiverConfig = assertInstanceOf(Describable.class, value);

        String expected = """
                (1:1)  receiverConfig:
                  (1:8)  name: first
                  (1:1)  type: debug""";

        assertEquals(expected, receiverConfig.describe(0));

    }

    @Test
    void getValue_grpc() {

        var config = """
                grpc: first
                config:
                  address: localhost:8080
                """;

        Node node = parser.parse(config);

        receiverChecker(new ReceiverTypeRegistry()).check(node);

        Object value = node.getValue();

        ReceiverConfig receiverConfig = assertInstanceOf(ReceiverConfig.class, value);
        assertNotNull(receiverConfig.getConfig());
        OtelGrpcReceiverConfig otelGrpcReceiverConfig = assertInstanceOf(OtelGrpcReceiverConfig.class,
                receiverConfig.getConfig());
        assertEquals(new InetSocketAddress("localhost", 8080), otelGrpcReceiverConfig.getAddress());

    }

    @Test
    void describe_grpc() {

        var config = """
                grpc: first
                config:
                  address: localhost:8080
                """;

        Node node = parser.parse(config);

        receiverChecker(new ReceiverTypeRegistry()).check(node);

        Object value = node.getValue();

        Describable receiverConfig = assertInstanceOf(Describable.class, value);

        String expected = """
                (1:1)  receiverConfig:
                  (1:7)  name: first
                  (1:1)  type: grpc
                  (2:1)  config: (OtelGrpcReceiverConfig)
                    (3,12)  address: localhost/127.0.0.1:8080""";

        assertEquals(expected, receiverConfig.describe(0));
    }



}