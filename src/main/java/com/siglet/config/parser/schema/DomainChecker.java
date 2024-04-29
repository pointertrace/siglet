package com.siglet.config.parser.schema;

import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.node.ConfigNode;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DomainChecker implements NodeChecker {

    private final Supplier<Collection<?>> domainProvider;

    public DomainChecker(Supplier<Collection<?>> domainProvider) {
        this.domainProvider = domainProvider;
    }


    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        Collection<?> domainValues = domainProvider.get();
        if (! (node.getValue() instanceof ValueItem<?> valueItem)) {
            throw new SingleSchemaValidationError("is not a String value", node.getLocation());
        }
        if (!(valueItem.getValue() instanceof String value)) {
            throw new SingleSchemaValidationError("is not a String value", node.getLocation());
        }
        if (!domainValues.contains(value)) {
            throw new SingleSchemaValidationError("must be in [" +
                    domainValues.stream().map(Object::toString).collect(Collectors.joining(", ") ) +"]",
                    node.getLocation());
        }
    }

    @Override
    public String getName() {
        return "domain";
    }
}
