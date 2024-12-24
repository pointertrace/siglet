package com.siglet.config.parser.node;

import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;

public abstract sealed class ValueNode<T> extends Node {

    private final T value;

    private ValueSetter valueSetter;

    private ValueTransformer valueTransformer;

    protected ValueNode(T value, Location location) {
        super(location);
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

    public static final class Text extends ValueNode<String> {

        public Text(String value, Location location) {
            super(value, location);
        }


        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  text (" + getValue().getValue() + ")";
        }
    }

    public abstract static sealed class NumberNode extends ValueNode<Number> {

        protected NumberNode(Number value, Location location) {
            super(value, location);
        }
    }

    public static final class Int extends NumberNode {

        public Int(Integer value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  int (" + getValue().getValue() + ")";
        }
    }

    public static final class Long extends NumberNode {

        public Long(java.lang.Long value, Location location) {
            super(value, location);
        }


        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  long (" + getValue().getValue() + ")";
        }
    }

    public static final class BigInteger extends NumberNode {

        public BigInteger(java.math.BigInteger value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  bigInteger (" + getValue().getValue() + ")";
        }
    }

    public static final class Float extends NumberNode {

        Float(java.lang.Float value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  float (" + getValue().getValue() + ")";
        }
    }

    public static final class Double extends NumberNode {

        Double(java.lang.Double value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  double (" + getValue().getValue() + ")";
        }
    }

    public static final class BigDecimal extends NumberNode {

        public BigDecimal(java.math.BigDecimal value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  bigDecimal (" + getValue().getValue() + ")";
        }
    }

    public static final class Null extends ValueNode<Object> {

        public Null(Location location) {
            super(null, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  null";
        }
    }

    public static final class Boolean extends ValueNode<java.lang.Boolean> {

        public Boolean(java.lang.Boolean value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  boolean (" + getValue().getValue() + ")";
        }
    }

    public static final class Binary extends ValueNode {

        public Binary(byte[] value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  byte[] (" + getValue().getValue() + ")";
        }
    }
}
