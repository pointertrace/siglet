package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.node.LocationSetter;
import io.github.pointertrace.siglet.parser.node.BaseNode;
import io.github.pointertrace.siglet.parser.node.ValueSetter;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class PropertyChecker extends BasicPropertyChecker {

    private final List<NodeChecker> propertyChecks;

    public <T,E,R extends Located> PropertyChecker(BiConsumer<T, E> valueSetter, BiConsumer<R, Location> locationSetter,
                           String name, boolean required, NodeChecker... propertyChecks) {
        super(ValueSetter.of(valueSetter), LocationSetter.of(locationSetter), name, required);
        this.propertyChecks = Arrays.asList(propertyChecks);
    }

    public <T, E> PropertyChecker(BiConsumer<T, E> valueSetter, String name, boolean required, NodeChecker... propertyChecks) {
        super(ValueSetter.of(valueSetter), null, name, required);
        this.propertyChecks = Arrays.asList(propertyChecks);
    }

    public PropertyChecker(String name, boolean required, NodeChecker... propertyChecks) {
        super(ValueSetter.EMPTY, null, name, required);
        this.propertyChecks = Arrays.asList(propertyChecks);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        BaseNode propertyNode = propertyPresenceCheck(node);
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
