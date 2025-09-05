package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.node.ObjectNode;
import io.github.pointertrace.siglet.parser.node.ValueCreator;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ObjectChecker extends BaseNodeChecker {

    private final boolean strict;

    private final List<AbstractPropertyChecker> propertiesCheck;

    private final ValueCreator valueCreator;

    public ObjectChecker(Supplier<?> valueCreator, boolean strict, AbstractPropertyChecker... propertiesChecks) {
        this.valueCreator = ValueCreator.of(valueCreator);
        this.strict = strict;
        this.propertiesCheck = Arrays.asList(propertiesChecks);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"expecting an object!");
        }
        ObjectNode objectNode = (ObjectNode)  node;
        for (AbstractPropertyChecker propertyCheck : getPropertyCheckers()) {
            propertyCheck.check(node);
        }
        for (DynamicPropertyChecker propertyCheck : getDynamicPropertyCheckers()) {
            propertyCheck.check(node);
        }
        if (strict) {
            Set<String> propCheckNames = propertiesCheck.stream()
                    .flatMap(propertiesCheck -> propertiesCheck.getValidKeys().stream())
                    .collect(Collectors.toSet());
            Set<String> propNames = new TreeSet<>(objectNode.getPropertyNames());
            propNames.removeAll(propCheckNames);
            List<SingleSchemaValidationError> errors = new ArrayList<>();
            for (String propName : propNames) {
                errors.add(new SingleSchemaValidationError(
                        Location.of(objectNode.get(propName).getLocation().getLine(), 1),
                        String.format("property %s is not expected!", propName))
                );
            }
            if (errors.size() == 1) {
                throw errors.get(0);
            } else if (errors.size() > 1) {
                throw new MultipleSchemaValidationError(node.getLocation(), "Object is not valid", errors);

            }
        }
        objectNode.setValueCreator(valueCreator);
    }

    protected List<AbstractPropertyChecker> getPropertyCheckers() {
        return propertiesCheck.stream()
                .filter(p -> !(p instanceof DynamicPropertyChecker))
                .collect(Collectors.toList());
    }

    protected List<DynamicPropertyChecker> getDynamicPropertyCheckers() {
        return propertiesCheck.stream()
                .filter(DynamicPropertyChecker.class::isInstance)
                .map(DynamicPropertyChecker.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "object";
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public List<? extends BaseNodeChecker> getChildren() {
        return  propertiesCheck;
    }
}
