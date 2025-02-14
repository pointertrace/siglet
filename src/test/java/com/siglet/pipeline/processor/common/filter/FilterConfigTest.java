package com.siglet.pipeline.processor.common.filter;

import com.siglet.config.item.SigletItem;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.PropertyChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.parser.schema.SchemaFactory.strictObject;
import static org.junit.jupiter.api.Assertions.*;

class FilterConfigTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();

    }


    @Test
    void describe() {

        String configTxt = """
                config:
                  expression: expression value
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(SigletItem::new,
                (PropertyChecker) new FilterDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);

        assertNotNull(siglet.getConfig());
        FilterConfig filterConfig = assertInstanceOf(FilterConfig.class, siglet.getConfig());

        String expected = """
                (1:1)  filterConfig:
                  (2:15)  expression: expression value""";

        assertEquals(expected, filterConfig.describe(0));

    }


    @Test
    void getValue() {


        String configTxt = """
                config:
                  expression: expression value
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(SigletItem::new,
                (PropertyChecker) new FilterDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);

        assertNotNull(siglet.getConfig());

        FilterConfig filterConfig = assertInstanceOf(FilterConfig.class, siglet.getConfig());


        assertEquals(Location.of(1, 1), filterConfig.getLocation());

        assertEquals( "expression value", filterConfig.getExpression());
        assertEquals(Location.of(2, 15), filterConfig.getExpressionLocation());

    }
}