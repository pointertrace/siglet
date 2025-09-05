package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.ValueTransformer;
import io.github.pointertrace.siglet.parser.ValueTransformerException;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;

public abstract class ValueNode<T> extends BaseNode {

    private final T value;

    private ValueTransformer valueTransformer;

    protected ValueNode(T value, Location location) {
        super(location);
        this.value = value;
    }


    public Object getValue() {
        Object result = value;
        if (valueTransformer != null) {
            try {
                result = valueTransformer.transform(value);
            } catch (ValueTransformerException | RuntimeException e) {
                throw new SigletParserError(String.format("Error transforming value: %s",e.getMessage()),getLocation());
            }
        }
        if (result instanceof Located) {
            Located located = (Located) result;
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
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  text (" + getValue() + ")";
        }
    }

    public abstract static class NumberNode extends ValueNode<Number> {

        protected NumberNode(Number value, Location location) {
            super(value, location);
        }
    }

    public static final class IntNode extends NumberNode {

        public IntNode(Integer value, Location location) {
            super(value, location);
        }

        @Override
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  int (" + getValue() + ")";
        }
    }

    public static final class LongNode extends NumberNode {

        public LongNode(java.lang.Long value, Location location) {
            super(value, location);
        }


        @Override
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  long (" + getValue() + ")";
        }
    }

    public static final class BigIntegerNode extends NumberNode {

        public BigIntegerNode(java.math.BigInteger value, Location location) {
            super(value, location);
        }

        @Override
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  bigInteger (" + getValue() + ")";
        }
    }

    public static final class FloatNode extends NumberNode {

        FloatNode(java.lang.Float value, Location location) {
            super(value, location);
        }

        @Override
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  float (" + getValue() + ")";
        }
    }

    public static final class DoubleNode extends NumberNode {

        DoubleNode(java.lang.Double value, Location location) {
            super(value, location);
        }

        @Override
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  double (" + getValue() + ")";
        }
    }

    public static final class BigDecimalNode extends NumberNode {

        public BigDecimalNode(java.math.BigDecimal value, Location location) {
            super(value, location);
        }

        @Override
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  bigDecimal (" + getValue() + ")";
        }
    }

    public static final class NullNode extends ValueNode<Object> {

        public NullNode(Location location) {
            super(null, location);
        }

        @Override
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  null";
        }
    }

    public static final class BooleanNode extends ValueNode<java.lang.Boolean> {

        public BooleanNode(java.lang.Boolean value, Location location) {
            super(value, location);
        }

        @Override
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  boolean (" + getValue() + ")";
        }
    }

    public static final class BinaryNode extends ValueNode {

        public BinaryNode(byte[] value, Location location) {
            super(value, location);
        }

        @Override
        public String describe(int level) {
            return getDescriptionPrefix(level) + getLocation().describe() + "  byte[] (" + getValue() + ")";
        }
    }
}
