package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlternativeChecker extends NodeChecker {

    private final List<NodeChecker> alternatives;

    public AlternativeChecker(NodeChecker... alternatives) {
        this.alternatives = List.of(alternatives);
    }


    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
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
