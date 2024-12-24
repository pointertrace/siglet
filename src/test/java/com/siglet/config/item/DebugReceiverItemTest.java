package com.siglet.config.item;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.ConfigCheckFactory.debugReceiverChecker;
import static com.siglet.config.ConfigCheckFactory.grpcReceiverChecker;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DebugReceiverItemTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void describe() {

        var config = """
                debug: first
                address: mock:test
                """;

        Node node = parser.parse(config);

        debugReceiverChecker().check(node);

        Item item = node.getValue();

        assertNotNull(item);

        String expected = """
        (1:1)  DebugReceiverItem
          (1:8)  name
            (1:8)  String  (first)
          (2:10)  address
            (2:10)  String  (mock:test)""";

        assertEquals(expected, item.describe());
    }

}