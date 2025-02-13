package com.siglet.config.item;

import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.ConfigCheckFactory.*;
import static org.junit.jupiter.api.Assertions.*;

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

        Object value = node.getValue();
        DebugReceiverItem debugReceiverItem = assertInstanceOf(DebugReceiverItem.class, value);

        assertNotNull(debugReceiverItem);

        String expected = """
        (1:1)  DebugReceiverItem
          (1:8)  name: first
          (2:10)  address: mock:test
        """;

        assertEquals(expected, debugReceiverItem.describe());
    }

    @Test
    void getValue() {

        var config = """
                debug: first
                address: mock:test
                """;


        Node node = parser.parse(config);

        debugReceiverChecker().check(node);

        Object value = node.getValue();

        DebugReceiverItem item = assertInstanceOf(DebugReceiverItem.class, value);

        assertNotNull(item);
        assertEquals(Location.of(1,1), item.getLocation());

        assertEquals("first", item.getName());
        assertEquals(Location.of(1,8), item.getNameLocation());

        assertEquals("mock:test", item.getAddress());
        assertEquals(Location.of(2,10), item.getAddressLocation());

    }
}