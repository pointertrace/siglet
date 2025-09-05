package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DomainChecker extends BaseNodeChecker {

    private final Supplier<Collection<?>> domainProvider;

    public DomainChecker(Supplier<Collection<?>> domainProvider) {
        this.domainProvider = domainProvider;
    }


    @Override
    public void check(Node node) throws SchemaValidationError {
        Collection<?> domainValues = domainProvider.get();
        String value = node.getValue(String.class);
        if (!domainValues.contains(value)) {
            throw new SingleSchemaValidationError(node.getLocation(),"must be one of [" + domainValues() + "]!");
        }
    }

    @Override
    public String getName() {
        return "domain";
    }

    @Override
    public String getDescription() {
        return "values [" + domainValues() + "]";
    }

    private String domainValues() {
        return domainProvider.get().stream().map(Object::toString).collect(Collectors.joining(", "));
    }
}
