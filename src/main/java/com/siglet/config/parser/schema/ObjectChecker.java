package com.siglet.config.parser.schema;

import com.siglet.config.item.Item;
import com.siglet.config.located.Location;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.config.parser.node.ValueCreator;
import com.siglet.utils.Joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ObjectChecker extends NodeChecker {

    private final boolean strict;

    private final List<AbstractPropertyChecker> propertiesCheck;

    private final ValueCreator valueCreator;

    public <T extends Item> ObjectChecker(Supplier<T> valueCreator, boolean strict, AbstractPropertyChecker... propertiesChecks) {
        this.valueCreator = ValueCreator.of(valueCreator);
        this.strict = strict;
        this.propertiesCheck = List.of(propertiesChecks);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"expecting an object!");
        }
        for (AbstractPropertyChecker propertyCheck : getPropertyCheckers()) {
            propertyCheck.check(node);
        }
        for (DynamicPropertyChecker propertyCheck : getDynamicPropertyCheckers()) {
            propertyCheck.check(node);
        }
        if (strict) {
            Set<String> propCheckNames = propertiesCheck.stream()
                    .map(AbstractPropertyChecker::getPropertyName)
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
                throw errors.getFirst();
            } else if (errors.size() > 1) {
                throw new MultipleSchemaValidationError(node.getLocation(), "Object is not valid", errors);

            }
        }
        objectNode.setValueCreator(valueCreator);
    }

    protected List<AbstractPropertyChecker> getPropertyCheckers() {
        return propertiesCheck.stream()
                .filter(p -> !(p instanceof DynamicPropertyChecker))
                .map(AbstractPropertyChecker.class::cast)
                .toList();
    }

    protected List<DynamicPropertyChecker> getDynamicPropertyCheckers() {
        return propertiesCheck.stream()
                .filter(DynamicPropertyChecker.class::isInstance)
                .map(DynamicPropertyChecker.class::cast)
                .toList();
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
    public List<? extends NodeChecker> getChildren() {
        return  propertiesCheck;
    }
}
