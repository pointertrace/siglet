package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.pointertrace.siglet.container.config.ConfigCheckFactory.pipelineChecker;
import static org.junit.jupiter.api.Assertions.*;

class PipelineConfigTest {

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void describe_startSingleValue() {

        var config = """
                name: pipeline name
                from: trace receiver
                start: spanlet name
                processors:
                - spanlet-groovy-action: spanlet name
                  to: second exporter
                  config:
                    action: signal.name = signal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();

        PipelineConfig pipelineConfig = assertInstanceOf(PipelineConfig.class, value);

        assertNotNull(pipelineConfig);

        String expected = """
                (1:1)  PipelineConfig:
                  (1:7)  name: pipeline name
                  (2:7)  from: trace receiver
                  (3:8)  start:
                    (3:8)  spanlet name
                  (4:1)  processors:
                    (5:3)  processorConfig:
                      (5:26)  name: spanlet name
                      (5:3)  type: spanlet-groovy-action
                      (6:7)  to:
                        (6:7)  second exporter
                      (7:3) config:
                        (7:3)  groovyActionConfig:
                          (8:13)  action: signal.name = signal.name +"-suffix" """;

        assertEquals(expected, pipelineConfig.describe());
    }

    @Test
    void getValue_startSingleValue() {

        var config = """
                name: pipeline name
                from: trace receiver
                start: spanlet name
                processors:
                - spanlet-groovy-action: spanlet name
                  to: second exporter
                  config:
                    action: signal.name = signal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();

        PipelineConfig pipelineConfig = assertInstanceOf(PipelineConfig.class, value);

        assertNotNull(pipelineConfig);

        assertEquals("pipeline name", pipelineConfig.getName());
        assertEquals(Location.of(1, 7), pipelineConfig.getNameLocation());

        assertEquals("trace receiver", pipelineConfig.getFrom());
        assertEquals(Location.of(2, 7), pipelineConfig.getFromLocation());

        List<LocatedString> start = pipelineConfig.getStart();
        assertNotNull(start);
        assertEquals(Location.of(3, 8), pipelineConfig.getStartLocation());

        assertEquals(1, start.size());
        assertEquals("spanlet name", start.getFirst().getValue());
        assertEquals(Location.of(3, 8), start.getFirst().getLocation());


        assertNotNull(pipelineConfig.getProcessors());
        assertEquals(Location.of(4, 1), pipelineConfig.getSigletsLocation());
        List<ProcessorConfig> siglets = pipelineConfig.getProcessors();

        assertEquals(1, siglets.size());
        assertEquals("spanlet name", siglets.getFirst().getName());

    }

    @Test
    void getValue_startList() {

        var config = """
                name: pipeline name
                from: trace receiver
                start:
                - first sigletConfig
                - second sigletConfig
                processors:
                - spanlet-groovy-action: spanlet name
                  to: second exporter
                  config:
                    action: signal.name = signal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();

        PipelineConfig pipelineConfig = assertInstanceOf(PipelineConfig.class, value);

        assertNotNull(pipelineConfig);

        assertEquals("pipeline name", pipelineConfig.getName());
        assertEquals(Location.of(1, 7), pipelineConfig.getNameLocation());

        assertEquals("trace receiver", pipelineConfig.getFrom());
        assertEquals(Location.of(2, 7), pipelineConfig.getFromLocation());

        List<LocatedString> start = pipelineConfig.getStart();
        assertNotNull(start);
        assertEquals(Location.of(3, 1), pipelineConfig.getStartLocation());

        assertEquals(2, start.size());
        assertEquals("first sigletConfig", start.getFirst().getValue());
        assertEquals(Location.of(4, 3), start.getFirst().getLocation());
        assertEquals("second sigletConfig", start.get(1).getValue());
        assertEquals(Location.of(5, 3), start.get(1).getLocation());


        assertNotNull(pipelineConfig.getProcessors());
        assertEquals(Location.of(6, 1), pipelineConfig.getSigletsLocation());
        List<ProcessorConfig> siglets = pipelineConfig.getProcessors();

        assertEquals(1, siglets.size());
        assertEquals("spanlet name", siglets.getFirst().getName());
    }

    @Test
    void describe_startList() {

        var config = """
                name: pipeline name
                from: trace receiver
                start:
                - first spanlet
                - second spanlet
                processors:
                - spanlet-groovy-action: first spanlet
                  to: second-exporter
                  config:
                    action: signal.name = signal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();

        PipelineConfig pipelineConfig = assertInstanceOf(PipelineConfig.class, value);

        assertNotNull(pipelineConfig);

        String expected = """
                (1:1)  PipelineConfig:
                  (1:7)  name: pipeline name
                  (2:7)  from: trace receiver
                  (3:1)  start:
                    (4:3)  first spanlet
                    (5:3)  second spanlet
                  (6:1)  processors:
                    (7:3)  processorConfig:
                      (7:26)  name: first spanlet
                      (7:3)  type: spanlet-groovy-action
                      (8:7)  to:
                        (8:7)  second-exporter
                      (9:3) config:
                        (9:3)  groovyActionConfig:
                          (10:13)  action: signal.name = signal.name +"-suffix" """;

        assertEquals(expected, pipelineConfig.describe());
    }
}