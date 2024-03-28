package com.siglet.config.parser.schema;

import com.google.api.Property;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicPropertyCheckerTest {


    @Test
    public void checkDynamicProperty() throws Exception {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, false,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new PropertyChecker(MyBean::setMyProperty, "config", true,
                        new ObjectChecker(MyIntProperty::new, true,
                                new PropertyChecker(MyIntProperty::setValue, "value", true, new IntChecker()))));

        var yaml = """
                discriminator: int
                config:
                  value: 10
                """;

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);
        var myBean = assertInstanceOf(MyBean.class, bean);

        assertNotNull(myBean.getMyProperty());
        var myIntProperty = assertInstanceOf(MyIntProperty.class, myBean.getMyProperty());

        assertEquals(10, myIntProperty.getValue());
    }


    @Test
    public void check_discriminator_int() throws Exception {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, true,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new DynamicPropertyChecker("config", true, new Discriminator()));

        var yaml = """
                discriminator: int
                config:
                  value: 10
                """;

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);
        var myBean = assertInstanceOf(MyBean.class, bean);

        assertNotNull(myBean.getMyProperty());
        var myIntProperty = assertInstanceOf(MyIntProperty.class, myBean.getMyProperty());

        assertEquals(10, myIntProperty.getValue());
    }

    @Test
    public void check_discriminator_text() throws Exception {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, true,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new DynamicPropertyChecker("config", true, new Discriminator()));

        var yaml = """
                discriminator: text
                config:
                  value: a text
                """;

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);
        var myBean = assertInstanceOf(MyBean.class, bean);

        assertNotNull(myBean.getMyProperty());
        var myIntProperty = assertInstanceOf(MyStringProperty.class, myBean.getMyProperty());

        assertEquals("a text", myIntProperty.getValue());
    }

    public static class Discriminator implements DynamicCheckerDiscriminator {

        @Override
        public NodeChecker getChecker(ConfigNode configNode)  {
            if (configNode instanceof ObjectConfigNode objectNode) {
                if ("int".equals(objectNode.getProperties().get("discriminator").getValue())) {
                    return new PropertyChecker(MyBean::setMyProperty, "config", true,
                            new ObjectChecker(MyIntProperty::new, true,
                                    new PropertyChecker(MyIntProperty::setValue, "value", true, new IntChecker())));
                } else if ("text".equals(objectNode.getProperties().get("discriminator").getValue())) {
                    return new PropertyChecker(MyBean::setMyProperty, "config", true,
                            new ObjectChecker(MyStringProperty::new, true,
                                    new PropertyChecker(MyStringProperty::setValue, "value", true, new TextChecker())));
                } else {
                    throw new IllegalStateException("xx");
                }
            }
            throw new IllegalStateException("xx");
        }
    }

    public static class MyBean {
        private MyProperty myProperty;

        public MyProperty getMyProperty() {
            return myProperty;
        }

        public void setMyProperty(MyProperty myProperty) {
            this.myProperty = myProperty;
        }
    }

    public static class MyProperty {

    }

    public static class MyStringProperty extends MyProperty {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class MyIntProperty extends MyProperty{
        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

}