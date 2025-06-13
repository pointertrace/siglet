package com.siglet.container.config.raw;

import com.siglet.api.parser.Node;
import com.siglet.api.parser.located.Location;
import com.siglet.container.engine.pipeline.processor.groovy.action.GroovyActionConfig;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.container.config.ConfigCheckFactory.sigletChecker;
import static org.junit.jupiter.api.Assertions.*;

class ProcessorConfigTest {

    private YamlParser parser;

    @BeforeEach
    public void setUp() {
        parser = new YamlParser();

    }


    @Test
    void describe_toList() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to:
                - first exporter
                - second exporter
                type: groovy-action
                config:
                  action: signal.name = signal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        sigletChecker().check(node);

        Object value = node.getValue();
        ProcessorConfig sigletConfig = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(sigletConfig);

        String expected = """
        (1:1)  processorConfig:
          (1:7)  name: spanlet node
          (2:7)  kind: SPANLET
          (3:1)  to:
            (4:3)  first exporter
            (5:3)  second exporter
          (6:7)  type: groovy-action
          (7:1) config:
            (7:1)  groovyActionConfig:
              (8:11)  action: signal.name = signal.name +"-suffix" """;

        assertEquals(expected, sigletConfig.describe());

    }


    @Test
    void getValue_toList() {


        String configTxt = """
                name: spanlet node
                kind: spanlet
                to:
                - first exporter
                - second exporter
                type: groovy-action
                config:
                  action: signal.name = signal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        sigletChecker().check(node);

        Object value = node.getValue();
        ProcessorConfig sigletConfig = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(sigletConfig);
        assertEquals("spanlet node", sigletConfig.getName());
        assertEquals(Location.of(1,7), sigletConfig.getNameLocation());

        assertEquals(ProcessorKind.SPANLET, sigletConfig.getKind());
        assertEquals(Location.of(2,7), sigletConfig.getKindLocation());

        List<LocatedString> to = sigletConfig.getTo();
        assertNotNull(to);
        assertEquals(Location.of(3,1),sigletConfig.getToLocation());

        assertEquals(2, to.size());
        assertEquals("first exporter", to.getFirst().getValue());
        assertEquals(Location.of(4,3), to.getFirst().getLocation());

        assertEquals("second exporter", to.get(1).getValue());
        assertEquals(Location.of(5,3), to.get(1).getLocation());

        assertEquals("groovy-action", sigletConfig.getType());
        assertEquals(Location.of(6,7), sigletConfig.getTypeLocation());

        GroovyActionConfig groovyActionConfig = assertInstanceOf(GroovyActionConfig.class, sigletConfig.getConfig());
        assertNotNull(groovyActionConfig);
        assertEquals(Location.of(7,1), sigletConfig.getConfigLocation());
        assertEquals("signal.name = signal.name +\"-suffix\"", groovyActionConfig.getAction());

    }


    @Test
    void describe_toSingle() {

        String configTxt = """
                name: spanlet-node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: signal.name = signal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        sigletChecker().check(node);

        Object value = node.getValue();
        ProcessorConfig sigletConfig = assertInstanceOf(ProcessorConfig.class, value);

        String expected = """
        (1:1)  processorConfig:
          (1:7)  name: spanlet-node
          (2:7)  kind: SPANLET
          (3:5)  to:
            (3:5)  exporter
          (4:7)  type: groovy-action
          (5:1) config:
            (5:1)  groovyActionConfig:
              (6:11)  action: signal.name = signal.name +"-suffix" """;

        assertEquals(expected, sigletConfig.describe());

    }


    @Test
    void getValue_toSingle() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: signal.name = signal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        sigletChecker().check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(processorConfig);

        assertNotNull(processorConfig);
        assertEquals("spanlet node", processorConfig.getName());
        assertEquals(Location.of(1,7), processorConfig.getNameLocation());

        assertEquals(ProcessorKind.SPANLET, processorConfig.getKind());
        assertEquals(Location.of(2,7), processorConfig.getKindLocation());

        List<LocatedString> to = processorConfig.getTo();
        assertNotNull(to);
        assertEquals(Location.of(3,5),processorConfig.getToLocation());

        assertEquals(1, to.size());
        assertEquals("exporter", to.getFirst().getValue());
        assertEquals(Location.of(3,5), to.getFirst().getLocation());

        assertEquals("groovy-action", processorConfig.getType());
        assertEquals(Location.of(4,7), processorConfig.getTypeLocation());

        GroovyActionConfig groovyActionConfig = assertInstanceOf(GroovyActionConfig.class, processorConfig.getConfig());
        assertNotNull(groovyActionConfig);
        assertEquals(Location.of(5,1), processorConfig.getConfigLocation());
        assertEquals("signal.name = signal.name +\"-suffix\"", groovyActionConfig.getAction());



    }
}