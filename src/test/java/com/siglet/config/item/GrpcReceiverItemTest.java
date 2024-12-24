package com.siglet.config.item;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.ConfigCheckFactory.grpcReceiverChecker;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                signal-type: metric
                """;


        Node node = parser.parse(config);

        grpcReceiverChecker().check(node);

        Item item = node.getValue();

        assertNotNull(item);

        String expected = """
        (1:1)  GrpcReceiverItem
          (1:7)  name
            (1:7)  String  (first)
          (2:10)  address
            (2:10)  InetSocketAddress  (localhost/<unresolved>:8080)
          (3:14)  signal-type
            (3:14)  String  (metric)""";

        assertEquals(expected, item.describe());
    }

}