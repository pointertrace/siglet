package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.LocationSetter;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ObjectNode;
import com.siglet.config.parser.node.ValueSetter;

public abstract class BasicPropertyChecker extends AbstractPropertyChecker {


    private final ValueSetter valueSetter;

    private final LocationSetter locationSetter;

    protected BasicPropertyChecker(ValueSetter valueSetter, LocationSetter locationSetter, String propertyName, boolean required) {
        super(propertyName, required);
        this.valueSetter = valueSetter;
        this.locationSetter = locationSetter;
    }

    @Override
    public Node propertyPresenceCheck(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a object");
        }
        Node propNode = objectNode.get(getPropertyName());
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
