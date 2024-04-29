package com.siglet.config.parser.locatednode;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.databind.util.RawValue;

/**
 * Used to store references between nodes and JsonLocations. Otherwise, delegates to actual
 * JsonNodeFactory
 *
 * @author csmith
 */
public class LocatedJsonNodeFactory extends JsonNodeFactory {

    private JsonParser parser;

    public void setParser(JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public BooleanNode booleanNode(boolean v) {
        return new Located.LocatedBooleanNode(v, Location.of(parser));
    }

    @Override
    public NullNode nullNode() {
        return new Located.LocatedNullNode(Location.of(parser));
    }

    @Override
    public NumericNode numberNode(byte value) {
        return new Located.LocatedIntNode(value, Location.of(parser));
    }

    @Override
    public ValueNode numberNode(Byte value) {
        return new Located.LocatedIntNode(value, Location.of(parser));
    }

    @Override
    public NumericNode numberNode(short value) {
        return new Located.LocatedIntNode(value, Location.of(parser));
    }

    @Override
    public ValueNode numberNode(Short value) {
        return new Located.LocatedIntNode(value, Location.of(parser));
    }


    @Override
    public NumericNode numberNode(int value) {
        return new Located.LocatedIntNode(value, Location.of(parser));
    }

    @Override
    public ValueNode numberNode(Integer value) {
        return new Located.LocatedIntNode(value, Location.of(parser));
    }

    @Override
    public NumericNode numberNode(long value) {
        return new Located.LocatedLongNode(value, Location.of(parser));
    }

    @Override
    public ValueNode numberNode(Long value) {
        return new Located.LocatedLongNode(value, Location.of(parser));
    }

    @Override
    public ValueNode numberNode(BigInteger value) {
        return new Located.LocatedBigIntegerNode(value, Location.of(parser));
    }

    @Override
    public NumericNode numberNode(float value) {
        return new Located.LocatedBigDecimalNode(BigDecimal.valueOf(value), Location.of(parser));
    }

    @Override
    public ValueNode numberNode(Float value) {
        return new Located.LocatedBigDecimalNode(BigDecimal.valueOf(value), Location.of(parser));
    }

    @Override
    public NumericNode numberNode(double value) {
        return new Located.LocatedBigDecimalNode(BigDecimal.valueOf(value), Location.of(parser));
    }

    @Override
    public ValueNode numberNode(Double value) {
        return new Located.LocatedBigDecimalNode(BigDecimal.valueOf(value), Location.of(parser));
    }


    @Override
    public ValueNode numberNode(BigDecimal value) {
        return new Located.LocatedBigDecimalNode(value, Location.of(parser));
    }

    @Override
    public TextNode textNode(String text) {
        return new Located.LocatedTextNode(text, Location.of(parser));
    }


    @Override
    public BinaryNode binaryNode(byte[] value) {
        return new Located.LocatedBinaryNode(value, Location.of(parser));
    }

    @Override
    public BinaryNode binaryNode(byte[] value, int offset, int length) {
        return new Located.LocatedBinaryNode(value, offset, length, Location.of(parser));
    }


    @Override
    public ValueNode pojoNode(Object value) {
        throw new IllegalStateException("there must not be a pojo node!");
    }

    @Override
    public ValueNode rawValueNode(RawValue value) {
        throw new IllegalStateException("there must not be a raw node!");
    }

    @Override
    public ArrayNode arrayNode() {
        return new Located.LocatedArrayNode(this, Location.of(parser));
    }

    @Override
    public ArrayNode arrayNode(int capacity) {
        return new Located.LocatedArrayNode(this, capacity, Location.of(parser));
    }

    @Override
    public ObjectNode objectNode() {
        return new Located.LocatedObjectNode(this, Location.of(parser));
    }

}
