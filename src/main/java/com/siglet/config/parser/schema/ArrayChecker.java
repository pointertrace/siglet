package com.siglet.config.parser.schema;

import com.siglet.config.located.Location;
import com.siglet.config.parser.node.ArrayNode;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueCreator;
import com.siglet.config.parser.node.ValueSetter;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ArrayChecker extends NodeChecker {


    private final List<NodeChecker> checks;

    private ValueCreator arrayContainerValueCreator;

    private ValueSetter arrayContainerValueSetter;

    private ValueCreator arrayItemCreator;

    private ValueSetter arrayItemValueSetter;

    public ArrayChecker(NodeChecker... checks) {
        this.checks = List.of(checks);
    }

    public ArrayChecker(Supplier<?> arrayContainerValueCreator, BiConsumer<?, ?> arrayContainerValueSetter,
                        java.util.function.BiConsumer<?, Location> locationSetter,
                        Supplier<?> arrayItemCreator,
                        BiConsumer<?, ?> arrayItemValueSetter,
                        NodeChecker... checks) {
        this.arrayContainerValueCreator = ValueCreator.of(arrayContainerValueCreator);
        this.arrayContainerValueSetter = ValueSetter.of(arrayContainerValueSetter);
        this.arrayItemCreator = ValueCreator.of(arrayItemCreator);
        this.arrayItemValueSetter = ValueSetter.of(arrayItemValueSetter);
        this.checks = List.of(checks);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ArrayNode arrayNode)) {
            throw new SingleSchemaValidationError(node.getLocation(), "is not a array!");
        }
        if (arrayContainerValueCreator != null) {
            arrayNode.setArraycontainerValueCreator(arrayContainerValueCreator);
        }
        if (arrayContainerValueSetter != null) {
            arrayNode.setArrayContainerValueSetter(arrayContainerValueSetter);
        }
        if (arrayItemCreator != null) {
            arrayNode.setArrayItemCreator(arrayItemCreator);
        }
        if (arrayItemValueSetter != null) {
            arrayNode.setArrayItemValueSetter(arrayItemValueSetter);
        }
        try {
            for (int i = 0; i < arrayNode.getLength(); i++) {
                Node item = arrayNode.getItem(i);
                for (NodeChecker itemCheck : checks) {
                    itemCheck.check(item);
                }
            }
        } catch (SingleSchemaValidationError e) {
            throw new SingleSchemaValidationError(e.getLocation(), "array item is not valid", e);
        } catch (MultipleSchemaValidationError e) {
            throw new SingleSchemaValidationError(node.getLocation(), "array item is not valid", e);
        }
    }

    @Override
    public String getName() {
        return "array";
    }

    @Override
    public List<NodeChecker> getChildren() {
        return checks;
    }
}
