package com.siglet.config.item;

import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.schema.SchemaValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static com.siglet.config.ConfigCheckFactory.grpcExporterChecker;
import static org.junit.jupiter.api.Assertions.*;

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

        Object value = node.getValue();

        Item item = assertInstanceOf(Item.class, value);

        assertNotNull(item);

        String expected = """
                (1:1)  GrpcExporter
                  (1:7)  name: first
                  (2:10)  address: localhost/<unresolved>:8080
                  (3:24)  batch-size-in-signal: 1
                  (4:26)  batch-timeout-in-millis: 2
                """;

        assertEquals(expected, item.describe());
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

        GrpcExporterItem item = assertInstanceOf(GrpcExporterItem.class, value);

        assertEquals("first",item.getName());
        assertEquals(Location.of(1,7), item.getNameLocation());

        assertEquals(InetSocketAddress.createUnresolved("localhost",8080),item.getAddress());
        assertEquals(Location.of(2,10),item.getAddressLocation());

        assertEquals(1,item.getBatchSizeInSignals());
        assertEquals(Location.of(3,24), item.getBatchSizeInSignalsLocation());

        assertEquals(2,item.getBatchTimeoutInMillis());
        assertEquals(Location.of(4,26), item.getBatchTimeoutInMillisLocation());

    }

}