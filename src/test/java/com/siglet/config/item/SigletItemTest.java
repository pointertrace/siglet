package com.siglet.config.item;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import com.siglet.pipeline.processor.common.action.ActionConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.config.ConfigCheckFactory.processorChecker;
import static org.junit.jupiter.api.Assertions.*;

class SigletItemTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();

    }


    @Test
    void describe_toList() {

        String configTxt = """
                kind: spanlet
                name: spanlet-node
                to:
                - first-exporter
                - second-exporter
                type: groovy-action
                config:
                  action: thisSignal.name = thisSignal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        System.out.println(processorChecker().describe());
        processorChecker().check(node);

        Object value = node.getValue();
        SigletItem item = assertInstanceOf(SigletItem.class, value);

        assertNotNull(item);

        String expected = """
        (1:1)  kind: SPANLET
          (2:7)  name: spanlet-node
          (3:1)  to:
            (4:3)  first-exporter
            (5:3)  second-exporter
          (6:7)  type: groovy-action
          (7:1) config:
            (7:1)  processorConfig
              (8:11)  action
                thisSignal.name = thisSignal.name +"-suffix" """;

        assertEquals(expected, item.describe());

    }


    @Test
    void getValue_toList() {


        String configTxt = """
                kind: spanlet
                name: spanlet-node
                to: exporter
                type: groovy-action
                config:
                  action: thisSignal.name = thisSignal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        System.out.println(processorChecker().describe());
        processorChecker().check(node);

        Object value = node.getValue();
        SigletItem processor = assertInstanceOf(SigletItem.class, value);

        assertNotNull(processor);

        assertNotNull(processor);
        assertEquals("spanlet-node", processor.getName());
        assertEquals(ProcessorKind.SPANLET, processor.getKind());

        List<LocatedString> to = processor.getTo();
        assertEquals(1, to.size());
        assertEquals("exporter", to.getFirst().getValue());

        ActionConfig actionConfig = assertInstanceOf(ActionConfig.class, processor.getConfig());
        assertNotNull(actionConfig);
        assertEquals("thisSignal.name = thisSignal.name +\"-suffix\"", actionConfig.getAction());

    }


    @Test
    void describe_toSingle() {

        String configTxt = """
                kind: spanlet
                name: spanlet-node
                to: exporter
                type: groovy-action
                config:
                  action: thisSignal.name = thisSignal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker().check(node);

        Object value = node.getValue();
        SigletItem processor = assertInstanceOf(SigletItem.class, value);

        String expected = """
                (1:1)  kind: SPANLET
                  (2:7)  name: spanlet-node
                  (3:5)  to:
                    (3:5)  exporter
                  (4:7)  type: groovy-action
                  (5:1) config:
                    (5:1)  processorConfig
                      (6:11)  action
                        thisSignal.name = thisSignal.name +"-suffix" """;

        assertEquals(expected, processor.describe());

    }


    @Test
    void getValue_toSingle() {

        String configTxt = """
                kind: spanlet
                name: spanlet-node
                to: exporter
                type: groovy-action
                config:
                  action: thisSignal.name = thisSignal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker().check(node);

        Object value = node.getValue();
        SigletItem processor = assertInstanceOf(SigletItem.class, value);

        assertNotNull(processor);
        assertEquals("spanlet-node", processor.getName());
        assertEquals(ProcessorKind.SPANLET, processor.getKind());

        List<LocatedString> to = processor.getTo();
        assertEquals(1, to.size());
        assertEquals("exporter", to.getFirst().getValue());

        ActionConfig actionConfig = assertInstanceOf(ActionConfig.class, processor.getConfig());
        assertNotNull(actionConfig);
        assertEquals("thisSignal.name = thisSignal.name +\"-suffix\"", actionConfig.getAction());


    }
}