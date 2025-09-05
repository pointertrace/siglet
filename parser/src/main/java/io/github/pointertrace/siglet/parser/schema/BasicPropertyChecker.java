package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.node.LocationSetter;
import io.github.pointertrace.siglet.parser.node.BaseNode;
import io.github.pointertrace.siglet.parser.node.ObjectNode;
import io.github.pointertrace.siglet.parser.node.ValueSetter;

public abstract class BasicPropertyChecker extends AbstractPropertyChecker {


    private final ValueSetter valueSetter;

    private final LocationSetter locationSetter;

    protected BasicPropertyChecker(ValueSetter valueSetter, LocationSetter locationSetter, String propertyName, boolean required) {
        super(new SimplePropertyKeyChecker(propertyName), required);
        this.valueSetter = valueSetter;
        this.locationSetter = locationSetter;
    }

    @Override
    public BaseNode propertyPresenceCheck(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a object");
        }
        ObjectNode objectNode = (ObjectNode) node;
        BaseNode propNode = objectNode.get(getPropertyName());
        if (isRequired() && propNode == null) {
            throw new SingleSchemaValidationError(node.getLocation(),"must have a " + getPropertyName()+ " property!");
        }
        return propNode;
    }

    public ValueSetter getValueSetter() {
        return valueSetter;
    }

    public LocationSetter getLocationSetter() {
        return locationSetter;
    }

    @Override
    public String getName() {
        return "basic property";
    }

    @Override
    public String getDescription() {
        return String.format("name:%s, required:%b",getPropertyName(),isRequired());
    }
}
