package com.siglet.config.parser.schema;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XTest {

    @Test
    public void test() throws Exception {

        ObjectChecker objectCheck = new ObjectChecker(MyParentBean::new, true,
                new PropertyChecker(MyParentBean::setProp, "prop", true,
                        new ObjectChecker(MyBean::new, true,
                                new PropertyChecker(MyBean::setStrProperty, "strProperty", true, new TextChecker()),
                                new PropertyChecker(MyBean::setIntProperty, "intProperty", true, new IntChecker())
                        )
                )
        );

        var yaml = """
                prop:
                  strProperty: str-value
                  intProperty: 1
                """;

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);
    }

    @Test
    public void test1() throws Exception {

        ObjectChecker objectCheck = new ObjectChecker(MyBean::new, true,
                new PropertyChecker(MyBean::setStrProperty, "strProperty", true, new TextChecker()),
                new PropertyChecker(MyBean::setIntProperty, "intProperty", true, new IntChecker())
        );

        var yaml = """
                strProperty: str-value
                intProperty: 1
                """;

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);


    }

    @Test
    public void test2() throws Exception {

        ObjectChecker objectCheck = new ObjectChecker(MyParentBean::new, true,
                new PropertyChecker(MyParentBean::setProp, "prop", true,
                        new ObjectChecker(MyBean::new, true,
                                new PropertyChecker(MyBean::setStrProperty, "strProperty", true, new TextChecker()),
                                new PropertyChecker(MyBean::setIntProperty, "intProperty", true, new IntChecker())
                        )
                ),
                new PropertyChecker(MyParentBean::setProp2, "prop2", true,
                        new ArrayChecker(
                                new ObjectChecker(MyBean::new, true,
                                        new PropertyChecker(MyBean::setStrProperty, "strProperty", true, new TextChecker()),
                                        new PropertyChecker(MyBean::setIntProperty, "intProperty", true, new IntChecker())
                                )
                        )

                )
        );

        var yaml = """
                prop2:
                  - strProperty: str-value-idx-1
                    intProperty: 2
                  - strProperty: str-value-idx-2
                    intProperty: 3
                prop:
                  strProperty: str-value
                  intProperty: 1
                """;

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        objectCheck.check(root);

        Object bean = root.getValue();

        assertNotNull(bean);


    }

    public static class MyParentBean {
        private MyBean prop;

        private List<MyBean> prop2;

        public MyBean getProp() {
            return prop;
        }

        public void setProp(MyBean prop) {
            this.prop = prop;
        }

        public List<MyBean> getProp2() {
            return prop2;
        }

        public void setProp2(List<MyBean> prop2) {
            this.prop2 = prop2;
        }

        public void addProp2(MyBean myBean) {
            prop2.add(myBean);
        }
    }

    public static class MyBean {
        private String strProperty;

        private int intProperty;

        public String getStrProperty() {
            return strProperty;
        }

        public void setStrProperty(String strProperty) {
            this.strProperty = strProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }
    }

}
