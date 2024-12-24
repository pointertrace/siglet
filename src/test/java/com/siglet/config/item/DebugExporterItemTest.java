package com.siglet.config.item;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.ConfigCheckFactory.debugExporterChecker;
import static com.siglet.config.ConfigCheckFactory.grpcExporterChecker;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DebugExporterItemTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void describe() {

        var config = """
                debug: first
                address: direct:any
                """;


        Node node = parser.parse(config);

        debugExporterChecker().check(node);

        Item item = node.getValue();

        assertNotNull(item);

        String expected = """
        (1:1)  DebugExporterItem
          (2:10)  address
            (2:10)  String  (direct:any)""";

        assertEquals(expected, item.describe());
    }

}