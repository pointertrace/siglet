package com.siglet.config.item;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.schema.SchemaValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.ConfigCheckFactory.grpcExporterChecker;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GrpcExporterItemTest {

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
                batch-size-in-signals: 1
                batch-timeout-in-millis: 2
                """;


        Node node = parser.parse(config);

        grpcExporterChecker().check(node);

        Item item = node.getValue();

        assertNotNull(item);

        String expected = """
                (1:1)  GrpcExporterItem
                  (1:7)  name
                    (1:7)  String  (first)
                  (2:10)  address
                    (2:10)  InetSocketAddress  (localhost/<unresolved>:8080)
                  (3:24)  batch-size-in-signal
                    (3:24)  Integer  (1)
                  (4:26)  batch-timeout-in-millis
                    (4:26)  Integer  (2)
                """;

        assertEquals(expected, item.describe());
    }

}