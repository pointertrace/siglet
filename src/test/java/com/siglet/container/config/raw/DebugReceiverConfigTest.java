package com.siglet.container.config.raw;

import com.siglet.api.parser.Node;
import com.siglet.api.parser.located.Location;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.container.config.ConfigCheckFactory.debugReceiverChecker;
import static org.junit.jupiter.api.Assertions.*;

class DebugReceiverConfigTest {

    private YamlParser parser;

    @BeforeEach
    public void setUp() {
        parser = new YamlParser();
    }

    @Test
    void describe() {

        var config = """
                debug: first
                """;

        Node node = parser.parse(config);

        debugReceiverChecker().check(node);

        Object value = node.getValue();
        DebugReceiverConfig debugReceiverConfig = assertInstanceOf(DebugReceiverConfig.class, value);

        assertNotNull(debugReceiverConfig);

        String expected = """
        (1:1)  DebugReceiverConfig
          (1:8)  name: first
        """;

        assertEquals(expected, debugReceiverConfig.describe());
    }

    @Test
    void getValue() {

        var config = """
                debug: first
                """;


        Node node = parser.parse(config);

        debugReceiverChecker().check(node);

        Object value = node.getValue();

        DebugReceiverConfig debugReceiverConfig = assertInstanceOf(DebugReceiverConfig.class, value);

        assertNotNull(debugReceiverConfig);
        assertEquals(Location.of(1,1), debugReceiverConfig.getLocation());

        assertEquals("first", debugReceiverConfig.getName());
        assertEquals(Location.of(1,8), debugReceiverConfig.getNameLocation());

    }
}