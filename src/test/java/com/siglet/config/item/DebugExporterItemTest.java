package com.siglet.config.item;

import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.ConfigCheckFactory.debugExporterChecker;
import static org.junit.jupiter.api.Assertions.*;

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

        Object value = node.getValue();

        Item item = assertInstanceOf(Item.class, value);

        assertNotNull(item);

        String expected = """
        (1:1)  DebugExporterItem
          (1:8)  name: first
          (2:10)  address: direct:any
        """;

        assertEquals(expected, item.describe());
    }

    @Test
    void getValue() {

        var config = """
                debug: first
                address: direct:any
                """;


        Node node = parser.parse(config);

        debugExporterChecker().check(node);

        Object value = node.getValue();

        DebugExporterItem item = assertInstanceOf(DebugExporterItem.class, value);

        assertNotNull(item);
        assertEquals(Location.of(1,1), item.getLocation());

        assertEquals("first", item.getName());
        assertEquals(Location.of(1,8), item.getNameLocation());

        assertEquals("direct:any", item.getAddress());
        assertEquals(Location.of(2,10), item.getAddressLocation());

    }
}