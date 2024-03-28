package com.siglet.config.parser.schema;

import com.siglet.config.parser.locatednode.Location;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.config.parser.node.ValueCreator;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ObjectChecker implements NodeChecker {

    private final boolean strict;

    private final List<BasicPropertyChecker> propertiesCheck;

    private final ValueCreator valueCreator;

    public <T> ObjectChecker(Supplier<T> valueCreator, boolean strict, BasicPropertyChecker... propertiesChecks) {
        this.valueCreator = ValueCreator.of(valueCreator);
        this.strict = strict;
        this.propertiesCheck = List.of(propertiesChecks);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationException {
        if (!(node instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationException("is not an Object", node.getLocation());
        }
        for (PropertyChecker propertyCheck : getPropertyCheckers()) {
            propertyCheck.check(node);
        }
        for (DynamicPropertyChecker propertyCheck : getDynamicPropertyCheckers()) {
            propertyCheck.check(node);
        }
        if (strict) {
            Set<String> propCheckNames = propertiesCheck.stream()
                    .map(BasicPropertyChecker::getPropertyName)
                    .collect(Collectors.toSet());
            Set<String> propNames = new TreeSet<>(objectNode.getPropertyNames());
            propNames.removeAll(propCheckNames);
            List<SingleSchemaValidationException> errors = new ArrayList<>();
            for (String propName : propNames) {
                errors.add(new SingleSchemaValidationException(
                        String.format("property %s not defined!", propName),
                        Location.create(objectNode.get(propName).getLocation().getLine(), 1)));
            }
            if (errors.size() == 1) {
                throw errors.getFirst();
            } else if (errors.size() > 1) {
                throw new MultipleSchemaValidationError(String.format("properties %s not defined!",
                        String.join(", ", propNames)), errors);

            }
        }
        objectNode.setValueCreator(valueCreator);
    }

    @Override
    public String getName() {
        return "object";
    }

    protected List<PropertyChecker> getPropertyCheckers() {
        return propertiesCheck.stream()
                .filter(PropertyChecker.class::isInstance)
                .map(PropertyChecker.class::cast)
                .toList();
    }

    protected List<DynamicPropertyChecker> getDynamicPropertyCheckers() {
        return propertiesCheck.stream()
                .filter(DynamicPropertyChecker.class::isInstance)
                .map(DynamicPropertyChecker.class::cast)
                .toList();
    }

}
