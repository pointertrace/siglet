package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueSetter;

import java.util.List;
import java.util.function.BiConsumer;

public class PropertyChecker extends BasicPropertyChecker {

    private final List<NodeChecker> propertyChecks;

    public <T, E> PropertyChecker(BiConsumer<T, E> valueSetter, String name, boolean required, NodeChecker... propertyChecks) {
        super(ValueSetter.of(valueSetter), name, required);
        this.propertyChecks = List.of(propertyChecks);
    }

    public <T, E> PropertyChecker(String name, boolean required, NodeChecker... propertyChecks) {
        super(ValueSetter.EMPTY, name, required);
        this.propertyChecks = List.of(propertyChecks);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        ConfigNode propertyNode = propertyPresenceCheck(node);
        try {
            if (propertyNode != null) {
                for (NodeChecker propCheck : propertyChecks) {
                    propCheck.check(propertyNode);
                }
                propertyNode.setValueSetter(getValueSetter());
            }
        } catch (SingleSchemaValidationError e) {
            throw new SingleSchemaValidationError(String.format("property %s %s", getPropertyName(), e.getMessage()),
                    e.getLocation());
        }
    }

    @Override
    public String getName() {
        return  "property";
    }

}
