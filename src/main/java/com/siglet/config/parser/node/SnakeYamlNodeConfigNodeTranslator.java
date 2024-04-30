package com.siglet.config.parser.node;

import com.siglet.config.located.Location;
import org.yaml.snakeyaml.nodes.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SnakeYamlNodeConfigNodeTranslator {


    public static ConfigNode translate(Node node) {

        return switch (node) {
            case ScalarNode scalarNode when Tag.INT.equals(scalarNode.getTag()) ->  //
                    getIntegerConfigNode(scalarNode);
            case ScalarNode scalarNode when Tag.FLOAT.equals(scalarNode.getTag()) ->  //
                    getDecimalConfigNode(scalarNode);
            case ScalarNode scalarNode when Tag.STR.equals(scalarNode.getTag()) ->
                    new ValueConfigNode.Text(scalarNode.getValue(), Location.of(scalarNode));
            case ScalarNode scalarNode when Tag.BINARY.equals(scalarNode.getTag()) ->
                    new ValueConfigNode.Binary(scalarNode.getValue().getBytes(StandardCharsets.UTF_8),
                            Location.of(scalarNode));
            case ScalarNode scalarNode when Tag.BOOL.equals(scalarNode.getTag()) ->
                    new ValueConfigNode.Boolean(Boolean.valueOf(scalarNode.getValue()),
                            Location.of(scalarNode));
            case ScalarNode scalarNode when Tag.NULL.equals(scalarNode.getTag()) ->
                    new ValueConfigNode.Null(Location.of(scalarNode));
            case MappingNode mappingNode -> {
                List<ObjectConfigNode.Property> properties = new ArrayList<>();
                mappingNode.getValue().forEach(nodeTuple -> {
                    ObjectConfigNode.Key key = keyFromNode(nodeTuple.getKeyNode());
                    ConfigNode value = translate(nodeTuple.getValueNode());
                    if (value instanceof ObjectConfigNode objectValue) {
                        objectValue.setLocation(key.getLocation());
                    }
                    if (value instanceof ArrayConfigNode arrayConfigNode) {
                        arrayConfigNode.setLocation(key.getLocation());
                    }
                    ObjectConfigNode.Property prop = new ObjectConfigNode.Property(key, value);
                    properties.add(prop);

                });
                yield new ObjectConfigNode(properties, Location.of(mappingNode));
            }
            case SequenceNode sequenceNode -> {
                List<ConfigNode> items = new ArrayList<>();
                sequenceNode.getValue().forEach(item -> items.add(translate(item)));
                yield new ArrayConfigNode(items, Location.of(sequenceNode));
            }
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }

    protected static ObjectConfigNode.Key keyFromNode(Node propertyKeyNode) {
        if (!Tag.STR.equals(propertyKeyNode.getTag()) || !(propertyKeyNode instanceof ScalarNode scalarKeyNode)) {
            throw new SigletParserError("object must have a str as key",
                    Location.of(propertyKeyNode));
        }
        return new ObjectConfigNode.Key(scalarKeyNode.getValue(), Location.of(scalarKeyNode));
    }

    protected static ValueConfigNode.NumberConfigNode getIntConfigNode(ScalarNode value) {
        try {
            return new ValueConfigNode.Int(Integer.parseInt(value.getValue()), Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueConfigNode.NumberConfigNode getLongConfigNode(ScalarNode value) {
        try {
            return new ValueConfigNode.Long(Long.parseLong(value.getValue()), Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueConfigNode.NumberConfigNode getBigIntegerConfigNode(ScalarNode value) {
        try {
            return new ValueConfigNode.BigInteger(new BigInteger(value.getValue()), Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueConfigNode.NumberConfigNode getIntegerConfigNode(ScalarNode value) {
        ValueConfigNode.NumberConfigNode result = getIntConfigNode(value);
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

    protected static ValueConfigNode.NumberConfigNode getFloatConfigNode(ScalarNode value) {
        try {
            Float f = Float.parseFloat(value.getValue());
            if (f.isInfinite()) {
                return null;
            } else {
                return new ValueConfigNode.Float(f, Location.of(value));
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueConfigNode.NumberConfigNode getDoubleConfigNode(ScalarNode value) {
        try {
            Double d = Double.parseDouble(value.getValue());
            if (d.isInfinite()) {
                return null;
            }
            return new ValueConfigNode.Double(d, Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueConfigNode.NumberConfigNode getBigDecimalConfigNode(ScalarNode value) {
        try {
            return new ValueConfigNode.BigDecimal(new BigDecimal(value.getValue()), Location.of(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueConfigNode.NumberConfigNode getDecimalConfigNode(ScalarNode value) {
        ValueConfigNode.NumberConfigNode result = getFloatConfigNode(value);
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
