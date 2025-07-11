package com.siglet.container.engine.pipeline.processor.groovy.filter;

import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.parser.Node;
import com.siglet.parser.NodeChecker;
import com.siglet.parser.YamlParser;
import com.siglet.parser.located.Location;
import com.siglet.parser.schema.PropertyChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.parser.schema.SchemaFactory.strictObject;
import static org.junit.jupiter.api.Assertions.*;

class GroovyFilterConfigTest {
    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }


    @Test
    void describe() {

        String configTxt = """
                config:
                  expression: expression value
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(ProcessorConfig::new,
                (PropertyChecker) new GroovyFilterDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        ProcessorConfig siglet = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(siglet.getConfig());
        GroovyFilterConfig groovyFilterConfig = assertInstanceOf(GroovyFilterConfig.class, siglet.getConfig());

        String expected = """
                (1:1)  groovyFilterConfig:
                  (2:15)  expression: expression value""";

        assertEquals(expected, groovyFilterConfig.describe(0));

    }


    @Test
    void getValue() {


        String configTxt = """
                config:
                  expression: expression value
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(ProcessorConfig::new,
                (PropertyChecker) new GroovyFilterDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        ProcessorConfig siglet = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(siglet.getConfig());

        GroovyFilterConfig groovyFilterConfig = assertInstanceOf(GroovyFilterConfig.class, siglet.getConfig());


        assertEquals(Location.of(1, 1), groovyFilterConfig.getLocation());

        assertEquals("expression value", groovyFilterConfig.getExpression());
        assertEquals(Location.of(2, 15), groovyFilterConfig.getExpressionLocation());

    }

}