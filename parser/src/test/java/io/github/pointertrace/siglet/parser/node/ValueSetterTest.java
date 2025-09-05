package io.github.pointertrace.siglet.parser.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueSetterTest {


    @Test
    void of() {

        ValueSetter valueSetter = ValueSetter.of(Bean::setValue);

        Bean bean = new Bean();

        valueSetter.set(bean, "str");

        assertEquals("str", bean.getValue());


    }

    static class Bean {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


}