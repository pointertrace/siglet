package com.siglet.config.parser.node;

import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;

public sealed abstract class ValueConfigNode<T> extends ConfigNode permits
        ValueConfigNode.Text, ValueConfigNode.NumberConfigNode, ValueConfigNode.Null, ValueConfigNode.Boolean, ValueConfigNode.Binary {

    private final T value;

    private ValueSetter valueSetter;

    private ValueTransformer valueTransformer;

    protected ValueConfigNode(T value, Location location) {
        super( location);
        this.value = value;
    }


    public ValueItem<?> getValue() {
        if (valueTransformer != null) {
            return valueTransformer.transform(getLocation(), value);
        }
        return new ValueItem<>(getLocation(), value);
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

    public final static class Text extends ValueConfigNode<String> {

        public Text(String value, Location location) {
            super(value, location);
        }
    }

    public sealed abstract static class NumberConfigNode extends ValueConfigNode<Number> permits ValueConfigNode.Int,
            ValueConfigNode.Long, ValueConfigNode.BigInteger, ValueConfigNode.Float, ValueConfigNode.Double,
            ValueConfigNode.BigDecimal {

        protected NumberConfigNode(Number value, Location location) {
            super(value, location);
        }
    }

    public final static class Int extends NumberConfigNode {

        public Int(Integer value, Location location) {
            super(value, location);
        }
    }

    public final static class Long extends NumberConfigNode {

        public Long(java.lang.Long value, Location location) {
            super(value, location);
        }
    }

    public final static class BigInteger extends NumberConfigNode {

        public BigInteger(java.math.BigInteger value, Location location) {
            super(value, location);
        }
    }

    public final static class Float extends NumberConfigNode {

        protected Float(java.lang.Float value, Location location) {
            super(value, location);
        }
    }

    public final static class Double extends NumberConfigNode {

        protected Double(java.lang.Double value, Location location) {
            super(value, location);
        }
    }

    public final static class BigDecimal extends NumberConfigNode {

        public BigDecimal(java.math.BigDecimal value, Location location) {
            super(value, location);
        }

    }

    public final static class Null extends ValueConfigNode {

        public Null(Location location) {
            super(null, location);
        }
    }

    public final static class Boolean extends ValueConfigNode<java.lang.Boolean> {

        public Boolean(java.lang.Boolean value, Location location) {
            super(value, location);
        }
    }

    public final static class Binary extends ValueConfigNode {

        public Binary(byte[] value, Location location) {
            super(value, location);
        }
    }
}
