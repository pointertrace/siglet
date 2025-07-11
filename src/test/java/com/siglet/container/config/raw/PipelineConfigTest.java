package com.siglet.container.config.raw;

import com.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import com.siglet.parser.Node;
import com.siglet.parser.located.Location;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.container.config.ConfigCheckFactory.pipelineChecker;
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
                signal: trace
                from: trace receiver
                start: spanlet name
                processors:
                - name: spanlet name
                  kind: spanlet
                  to: second exporter
                  type: groovy-action
                  config:
                    action: thisSignal.name = thisSignal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();

        PipelineConfig pipelineConfig = assertInstanceOf(PipelineConfig.class, value);

        assertNotNull(pipelineConfig);

        String expected = """
                (1:1)  PipelineConfig:
                  (1:7)  name: pipeline name
                  (2:9)  signal: TRACE
                  (3:7)  from: trace receiver
                  (4:8)  start:
                    (4:8)  spanlet name
                  (5:1)  processors:
                    (6:3)  processorConfig:
                      (6:9)  name: spanlet name
                      (7:9)  kind: SPANLET
                      (8:7)  to:
                        (8:7)  second exporter
                      (9:9)  type: groovy-action
                      (10:3) config:
                        (10:3)  groovyActionConfig:
                          (11:13)  action: thisSignal.name = thisSignal.name +"-suffix" """;

        assertEquals(expected, pipelineConfig.describe());
    }

    @Test
    void getValue_startSingleValue() {

        var config = """
                name: pipeline name
                signal: trace
                from: trace receiver
                start: spanlet name
                processors:
                - name: spanlet name
                  kind: spanlet
                  to: second exporter
                  type: groovy-action
                  config:
                    action: thisSignal.name = thisSignal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();

        PipelineConfig pipelineConfig = assertInstanceOf(PipelineConfig.class, value);

        assertNotNull(pipelineConfig);

        assertEquals("pipeline name", pipelineConfig.getName());
        assertEquals(Location.of(1, 7), pipelineConfig.getNameLocation());

        assertEquals(Signal.TRACE, pipelineConfig.getSignal());
        assertEquals(Location.of(2, 9), pipelineConfig.getSignalLocation());

        assertEquals("trace receiver", pipelineConfig.getFrom());
        assertEquals(Location.of(3, 7), pipelineConfig.getFromLocation());

        List<LocatedString> start = pipelineConfig.getStart();
        assertNotNull(start);
        assertEquals(Location.of(4, 8), pipelineConfig.getStartLocation());

        assertEquals(1, start.size());
        assertEquals("spanlet name", start.getFirst().getValue());
        assertEquals(Location.of(4, 8), start.getFirst().getLocation());


        assertNotNull(pipelineConfig.getProcessors());
        assertEquals(Location.of(5, 1), pipelineConfig.getSigletsLocation());
        List<ProcessorConfig> siglets = pipelineConfig.getProcessors();

        assertEquals(1, siglets.size());
        assertEquals("spanlet name", siglets.getFirst().getName());

    }

    @Test
    void getValue_startList() {

        var config = """
                name: pipeline name
                signal: trace
                from: trace receiver
                start:
                - first sigletConfig
                - second sigletConfig
                processors:
                - name: spanlet name
                  kind: spanlet
                  to: second exporter
                  type: groovy-action
                  config:
                    action: thisSignal.name = thisSignal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();

        PipelineConfig pipelineConfig = assertInstanceOf(PipelineConfig.class, value);

        assertNotNull(pipelineConfig);

        assertEquals("pipeline name", pipelineConfig.getName());
        assertEquals(Location.of(1, 7), pipelineConfig.getNameLocation());

        assertEquals(Signal.TRACE, pipelineConfig.getSignal());
        assertEquals(Location.of(2, 9), pipelineConfig.getSignalLocation());

        assertEquals("trace receiver", pipelineConfig.getFrom());
        assertEquals(Location.of(3, 7), pipelineConfig.getFromLocation());

        List<LocatedString> start = pipelineConfig.getStart();
        assertNotNull(start);
        assertEquals(Location.of(4, 1), pipelineConfig.getStartLocation());

        assertEquals(2, start.size());
        assertEquals("first sigletConfig", start.getFirst().getValue());
        assertEquals(Location.of(5, 3), start.getFirst().getLocation());
        assertEquals("second sigletConfig", start.get(1).getValue());
        assertEquals(Location.of(6, 3), start.get(1).getLocation());


        assertNotNull(pipelineConfig.getProcessors());
        assertEquals(Location.of(7, 1), pipelineConfig.getSigletsLocation());
        List<ProcessorConfig> siglets = pipelineConfig.getProcessors();

        assertEquals(1, siglets.size());
        assertEquals("spanlet name", siglets.getFirst().getName());
    }

    @Test
    void describe_startList() {

        var config = """
                name: pipeline name
                signal: trace
                from: trace receiver
                start:
                - first spanlet
                - second spanlet
                processors:
                - name: first spanlet
                  kind: spanlet
                  to: second-exporter
                  type: groovy-action
                  config:
                    action: thisSignal.name = thisSignal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();

        PipelineConfig pipelineConfig = assertInstanceOf(PipelineConfig.class, value);

        assertNotNull(pipelineConfig);

        String expected = """
                (1:1)  PipelineConfig:
                  (1:7)  name: pipeline name
                  (2:9)  signal: TRACE
                  (3:7)  from: trace receiver
                  (4:1)  start:
                    (5:3)  first spanlet
                    (6:3)  second spanlet
                  (7:1)  processors:
                    (8:3)  processorConfig:
                      (8:9)  name: first spanlet
                      (9:9)  kind: SPANLET
                      (10:7)  to:
                        (10:7)  second-exporter
                      (11:9)  type: groovy-action
                      (12:3) config:
                        (12:3)  groovyActionConfig:
                          (13:13)  action: thisSignal.name = thisSignal.name +"-suffix" """;

        assertEquals(expected, pipelineConfig.describe());
    }
}