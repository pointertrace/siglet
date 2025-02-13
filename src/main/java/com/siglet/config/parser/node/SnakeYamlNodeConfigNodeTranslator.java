package com.siglet.config.parser.node;

import com.siglet.config.located.Location;
import org.yaml.snakeyaml.nodes.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SnakeYamlNodeConfigNodeTranslator {

    private SnakeYamlNodeConfigNodeTranslator() {

    }

    public static Node translate(org.yaml.snakeyaml.nodes.Node node) {

        return switch (node) {
            case ScalarNode scalarNode when Tag.INT.equals(scalarNode.getTag()) ->  //
                    getIntegerConfigNode(scalarNode);
            case ScalarNode scalarNode when Tag.FLOAT.equals(scalarNode.getTag()) ->  //
                    getDecimalConfigNode(scalarNode);
            case ScalarNode scalarNode when Tag.STR.equals(scalarNode.getTag()) ->
                    new ValueNode.TextNode(scalarNode.getValue(), Location.of(scalarNode));
            case ScalarNode scalarNode when Tag.BINARY.equals(scalarNode.getTag()) ->
                    new ValueNode.BinaryNode(scalarNode.getValue().getBytes(StandardCharsets.UTF_8),
                            Location.of(scalarNode));
            case ScalarNode scalarNode when Tag.BOOL.equals(scalarNode.getTag()) ->
                    new ValueNode.BooleanNode(Boolean.valueOf(scalarNode.getValue()),
                            Location.of(scalarNode));
            case ScalarNode scalarNode when Tag.NULL.equals(scalarNode.getTag()) ->
                    new ValueNode.NullNode(Location.of(scalarNode));
            case MappingNode mappingNode -> {
                List<ObjectNode.Property> properties = new ArrayList<>();
                mappingNode.getValue().forEach(nodeTuple -> {
                    ObjectNode.Key key = keyFromNode(nodeTuple.getKeyNode());
                    Node value = translate(nodeTuple.getValueNode());
                    if (value instanceof ObjectNode objectValue) {
                        objectValue.setLocation(key.getLocation());
                    }
                    if (value instanceof ArrayNode arrayNode) {
                        arrayNode.setLocation(key.getLocation());
                    }
                    ObjectNode.Property prop = new ObjectNode.Property(key, value);
                    properties.add(prop);

                });
                yield new ObjectNode(properties, Location.of(mappingNode));
            }
            case SequenceNode sequenceNode -> {
                List<Node> items = new ArrayList<>();
                sequenceNode.getValue().forEach(item -> items.add(translate(item)));
                yield new ArrayNode(items, Location.of(sequenceNode));
            }
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }

    protected static ObjectNode.Key keyFromNode(org.yaml.snakeyaml.nodes.Node propertyKeyNode) {
        if (!Tag.STR.equals(propertyKeyNode.getTag()) || !(propertyKeyNode instanceof ScalarNode scalarKeyNode)) {
            throw new SigletParserError("object must have a str as key",
                    Location.of(propertyKeyNode));
        }
        return new ObjectNode.Key(scalarKeyNode.getValue(), Location.of(scalarKeyNode));
    }

    protected static ValueNode.NumberNode getIntConfigNode(ScalarNode value) {
        try {
            return new ValueNode.IntNode(Integer.parseInt(value.getValue()), Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueNode.NumberNode getLongConfigNode(ScalarNode value) {
        try {
            return new ValueNode.LongNode(Long.parseLong(value.getValue()), Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueNode.NumberNode getBigIntegerConfigNode(ScalarNode value) {
        try {
            return new ValueNode.BigIntegerNode(new BigInteger(value.getValue()), Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueNode.NumberNode getIntegerConfigNode(ScalarNode value) {
        ValueNode.NumberNode result = getIntConfigNode(value);
        if (result == null) {
            result = getLongConfigNode(value);
        }
        if (result == null) {
            result = getBigIntegerConfigNode(value);
        }
        if (result == null) {
            throw new SigletParserError("'%s' is not a valid integer", Location.of(value));
        } else {
            return result;
        }
    }

    protected static ValueNode.NumberNode getFloatConfigNode(ScalarNode value) {
        try {
            Float f = Float.parseFloat(value.getValue());
            if (f.isInfinite()) {
                return null;
            } else {
                return new ValueNode.FloatNode(f, Location.of(value));
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueNode.NumberNode getDoubleConfigNode(ScalarNode value) {
        try {
            Double d = Double.parseDouble(value.getValue());
            if (d.isInfinite()) {
                return null;
            }
            return new ValueNode.DoubleNode(d, Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueNode.NumberNode getBigDecimalConfigNode(ScalarNode value) {
        try {
            return new ValueNode.BigDecimalNode(new BigDecimal(value.getValue()), Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueNode.NumberNode getDecimalConfigNode(ScalarNode value) {
        ValueNode.NumberNode result = getFloatConfigNode(value);
        if (result == null) {
            result = getDoubleConfigNode(value);
        }
        if (result == null) {
            result = getBigDecimalConfigNode(value);
        }
        if (result == null) {
            throw new SigletParserError("'%s' is not a valid decimal", Location.of(value));
        } else {
            return result;
        }
    }
}
