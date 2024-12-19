package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;

import java.util.List;

public class AlternativePropertyChecker extends AbstractPropertyChecker {

    private final AlternativeChecker alternativeChecker;

    public AlternativePropertyChecker(String name, boolean required, AbstractPropertyChecker... propertyChecks) {
        super( name, required);
        this.alternativeChecker = new AlternativeChecker(propertyChecks);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        propertyPresenceCheck(node);
        alternativeChecker.check(node);
    }

    @Override
    public String getName() {
        return  "alternative property";
    }

    @Override
    public List<NodeChecker> getChildren() {
        return List.of(alternativeChecker);
    }
}
