package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.config.parser.node.ValueSetter;

public abstract class BasicPropertyChecker extends AbstractPropertyChecker {


    private final ValueSetter valueSetter;

    protected BasicPropertyChecker(ValueSetter valueSetter, String propertyName, boolean required) {
        super(propertyName, required);
        this.valueSetter = valueSetter;
    }

    @Override
    public ConfigNode propertyPresenceCheck(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a object");
        }
        ConfigNode propNode = objectNode.get(getPropertyName());
        if (isRequired() && propNode == null) {
            throw new SingleSchemaValidationError(node.getLocation(),"must have a " + getPropertyName()+ " property!");
        }
        return propNode;
    }

    public ValueSetter getValueSetter() {
        return valueSetter;
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
