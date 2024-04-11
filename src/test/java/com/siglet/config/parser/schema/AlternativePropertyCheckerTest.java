package com.siglet.config.parser.schema;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.config.parser.schema.SchemaFactory.array;
import static com.siglet.config.parser.schema.SchemaFactory.text;
import static org.junit.jupiter.api.Assertions.*;

class AlternativePropertyCheckerTest {
    private ObjectChecker objectCheck;

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    public void check_firstAlternative() throws SchemaValidationError {

        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue,"prop1", true, text()),
                        new PropertyChecker(Bean::setProp1,"prop1", true,array(text()))));

        ConfigNode node = parser.parse("""
                prop1: text-value
                """);

        objectCheck.check(node);

        Object value = node.getValue();
        assertNotNull(value);
        var bean = assertInstanceOf(Bean.class, value);
        assertEquals(List.of("text-value"), bean.getProp1());
    }

    @Test
    public void check_secondAlternative() throws SchemaValidationError {

        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue,"prop1", true, text()),
                        new PropertyChecker(Bean::setProp1,"prop1", true,array(text()))));

        ConfigNode node = parser.parse("""
                prop1:
                - first-value
                - second-value
                """);

        objectCheck.check(node);

        Object value = node.getValue();
        assertNotNull(value);
        var bean = assertInstanceOf(Bean.class, value);
        assertEquals(List.of("first-value","second-value"), bean.getProp1());
    }
    public static class Bean {

        private List<String> prop1;

        public void setProp1(List<String> prop1) {
            this.prop1 = prop1;
        }

        public void setProp1AsValue(String prop1) {
            this.prop1 = List.of(prop1);
        }

        public List<String> getProp1() {
            return prop1;
        }
    }

}