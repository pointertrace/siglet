package com.siglet.config.parser.node;

import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;

public abstract sealed class ValueConfigNode<T> extends ConfigNode  {

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

    @Override
    public ValueSetter getValueSetter() {
        return valueSetter;
    }

    @Override
    public void setValueSetter(ValueSetter valueSetter) {
        this.valueSetter = valueSetter;
    }

    public void setValueTransformer(ValueTransformer valueTransformer) {
        this.valueTransformer = valueTransformer;
    }

    public void clear() {
        setValueSetter(null);
    }

    public static final class Text extends ValueConfigNode<String> {

        public Text(String value, Location location) {
            super(value, location);
        }
    }

    public abstract static sealed class NumberConfigNode extends ValueConfigNode<Number>  {

        protected NumberConfigNode(Number value, Location location) {
            super(value, location);
        }
    }

    public static final class Int extends NumberConfigNode {

        public Int(Integer value, Location location) {
            super(value, location);
        }
    }

    public static final class Long extends NumberConfigNode {

        public Long(java.lang.Long value, Location location) {
            super(value, location);
        }
    }

    public static final class BigInteger extends NumberConfigNode {

        public BigInteger(java.math.BigInteger value, Location location) {
            super(value, location);
        }
    }

    public static final class Float extends NumberConfigNode {

        Float(java.lang.Float value, Location location) {
            super(value, location);
        }
    }

    public static final class Double extends NumberConfigNode {

        Double(java.lang.Double value, Location location) {
            super(value, location);
        }
    }

    public static final class BigDecimal extends NumberConfigNode {

        public BigDecimal(java.math.BigDecimal value, Location location) {
            super(value, location);
        }

    }

    public static final class Null extends ValueConfigNode {

        public Null(Location location) {
            super(null, location);
        }
    }

    public static final class Boolean extends ValueConfigNode<java.lang.Boolean> {

        public Boolean(java.lang.Boolean value, Location location) {
            super(value, location);
        }
    }

    public static final class Binary extends ValueConfigNode {

        public Binary(byte[] value, Location location) {
            super(value, location);
        }
    }
}
