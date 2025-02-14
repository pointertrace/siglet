package com.siglet.config.item;

import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.config.ConfigCheckFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class PipelineItemTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void describe_startSingleValue() {

        var config = """
                name: pipeline name
                signal: trace
                from: trace receiver
                start: siglet name
                siglets:
                - name: spanlet name
                  kind: spanlet
                  to: second exporter
                  type: groovy-action
                  config:
                    action: thisSignal.name = thisSignal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker().check(node);

        Object value = node.getValue();

        PipelineItem pipeline = assertInstanceOf(PipelineItem.class, value);

        assertNotNull(pipeline);

        String expected = """
                (1:1)  Pipeline:
                  (1:7)  name: pipeline name
                  (2:9)  signal: TRACE
                  (3:7)  from: trace receiver
                  (4:8)  start:
                    (4:8)  siglet name
                  (5:1)  siglets:
                    (6:3)  siglet:
                      (6:9)  name: spanlet name
                      (7:9)  kind: SPANLET
                      (8:7)  to:
                        (8:7)  second exporter
                      (9:9)  type: groovy-action
                      (10:3) config:
                        (10:3)  actionConfig:
                          (11:13)  action: thisSignal.name = thisSignal.name +"-suffix" """;

        assertEquals(expected, pipeline.describe());
    }

    @Test
    void getValue_startSingleValue() {

        var config = """
                name: pipeline name
                signal: trace
                from: trace receiver
                start: siglet name
                siglets:
                - name: spanlet name
                  kind: spanlet
                  to: second exporter
                  type: groovy-action
                  config:
                    action: thisSignal.name = thisSignal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker().check(node);

        Object value = node.getValue();

        PipelineItem pipeline = assertInstanceOf(PipelineItem.class, value);

        assertNotNull(pipeline);

        assertEquals("pipeline name", pipeline.getName());
        assertEquals(Location.of(1, 7), pipeline.getNameLocation());

        assertEquals(Signal.TRACE, pipeline.getSignal());
        assertEquals(Location.of(2, 9), pipeline.getSignalLocation());

        assertEquals("trace receiver", pipeline.getFrom());
        assertEquals(Location.of(3, 7), pipeline.getFromLocation());

        List<LocatedString> start = pipeline.getStart();
        assertNotNull(start);
        assertEquals(Location.of(4, 8), pipeline.getStartLocation());

        assertEquals(1, start.size());
        assertEquals("siglet name", start.getFirst().getValue());
        assertEquals(Location.of(4, 8), start.getFirst().getLocation());


        assertNotNull(pipeline.getSiglets());
        assertEquals(Location.of(5, 1), pipeline.getSigletsLocation());
        List<SigletItem> siglets = pipeline.getSiglets();

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
                - first siglet
                - second siglet
                siglets:
                - name: spanlet name
                  kind: spanlet
                  to: second exporter
                  type: groovy-action
                  config:
                    action: thisSignal.name = thisSignal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker().check(node);

        Object value = node.getValue();

        PipelineItem pipeline = assertInstanceOf(PipelineItem.class, value);

        assertNotNull(pipeline);

        assertEquals("pipeline name", pipeline.getName());
        assertEquals(Location.of(1, 7), pipeline.getNameLocation());

        assertEquals(Signal.TRACE, pipeline.getSignal());
        assertEquals(Location.of(2, 9), pipeline.getSignalLocation());

        assertEquals("trace receiver", pipeline.getFrom());
        assertEquals(Location.of(3, 7), pipeline.getFromLocation());

        List<LocatedString> start = pipeline.getStart();
        assertNotNull(start);
        assertEquals(Location.of(4, 1), pipeline.getStartLocation());

        assertEquals(2, start.size());
        assertEquals("first siglet", start.getFirst().getValue());
        assertEquals(Location.of(5, 3), start.getFirst().getLocation());
        assertEquals("second siglet", start.get(1).getValue());
        assertEquals(Location.of(6, 3), start.get(1).getLocation());


        assertNotNull(pipeline.getSiglets());
        assertEquals(Location.of(7, 1), pipeline.getSigletsLocation());
        List<SigletItem> siglets = pipeline.getSiglets();

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
                - first siglet
                - second siglet
                siglets:
                - name: spanlet name
                  kind: spanlet
                  to: second-exporter
                  type: groovy-action
                  config:
                    action: thisSignal.name = thisSignal.name +"-suffix" """;

        Node node = parser.parse(config);

        pipelineChecker().check(node);

        Object value = node.getValue();

        PipelineItem pipeline = assertInstanceOf(PipelineItem.class, value);

        assertNotNull(pipeline);

        String expected = """
                (1:1)  Pipeline:
                  (1:7)  name: pipeline name
                  (2:9)  signal: TRACE
                  (3:7)  from: trace receiver
                  (4:1)  start:
                    (5:3)  first siglet
                    (6:3)  second siglet
                  (7:1)  siglets:
                    (8:3)  siglet:
                      (8:9)  name: spanlet name
                      (9:9)  kind: SPANLET
                      (10:7)  to:
                        (10:7)  second-exporter
                      (11:9)  type: groovy-action
                      (12:3) config:
                        (12:3)  actionConfig:
                          (13:13)  action: thisSignal.name = thisSignal.name +"-suffix" """;

        assertEquals(expected, pipeline.describe());
    }
}