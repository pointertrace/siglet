package com.siglet.config.item;

import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import com.siglet.pipeline.processor.common.action.ActionConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.config.ConfigCheckFactory.sigletChecker;
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
                name: spanlet node
                kind: spanlet
                to:
                - first exporter
                - second exporter
                type: groovy-action
                config:
                  action: thisSignal.name = thisSignal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        sigletChecker().check(node);

        Object value = node.getValue();
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);

        assertNotNull(siglet);

        String expected = """
        (1:1)  siglet:
          (1:7)  name: spanlet node
          (2:7)  kind: SPANLET
          (3:1)  to:
            (4:3)  first exporter
            (5:3)  second exporter
          (6:7)  type: groovy-action
          (7:1) config:
            (7:1)  processorConfig
              (8:11)  action: thisSignal.name = thisSignal.name +"-suffix" """;

        assertEquals(expected, siglet.describe());

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
                  action: thisSignal.name = thisSignal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        sigletChecker().check(node);

        Object value = node.getValue();
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);

        assertNotNull(siglet);
        assertEquals("spanlet node", siglet.getName());
        assertEquals(Location.of(1,7), siglet.getNameLocation());

        assertEquals(SigletKind.SPANLET, siglet.getKind());
        assertEquals(Location.of(2,7), siglet.getKindLocation());

        List<LocatedString> to = siglet.getTo();
        assertNotNull(to);
        assertEquals(Location.of(3,1),siglet.getToLocation());

        assertEquals(2, to.size());
        assertEquals("first exporter", to.getFirst().getValue());
        assertEquals(Location.of(4,3), to.getFirst().getLocation());

        assertEquals("second exporter", to.get(1).getValue());
        assertEquals(Location.of(5,3), to.get(1).getLocation());

        assertEquals("groovy-action", siglet.getType());
        assertEquals(Location.of(6,7), siglet.getTypeLocation());

        ActionConfig actionConfig = assertInstanceOf(ActionConfig.class, siglet.getConfig());
        assertNotNull(actionConfig);
        assertEquals(Location.of(7,1), siglet.getConfigLocation());
        assertEquals("thisSignal.name = thisSignal.name +\"-suffix\"", actionConfig.getAction());

    }


    @Test
    void describe_toSingle() {

        String configTxt = """
                name: spanlet-node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: thisSignal.name = thisSignal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        sigletChecker().check(node);

        Object value = node.getValue();
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);

        String expected = """
        (1:1)  siglet:
          (1:7)  name: spanlet-node
          (2:7)  kind: SPANLET
          (3:5)  to:
            (3:5)  exporter
          (4:7)  type: groovy-action
          (5:1) config:
            (5:1)  processorConfig
              (6:11)  action: thisSignal.name = thisSignal.name +"-suffix" """;

        assertEquals(expected, siglet.describe());

    }


    @Test
    void getValue_toSingle() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: thisSignal.name = thisSignal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        sigletChecker().check(node);

        Object value = node.getValue();
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);

        assertNotNull(siglet);

        assertNotNull(siglet);
        assertEquals("spanlet node", siglet.getName());
        assertEquals(Location.of(1,7), siglet.getNameLocation());

        assertEquals(SigletKind.SPANLET, siglet.getKind());
        assertEquals(Location.of(2,7), siglet.getKindLocation());

        List<LocatedString> to = siglet.getTo();
        assertNotNull(to);
        assertEquals(Location.of(3,5),siglet.getToLocation());

        assertEquals(1, to.size());
        assertEquals("exporter", to.getFirst().getValue());
        assertEquals(Location.of(3,5), to.getFirst().getLocation());

        assertEquals("groovy-action", siglet.getType());
        assertEquals(Location.of(4,7), siglet.getTypeLocation());

        ActionConfig actionConfig = assertInstanceOf(ActionConfig.class, siglet.getConfig());
        assertNotNull(actionConfig);
        assertEquals(Location.of(5,1), siglet.getConfigLocation());
        assertEquals("thisSignal.name = thisSignal.name +\"-suffix\"", actionConfig.getAction());



    }
}