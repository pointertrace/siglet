package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.impl.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.schema.PropertyChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.strictObject;
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