package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.node.ArrayNode;
import io.github.pointertrace.siglet.parser.node.BaseNode;
import io.github.pointertrace.siglet.parser.node.ValueCreator;
import io.github.pointertrace.siglet.parser.node.ValueSetter;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ArrayChecker extends BaseNodeChecker {


    private final List<NodeChecker> checks;

    private ValueCreator arrayContainerValueCreator;

    private ValueSetter arrayContainerValueSetter;

    private ValueCreator arrayItemCreator;

    private ValueSetter arrayItemValueSetter;

    public ArrayChecker(NodeChecker... checks) {
        this.checks = Arrays.asList(checks);
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
        this.checks = Arrays.asList(checks);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ArrayNode)) {
            throw new SingleSchemaValidationError(node.getLocation(), "is not a array!");
        }
        ArrayNode arrayNode = (ArrayNode) node;
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
                BaseNode item = arrayNode.getItem(i);
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
