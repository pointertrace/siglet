package com.siglet.config.item;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.ConfigCheckFactory.metricletChecker;
import static com.siglet.config.ConfigCheckFactory.spanletChecker;
import static org.junit.jupiter.api.Assertions.*;

class MetricletItemTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void describe() {

        var config = """
                metriclet: name-value
                to:
                - first-destination
                - second-destination
                type: processor
                config:
                  action: action-value
                """;


        Node node = parser.parse(config);

        metricletChecker().check(node);

        Item item = node.getValue();

        assertNotNull(item);

        String expected = """
        (1:1)  metricletItem
          (1:12)  name
            (1:12)  String  (name-value)
          (2:1)  to
            (2:1)  arrayItem
              (3:3)  array item
                (3:3)  String  (first-destination)
              (4:3)  array item
                (4:3)  String  (second-destination)
          (5:7)  type
            (5:7)  String  (processor)
          (6:1)  config
            (6:1)  processorConfig
              (7:11)  action
                (7:11)  String  (action-value)""";

        assertEquals(expected, item.describe());
    }
}