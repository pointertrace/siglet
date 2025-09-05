package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.ParserError;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.node.ObjectNode;
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

        String yaml = "discriminator: int\n" +
                      "config:\n" +
                      "  value: 10\n";

        YamlParser parser = new YamlParser();

        Node root = parser.parse(yaml);

        objectCheck.check(root);

        MyBean myBean = assertInstanceOf(MyBean.class, root.getValue());

        assertNotNull(myBean);

        assertNotNull(myBean.getMyProperty());
        // TODO verifica pq location é nula
//        assertEquals(Location.of(2, 1), myBean.getMyPropertyLocation());
        MyIntProperty myIntProperty = assertInstanceOf(MyIntProperty.class, myBean.getMyProperty());

        assertEquals(10, myIntProperty.getValue());
        // TODO verifica pq location é nula
//        assertEquals(Location.of(3, 10), myIntProperty.getValueLocation());
    }


    @Test
    void check_discriminator_int() {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, true,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new DynamicPropertyChecker("config", true, new Discriminator()));

        String yaml = "discriminator: int\n" +
                      "config:\n" +
                      "  value: 10\n";

        YamlParser parser = new YamlParser();

        Node root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);
        MyBean myBean = assertInstanceOf(MyBean.class, bean);

        assertNotNull(myBean.getMyProperty());
        MyIntProperty myIntProperty = assertInstanceOf(MyIntProperty.class, myBean.getMyProperty());

        assertEquals(10, myIntProperty.getValue());
        // TODO verificar pq location é nulo
//        assertEquals(Location.of(3, 10), myIntProperty.getValueLocation());
    }

    @Test
    void check_discriminator_text() {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, true,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new DynamicPropertyChecker("config", true, new Discriminator()));

        String yaml = "discriminator: textNode\n" +
                   "config:\n" +
                   "  value: a textNode\n";

        YamlParser parser = new YamlParser();

        Node root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);
        MyBean myBean = assertInstanceOf(MyBean.class, bean);

        assertNotNull(myBean.getMyProperty());
        MyStringProperty myIntProperty = assertInstanceOf(MyStringProperty.class, myBean.getMyProperty());

        assertEquals("a textNode", myIntProperty.getValue());
    }

    @Test
    void check_discriminator_empty() {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, true,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new DynamicPropertyChecker("config", true, new Discriminator()));

        String yaml = "discriminator: empty\n";

        YamlParser parser = new YamlParser();

        Node root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);
        MyBean myBean = assertInstanceOf(MyBean.class, bean);

        assertNull(myBean.getMyProperty());
    }

    @Test
    void check_discriminator_emptyNotEmpty() {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, true,
                new PropertyChecker("discriminator", true, new TextChecker()),
                new DynamicPropertyChecker("config", true, new Discriminator()));

        String yaml = "discriminator: empty\n" +
                      "config:\n" +
                      "  value: a textNode\n";

        YamlParser parser = new YamlParser();

        Node root = parser.parse(yaml);

        assertThrowsExactly(SingleSchemaValidationError.class, () -> objectCheck.check(root));
    }

    public static class Discriminator implements DynamicCheckerDiscriminator {

        @Override
        public BaseNodeChecker getChecker(Node node) {
            if (node instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) node;
                Object discriminator = objectNode.getProperties().get("discriminator").getValue();
                if (!(discriminator instanceof String)) {
                    throw new ParserError("discriminator is not a String ValueItem");
                }
                String discValue = (String) discriminator;
                if ("empty".equals(discValue)) {
                    return new EmptyPropertyChecker("config");
                } else if ("int".equals(discValue)) {
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

    public static class MyBean {

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

    public static class MyProperty {

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