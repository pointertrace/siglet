package com.siglet.pipeline.processor.traceaggregator;

import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TraceAggregationDefinitionTest {

    private YamlParser parser;

    @BeforeEach
    public void setUp() {
        parser = new YamlParser();

    }


    @Test
    void describe() {

//        String configTxt = """
//                config:
//                  timeout-millis: 1
//                  inactive-timeout-millis: 2
//                  completion-expression: expression value
//                """;
//
//        Node node = parser.parse(configTxt);
//
//        NodeChecker checker = strictObject(SigletItem::new,
//                (PropertyChecker) new TraceAggregationDefinition().getChecker());
//
//        checker.check(node);
//        Object value = node.getValue();
//
//        assertNotNull(node);
//        SigletItem siglet = assertInstanceOf(SigletItem.class, value);
//
//        assertNotNull(siglet.getConfig());
//        TraceAggregatorConfig traceAggregatorConfig = assertInstanceOf(TraceAggregatorConfig.class, siglet.getConfig());
//
//        String expected = """
//                (1:1)  traceAggregationConfig: 1
//                  (2:19)  timeout-millis: 1
//                  (3:28)  inactive-timeout-millis: 2
//                  (4:26)  completion-expression: expression value""";
//
//        assertEquals(expected, traceAggregatorConfig.describe(0));

    }


    @Test
    void getValue() {


//        String configTxt = """
//                config:
//                  timeout-millis: 1
//                  inactive-timeout-millis: 2
//                  completion-expression: expression value
//                """;
//
//        Node node = parser.parse(configTxt);
//
//        NodeChecker checker = strictObject(SigletItem::new,
//                (PropertyChecker) new TraceAggregationDefinition().getChecker());
//
//        checker.check(node);
//        Object value = node.getValue();
//
//        assertNotNull(node);
//        SigletItem siglet = assertInstanceOf(SigletItem.class, value);
//
//
//        assertNotNull(siglet.getConfig());
//        TraceAggregatorConfig traceAggregatorConfig = assertInstanceOf(TraceAggregatorConfig.class, siglet.getConfig());
//        assertEquals(Location.of(1, 1), traceAggregatorConfig.getLocation());
//
//
//        assertEquals(1, traceAggregatorConfig.getTimeoutMillis());
//        assertEquals(Location.of(2, 19), traceAggregatorConfig.getTimeoutMillisLocation());
//
//        assertEquals(2, traceAggregatorConfig.getInactiveTimeoutMillis());
//        assertEquals(Location.of(3, 28), traceAggregatorConfig.getInactiveTimeoutMillisLocation());
//
//        assertEquals("expression value", traceAggregatorConfig.getCompletionExpression());
//        assertEquals(Location.of(4, 26), traceAggregatorConfig.getCompletionExpressionLocation());

    }
}