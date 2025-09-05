package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlternativeChecker extends BaseNodeChecker {

    private final List<NodeChecker> alternatives;

    public AlternativeChecker(NodeChecker... alternatives) {
        this.alternatives = Arrays.asList(alternatives);
    }


    @Override
    public void check(Node node) throws SchemaValidationError {
        List<SingleSchemaValidationError> alternativeErrors = new ArrayList<>();
        for (NodeChecker alternative : alternatives) {
            try {
                alternative.check(node);
                return;
            } catch (SingleSchemaValidationError e) {
                alternativeErrors.add(e);
            }
        }
        throw new MultipleSchemaValidationError(node.getLocation(), "None of alternatives are valid",
                alternativeErrors);
    }

    @Override
    public String getName() {
        return "alternative";
    }

    @Override
    public List<NodeChecker> getChildren() {
        return alternatives;
    }

}
