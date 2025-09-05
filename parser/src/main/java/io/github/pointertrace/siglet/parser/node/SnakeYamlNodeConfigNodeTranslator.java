package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.located.Location;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SnakeYamlNodeConfigNodeTranslator {

    private SnakeYamlNodeConfigNodeTranslator() {

    }

    public static BaseNode translate(org.yaml.snakeyaml.nodes.Node node) {

        if (node instanceof ScalarNode) {
            ScalarNode scalarNode = (ScalarNode) node;
            if (scalarNode.getTag() == Tag.INT) {
                return getIntegerConfigNode(scalarNode);
            } else if (scalarNode.getTag() == Tag.FLOAT) {
                return getDecimalConfigNode(scalarNode);
            } else if (scalarNode.getTag() == Tag.STR) {
                return new ValueNode.TextNode(scalarNode.getValue(), location(scalarNode));
            } else if (scalarNode.getTag() == Tag.BINARY) {
                return new ValueNode.BinaryNode(scalarNode.getValue().getBytes(StandardCharsets.UTF_8),
                        location(scalarNode));
            } else if (scalarNode.getTag() == Tag.BOOL) {
                return new ValueNode.BooleanNode(Boolean.valueOf(scalarNode.getValue()),
                        location(scalarNode));
            } else if (scalarNode.getTag() == Tag.NULL) {
                return new ValueNode.NullNode(location(scalarNode));
            } else {
                throw new IllegalStateException("Unexpected value: " + node);
            }
        } else if (node instanceof MappingNode) {
            MappingNode mappingNode = (MappingNode) node;
            List<ObjectNode.Property> properties = new ArrayList<>();
            mappingNode.getValue().forEach(nodeTuple -> {
                ObjectNode.Key key = keyFromNode(nodeTuple.getKeyNode());
                BaseNode value = translate(nodeTuple.getValueNode());
                if (value instanceof ObjectNode) {
                    value.setLocation(key.getLocation());
                }
                if (value instanceof ArrayNode) {
                    value.setLocation(key.getLocation());
                }
                ObjectNode.Property prop = new ObjectNode.Property(key, value);
                properties.add(prop);

            });
            return new ObjectNode(properties, location(mappingNode));
        } else if (node instanceof SequenceNode) {
            SequenceNode sequenceNode = (SequenceNode) node;
            List<BaseNode> items = new ArrayList<>();
            sequenceNode.getValue().forEach(item -> items.add(translate(item)));
            return new ArrayNode(items, location(sequenceNode));
        } else {
            throw new IllegalStateException("Unexpected value: " + node);
        }
    }

    protected static ObjectNode.Key keyFromNode(org.yaml.snakeyaml.nodes.Node propertyKeyNode) {
        if (!Tag.STR.equals(propertyKeyNode.getTag()) || !(propertyKeyNode instanceof ScalarNode)) {
            throw new SigletParserError("object must have a str as key",
                    location(propertyKeyNode));
        }
        ScalarNode scalarKeyNode = (ScalarNode)  propertyKeyNode;
        return new ObjectNode.Key(scalarKeyNode.getValue(), location(scalarKeyNode));
    }

    protected static ValueNode.NumberNode getIntConfigNode(ScalarNode value) {
        try {
            return new ValueNode.IntNode(Integer.parseInt(value.getValue()), location(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueNode.NumberNode getLongConfigNode(ScalarNode value) {
        try {
            return new ValueNode.LongNode(Long.parseLong(value.getValue()), location(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueNode.NumberNode getBigIntegerConfigNode(ScalarNode value) {
        try {
            return new ValueNode.BigIntegerNode(new BigInteger(value.getValue()), location(value));
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
            throw new SigletParserError("'%s' is not a valid integer", location(value));
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
                return new ValueNode.FloatNode(f, location(value));
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
            return new ValueNode.DoubleNode(d, location(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static ValueNode.NumberNode getBigDecimalConfigNode(ScalarNode value) {
        try {
            return new ValueNode.BigDecimalNode(new BigDecimal(value.getValue()), location(value));
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
            throw new SigletParserError("'%s' is not a valid decimal", location(value));
        } else {
            return result;
        }
    }


    private static Location location(org.yaml.snakeyaml.nodes.Node node) {
        return Location.of(node.getStartMark().getLine() + 1, node.getStartMark().getColumn() + 1);
    }

}
