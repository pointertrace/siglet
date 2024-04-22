package com.siglet.config.parser.node;

import com.siglet.config.parser.locatednode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocatedNodeConfigNodeTranslator {

    public static ConfigNode translate(Located located) {

        return switch (located) {
            case Located.LocatedBigDecimalNode n -> new ValueConfigNode.BigDecimal(n.decimalValue(), n.getLocation());
            case Located.LocatedBigIntegerNode n ->
                    new ValueConfigNode.BigInteger(n.bigIntegerValue(), n.getLocation());
            case Located.LocatedBinaryNode n -> new ValueConfigNode.Binary(n.binaryValue(), n.getLocation());
            case Located.LocatedBooleanNode n -> new ValueConfigNode.Boolean(n.booleanValue(), n.getLocation());
            case Located.LocatedIntNode n -> new ValueConfigNode.Int(n.intValue(), n.getLocation());
            case Located.LocatedLongNode n -> new ValueConfigNode.Long(n.longValue(), n.getLocation());
            case Located.LocatedTextNode n -> new ValueConfigNode.Text(n.textValue(), n.getLocation());
            case Located.LocatedNullNode n -> new ValueConfigNode.Null(n.getLocation());
            case Located.LocatedObjectNode n -> {
                Map<String, ConfigNode> children = new HashMap<>();
                n.fieldNames().forEachRemaining(field -> children.put(field, translate((Located) n.get(field))));
                yield new ObjectConfigNode(children, n.getLocation());
            }
            case Located.LocatedArrayNode n -> {
                List<ConfigNode> items = new ArrayList<>();
                n.forEach(item -> items.add(translate((Located) item)));
                yield new ArrayConfigNode(items, located.getLocation());
            }
            default -> throw new IllegalStateException("Unexpected value: " + located);
        };
    }

}
