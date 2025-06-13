package com.siglet.container.config.raw;

import com.siglet.api.parser.Node;
import com.siglet.api.parser.located.Location;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.container.config.ConfigCheckFactory.debugExporterChecker;
import static org.junit.jupiter.api.Assertions.*;

class DebugExporterConfigTest {

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

        debugExporterChecker().check(node);

        Object value = node.getValue();

        DebugExporterConfig debugExporterConfig = assertInstanceOf(DebugExporterConfig.class, value);

        assertNotNull(debugExporterConfig);

        String expected = """
        (1:1)  DebugExporterConfig
          (1:8)  name: first
        """;

        assertEquals(expected, debugExporterConfig.describe());
    }

    @Test
    void getValue() {

        var config = """
                debug: first
                """;


        Node node = parser.parse(config);

        debugExporterChecker().check(node);

        Object value = node.getValue();

        DebugExporterConfig debugExporterConfig = assertInstanceOf(DebugExporterConfig.class, value);

        assertNotNull(debugExporterConfig);
        assertEquals(Location.of(1,1), debugExporterConfig.getLocation());

        assertEquals("first", debugExporterConfig.getName());
        assertEquals(Location.of(1,8), debugExporterConfig.getNameLocation());

    }
}