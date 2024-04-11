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

    public ConfigNode propertyPresenceCheck(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationError("is not a object", node.getLocation());
        }
        ConfigNode propNode = objectNode.get(getPropertyName());
        if (isRequired() && propNode == null) {
            throw new SingleSchemaValidationError("must have a " + getPropertyName()+ " property!", node.getLocation());
        }
        return propNode;
    }

    public ValueSetter getValueSetter() {
        return valueSetter;
    }
}
