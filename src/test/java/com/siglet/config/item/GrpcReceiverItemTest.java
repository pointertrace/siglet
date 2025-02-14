package com.siglet.config.item;

import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static com.siglet.config.ConfigCheckFactory.grpcReceiverChecker;
import static org.junit.jupiter.api.Assertions.*;

class GrpcReceiverItemTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void describe() {

        var config = """
                grpc: first
                address: localhost:8080
                signal: metric
                """;


        Node node = parser.parse(config);

        grpcReceiverChecker().check(node);

        Object value = node.getValue();

        GrpcReceiverItem item = assertInstanceOf(GrpcReceiverItem.class, value);

        assertNotNull(item);

        String expected = """
                (1:1)  GrpcReceiver
                  (1:7)  name: first
                  (2:10)  address: localhost/<unresolved>:8080
                  (3:9)  signal: metric
                """;

        assertEquals(expected, item.describe());
    }

    @Test
    void getValue() {

        var config = """
                grpc: first
                address: localhost:8080
                signal: metric
                """;


        Node node = parser.parse(config);

        grpcReceiverChecker().check(node);

        Object value = node.getValue();

        GrpcReceiverItem item = assertInstanceOf(GrpcReceiverItem.class, value);

        assertNotNull(item);
        assertEquals(Location.of(1, 1), item.getLocation());

        assertEquals("first", item.getName());
        assertEquals(Location.of(1, 7), item.getNameLocation());

        assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), item.getAddress());
        assertEquals(Location.of(2, 10), item.getAddressLocation());

        assertEquals(Signal.METRIC, item.getSignal());
        assertEquals(Location.of(3, 9), item.getSignalTypeLocation());
    }
}