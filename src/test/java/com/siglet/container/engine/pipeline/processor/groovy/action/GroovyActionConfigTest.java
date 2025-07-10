package com.siglet.container.engine.pipeline.processor.groovy.action;

import com.siglet.parser.Node;
import com.siglet.parser.NodeChecker;
import com.siglet.parser.located.Location;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.parser.YamlParser;
import com.siglet.parser.schema.PropertyChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.parser.schema.SchemaFactory.strictObject;
import static org.junit.jupiter.api.Assertions.*;

class GroovyActionConfigTest {
    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }


    @Test
    void describe() {

        String configTxt = """
                config:
                  action: action value
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(ProcessorConfig::new,
                (PropertyChecker) new GroovyActionDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        ProcessorConfig siglet = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(siglet.getConfig());
        GroovyActionConfig groovyActionConfig = assertInstanceOf(GroovyActionConfig.class, siglet.getConfig());

        String expected = """
                (1:1)  groovyActionConfig:
                  (2:11)  action: action value""";

        assertEquals(expected, groovyActionConfig.describe(0));

    }

    @Test
    void getValue() {


        String configTxt = """
                config:
                  action: action value
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(ProcessorConfig::new,
                (PropertyChecker) new GroovyActionDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        ProcessorConfig siglet = assertInstanceOf(ProcessorConfig.class, value);


        assertNotNull(siglet.getConfig());
        GroovyActionConfig groovyActionConfig = assertInstanceOf(GroovyActionConfig.class, siglet.getConfig());

        assertEquals("action value", groovyActionConfig.getAction());
        assertEquals(Location.of(2, 11), groovyActionConfig.getActionLocation());
        assertEquals(Location.of(1, 1), groovyActionConfig.getLocation());


    }

}