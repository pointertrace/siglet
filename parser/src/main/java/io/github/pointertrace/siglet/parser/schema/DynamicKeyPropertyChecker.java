package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.node.BaseNode;
import io.github.pointertrace.siglet.parser.node.LocationSetter;
import io.github.pointertrace.siglet.parser.node.ObjectNode;
import io.github.pointertrace.siglet.parser.node.ValueSetter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class DynamicKeyPropertyChecker extends AbstractPropertyChecker {

    private final ValueSetter keySetter;

    private final LocationSetter keyLocationSetter;

    private final ValueSetter valueSetter;

    private final LocationSetter valueLocationSetter;

    private final NodeChecker propertyValueCheck;

    public <TK, EK, RK extends Located, TV, EV, RV extends Located> DynamicKeyPropertyChecker(
            BiConsumer<TK, EK> keySetter, BiConsumer<RK, Location> keyLocationSetter,
            BiConsumer<TV, EV> valueSetter, BiConsumer<RV, Location> valueLocationSetter,
            Set<String> propertyKeyDomain,
            boolean required, NodeChecker propertyValueCheck) {
        super(new DynamicPropertyKeyChecker(propertyKeyDomain), required);
        this.keySetter = ValueSetter.of(keySetter);
        this.keyLocationSetter = LocationSetter.of(keyLocationSetter);
        this.valueSetter = ValueSetter.of(valueSetter);
        this.valueLocationSetter = LocationSetter.of(valueLocationSetter);
        this.propertyValueCheck = propertyValueCheck;
    }

    @Override
    public String getName() {
        return "dynamic key";
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        BaseNode propertyNode = super.propertyPresenceCheck(node);
        try {
            if (propertyNode != null) {
                propertyValueCheck.check(propertyNode);

                propertyNode.setValueSetter(getValueSetter());
                propertyNode.setLocationSetter(getValueLocationSetter());
                ObjectNode objectNode = (ObjectNode) node;
                String name = getPropertyKey(objectNode);
                objectNode.addKeySetter(name, keySetter);
                objectNode.addKeyLocationSetter(name, keyLocationSetter);
            }
        } catch (SchemaValidationError e) {
            throw new SingleSchemaValidationError(node.getLocation(),
                    String.format("property %s is not valid", getPropertyName()), e);
        }

    }

    public ValueSetter getKeySetter() {
        return keySetter;
    }

    public LocationSetter getKeyLocationSetter() {
        return keyLocationSetter;
    }

    public ValueSetter getValueSetter() {
        return valueSetter;
    }

    public LocationSetter getValueLocationSetter() {
        return valueLocationSetter;
    }

    private String getPropertyKey(ObjectNode objectNode) {
        DynamicPropertyKeyChecker dynamicPropertyKeyChecker = (DynamicPropertyKeyChecker) getPropertyKeyChecker();
        Set<String> intersection = new HashSet<>(dynamicPropertyKeyChecker.getValidKeys());
        intersection.retainAll(objectNode.getPropertyNames());
        if (intersection.size() != 1) {
            throw new SingleSchemaValidationError(objectNode.getLocation(),
                    String.format("Expecting one of: %s but available keys are: %s",
                            String.join(", ", dynamicPropertyKeyChecker.getValidKeys()),
                            String.join(", ", objectNode.getPropertyNames())));
        } else {
            return intersection.iterator().next();
        }
    }

}
