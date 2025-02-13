package com.siglet.config.parser.schema;

import com.siglet.config.located.Located;
import com.siglet.config.located.Location;
import com.siglet.config.parser.node.LocationSetter;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueSetter;

import java.util.List;
import java.util.function.BiConsumer;

public class PropertyChecker extends BasicPropertyChecker {

    private final List<NodeChecker> propertyChecks;

    public <T,E,R extends Located> PropertyChecker(BiConsumer<T, E> valueSetter, BiConsumer<R, Location> locationSetter,
                           String name, boolean required, NodeChecker... propertyChecks) {
        super(ValueSetter.of(valueSetter), LocationSetter.of(locationSetter), name, required);
        this.propertyChecks = List.of(propertyChecks);
    }

    public <T, E> PropertyChecker(BiConsumer<T, E> valueSetter, String name, boolean required, NodeChecker... propertyChecks) {
        super(ValueSetter.of(valueSetter), null, name, required);
        this.propertyChecks = List.of(propertyChecks);
    }

    public PropertyChecker(String name, boolean required, NodeChecker... propertyChecks) {
        super(ValueSetter.EMPTY, null, name, required);
        this.propertyChecks = List.of(propertyChecks);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        Node propertyNode = propertyPresenceCheck(node);
        try {
            if (propertyNode != null) {
                for (NodeChecker propCheck : propertyChecks) {
                    propCheck.check(propertyNode);
                }
                propertyNode.setValueSetter(getValueSetter());
                propertyNode.setLocationSetter(getLocationSetter());
            }
        } catch (SchemaValidationError e) {
            throw new SingleSchemaValidationError(node.getLocation(),
                    String.format("property %s is not valid", getPropertyName()), e);
        }
    }

    @Override
    public String getName() {
        return "property";
    }

    @Override
    public String getDescription() {
        return String.format("name:%s, required:%b", getPropertyName(), isRequired());
    }

    @Override
    public List<? extends NodeChecker> getChildren() {
        return propertyChecks;
    }
}
