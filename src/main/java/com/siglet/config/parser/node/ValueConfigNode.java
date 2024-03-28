package com.siglet.config.parser.node;

import com.google.protobuf.Value;
import com.siglet.config.parser.locatednode.Location;

public abstract class ValueConfigNode extends ConfigNode {

    private final Object value;

    private ValueSetter valueSetter;

    private ValueTransformer valueTransformer;

    protected ValueConfigNode(Object value, Location location) {
        super(location);
        this.value = value;
    }


    public Object getValue() {
        if (valueTransformer != null) {
            return valueTransformer.transform(value);
        }
        return value;
    }

    public ValueSetter getValueSetter() {
        return valueSetter;
    }

    public void setValueSetter(ValueSetter valueSetter) {
        this.valueSetter = valueSetter;
    }

    public void setValueTransformer(ValueTransformer valueTransformer) {
        this.valueTransformer = valueTransformer;
    }

    public void clear() {
        setValueSetter(null);
    }

    public static class Text extends ValueConfigNode {

        public Text(String value, Location location) {
            super(value, location);
        }
    }

    public static class Int extends ValueConfigNode {

        public Int(Integer value, Location location) {
            super(value, location);
        }
    }

    public static class Long extends ValueConfigNode {

        public Long(java.lang.Long value, Location location) {
            super(value, location);
        }
    }

    public static class BigInteger extends ValueConfigNode {

        public BigInteger(java.math.BigInteger value, Location location) {
            super(value, location);
        }
    }

    public static class BigDecimal extends ValueConfigNode {

        public BigDecimal(java.math.BigDecimal value, Location location) {
            super(value, location);
        }

    }

    public static class Null extends ValueConfigNode {

        public Null(Location location) {
            super(null, location);
        }
    }

    public static class Boolean extends ValueConfigNode {

        public Boolean(java.lang.Boolean value, Location location) {
            super(value, location);
        }
    }

    public static class Binary extends ValueConfigNode {

        public Binary(byte[] value, Location location) {
            super(value, location);
        }
    }
}
