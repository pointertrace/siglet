package com.siglet.config.item;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.schema.SchemaValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.ConfigCheckFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class PipelineItemTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void describe() {

        var config = """
                      trace: trace-pipeline
                      from: trace-receiver
                      start: imprime span
                      pipeline:
                      - spanlet: imprime span
                        to: exporter
                        type: processor
                        config:
                          action: |
                            println "spanId=" + thisSignal.spanIdEx""";

        Node node = parser.parse(config);

        try {
            pipelineChecker().check(node);
        } catch (SchemaValidationError e) {
            System.out.println(e.getMessage());
        }

        Item item = node.getValue();

        assertNotNull(item);

        String expected = """
                (1:1)  PipelineItem
                  (1:8)  name
                    (1:8)  String  (trace-pipeline)
                  (2:7)  from
                    (2:7)  String  (trace-receiver)
                  (3:8)  start
                    (3:8)  String  (imprime span)
                  (4:1)  processors
                    (4:1)  arrayItem
                      (5:3)  array item
                        (5:3)  spanletItem
                          (5:12)  name
                            (5:12)  String  (imprime span)
                          (6:7)  to
                            (6:7)  arrayItem
                              (6:7)  array item
                                (6:7)  String  (exporter)
                          (7:9)  type
                            (7:9)  String  (processor)
                          (8:3)  config
                            (8:3)  processorConfig
                              (9:13)  action
                                (9:13)  String  (println "spanId=" + thisSignal.spanIdEx)""";

        assertEquals(expected, item.describe());
    }
}