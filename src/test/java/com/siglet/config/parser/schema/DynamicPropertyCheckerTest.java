package com.siglet.config.parser.schema;

import com.siglet.SigletError;
import com.siglet.config.item.Item;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicPropertyCheckerTest {


    @Test
    void checkDynamicProperty() {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, false,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new PropertyChecker(MyBean::setMyProperty, MyBean::setMyPropertyLocation, "config", true,
                        new ObjectChecker(MyIntProperty::new, true,
                                new PropertyChecker(MyIntProperty::setValue, MyIntProperty::setValueLocation,
                                        "value", true, new IntChecker()))));

        var yaml = """
                discriminator: int
                config:
                  value: 10
                """;

        ConfigParser parser = new ConfigParser();

        Node root = parser.parse(yaml);

        objectCheck.check(root);

        MyBean myBean = assertInstanceOf(MyBean.class, root.getValue());

        assertNotNull(myBean);

        assertNotNull(myBean.getMyProperty());
        assertEquals(Location.of(2, 1), myBean.getMyPropertyLocation());
        var myIntProperty = assertInstanceOf(MyIntProperty.class, myBean.getMyProperty());

        assertEquals(10, myIntProperty.getValue());
        assertEquals(Location.of(3, 10), myIntProperty.getValueLocation());
    }


    @Test
    void check_discriminator_int() {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, true,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new DynamicPropertyChecker("config", true, new Discriminator()));

        var yaml = """
                discriminator: int
                config:
                  value: 10
                """;

        ConfigParser parser = new ConfigParser();

        Node root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);
        var myBean = assertInstanceOf(MyBean.class, bean);

        assertNotNull(myBean.getMyProperty());
        var myIntProperty = assertInstanceOf(MyIntProperty.class, myBean.getMyProperty());

        assertEquals(10, myIntProperty.getValue());
        assertEquals(Location.of(3, 10), myIntProperty.getValueLocation());
    }

    @Test
    void check_discriminator_text() {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, true,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new DynamicPropertyChecker("config", true, new Discriminator()));

        var yaml = """
                discriminator: textNode
                config:
                  value: a textNode
                """;

        ConfigParser parser = new ConfigParser();

        Node root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);
        var myBean = assertInstanceOf(MyBean.class, bean);

        assertNotNull(myBean.getMyProperty());
        var myIntProperty = assertInstanceOf(MyStringProperty.class, myBean.getMyProperty());

        assertEquals("a textNode", myIntProperty.getValue());
    }

    public static class Discriminator implements DynamicCheckerDiscriminator {

        @Override
        public NodeChecker getChecker(Node node) {
            if (node instanceof ObjectNode objectNode) {
                Object discriminator = objectNode.getProperties().get("discriminator").getValue();
                if (!(discriminator instanceof String discValue)) {
                    throw new SigletError("discriminator is not a String ValueItem");
                }
                if ("int".equals(discValue)) {
                    return new PropertyChecker(MyBean::setMyProperty, MyBean::setMyPropertyLocation,
                            "config", true,
                            new ObjectChecker(MyIntProperty::new, true,
                                    new PropertyChecker(MyIntProperty::setValue,
                                            MyIntProperty::setValueLocation, "value",
                                            true, new IntChecker())));
                } else if ("textNode".equals(discValue)) {
                    return new PropertyChecker(MyBean::setMyProperty, "config", true,
                            new ObjectChecker(MyStringProperty::new, true,
                                    new PropertyChecker(MyStringProperty::setValue,
                                            MyStringProperty::setValueLocation, "value",
                                            true, new TextChecker())));
                } else {
                    throw new IllegalStateException("xx");
                }
            }
            throw new IllegalStateException("xx");
        }
    }

    public static class MyBean extends Item {

        private MyProperty myProperty;

        private Location myPropertyLocation;

        public MyProperty getMyProperty() {
            return myProperty;
        }

        public void setMyProperty(MyProperty myProperty) {
            this.myProperty = myProperty;
        }

        public Location getMyPropertyLocation() {
            return myPropertyLocation;
        }

        public void setMyPropertyLocation(Location myPropertyLocation) {
            this.myPropertyLocation = myPropertyLocation;
        }
    }

    public static class MyProperty extends Item {

    }

    public static class MyStringProperty extends MyProperty {

        private String value;

        private Location valueLocation;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Location getValueLocation() {
            return valueLocation;
        }

        public void setValueLocation(Location valueLocation) {
            this.valueLocation = valueLocation;
        }
    }

    public static class MyIntProperty extends MyProperty {

        private Integer value;

        private Location valueLocation;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Location getValueLocation() {
            return valueLocation;
        }

        public void setValueLocation(Location valueLocation) {
            this.valueLocation = valueLocation;
        }
    }

}