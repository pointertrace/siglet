package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;

public class AlternativePropertyChecker extends AbstractPropertyChecker {

    private final AlternativeChecker alternativeChecker;

    public <T, E> AlternativePropertyChecker(String name, boolean required, AbstractPropertyChecker... propertyChecks) {
        super( name, required);
        this.alternativeChecker = new AlternativeChecker(propertyChecks);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationException {
        propertyPresenceCheck(node);
        alternativeChecker.check(node);
    }

    @Override
    public String getName() {
        return  "alternative property";
    }

}
