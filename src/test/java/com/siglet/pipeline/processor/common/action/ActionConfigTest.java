package com.siglet.pipeline.processor.common.action;

import com.siglet.config.item.SigletItem;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.PropertyChecker;
import com.siglet.pipeline.processor.traceaggregator.TraceAggregationDefinition;
import com.siglet.pipeline.processor.traceaggregator.TraceAggregatorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.parser.schema.SchemaFactory.strictObject;
import static org.junit.jupiter.api.Assertions.*;

class ActionConfigTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();

    }


    @Test
    void describe() {

        String configTxt = """
                config:
                  action: action value
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(SigletItem::new,
                (PropertyChecker) new ActionDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);

        assertNotNull(siglet.getConfig());
        ActionConfig actionConfig= assertInstanceOf(ActionConfig.class, siglet.getConfig());

        String expected = """
        (1:1)  actionConfig:
          (2:11)  action: action value""";

        assertEquals(expected, actionConfig.describe(0));

    }


    @Test
    void getValue() {


        String configTxt = """
                config:
                  timeout-millis: 1
                  inactive-timeout-millis: 2
                  completion-expression: expression value
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(SigletItem::new,
                (PropertyChecker) new TraceAggregationDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);



        assertNotNull(siglet.getConfig());
        TraceAggregatorConfig traceAggregatorConfig = assertInstanceOf(TraceAggregatorConfig.class, siglet.getConfig());
        assertEquals(Location.of(1,1), traceAggregatorConfig.getLocation());


        assertEquals(1, traceAggregatorConfig.getTimeoutMillis());
        assertEquals(Location.of(2,19), traceAggregatorConfig.getTimeoutMillisLocation());

        assertEquals(2, traceAggregatorConfig.getInactiveTimeoutMillis());
        assertEquals(Location.of(3,28), traceAggregatorConfig.getInactiveTimeoutMillisLocation());

        assertEquals("expression value", traceAggregatorConfig.getCompletionExpression());
        assertEquals(Location.of(4,26), traceAggregatorConfig.getCompletionExpressionLocation());

    }
}