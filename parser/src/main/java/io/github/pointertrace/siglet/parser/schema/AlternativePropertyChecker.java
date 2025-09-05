package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;

import java.util.Arrays;
import java.util.List;

public class AlternativePropertyChecker extends AbstractPropertyChecker {

    private final AlternativeChecker alternativeChecker;

    public AlternativePropertyChecker(String name, boolean required, AbstractPropertyChecker... propertyChecks) {
        super(new SimplePropertyKeyChecker(name), required);
        this.alternativeChecker = new AlternativeChecker(propertyChecks);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        propertyPresenceCheck(node);
        alternativeChecker.check(node);
    }

    @Override
    public String getName() {
        return  "alternative property";
    }

    @Override
    public List<BaseNodeChecker> getChildren() {
        return Arrays.asList(alternativeChecker);
    }
}
