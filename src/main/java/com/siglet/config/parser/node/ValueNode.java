package com.siglet.config.parser.node;

import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

public abstract sealed class ValueNode<T> extends Node {

    private final T value;

    private ValueTransformer valueTransformer;

    protected ValueNode(T value, Location location) {
        super(location);
        this.value = value;
    }


    public Object getValue() {
        Object result = value;
        if (valueTransformer != null) {
            result = valueTransformer.transform(value);
        }
        if (result instanceof Located located) {
            located.setLocation(getLocation());
        }

        return result;
    }

    public void setValueTransformer(ValueTransformer valueTransformer) {
        this.valueTransformer = valueTransformer;
    }

    public static final class TextNode extends ValueNode<String> {

        public TextNode(String value, Location location) {
            super(value, location);
        }


        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  text (" + getValue() + ")";
        }
    }

    public abstract static sealed class NumberNode extends ValueNode<Number> {

        protected NumberNode(Number value, Location location) {
            super(value, location);
        }
    }

    public static final class IntNode extends NumberNode {

        public IntNode(Integer value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  int (" + getValue() + ")";
        }
    }

    public static final class LongNode extends NumberNode {

        public LongNode(java.lang.Long value, Location location) {
            super(value, location);
        }


        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  long (" + getValue() + ")";
        }
    }

    public static final class BigIntegerNode extends NumberNode {

        public BigIntegerNode(java.math.BigInteger value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  bigInteger (" + getValue() + ")";
        }
    }

    public static final class FloatNode extends NumberNode {

        FloatNode(java.lang.Float value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  float (" + getValue() + ")";
        }
    }

    public static final class DoubleNode extends NumberNode {

        DoubleNode(java.lang.Double value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  double (" + getValue() + ")";
        }
    }

    public static final class BigDecimalNode extends NumberNode {

        public BigDecimalNode(java.math.BigDecimal value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  bigDecimal (" + getValue() + ")";
        }
    }

    public static final class NullNode extends ValueNode<Object> {

        public NullNode(Location location) {
            super(null, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  null";
        }
    }

    public static final class BooleanNode extends ValueNode<java.lang.Boolean> {

        public BooleanNode(java.lang.Boolean value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  boolean (" + getValue() + ")";
        }
    }

    public static final class BinaryNode extends ValueNode {

        public BinaryNode(byte[] value, Location location) {
            super(value, location);
        }

        @Override
        protected String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  byte[] (" + getValue() + ")";
        }
    }
}
